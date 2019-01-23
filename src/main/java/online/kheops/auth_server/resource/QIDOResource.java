package online.kheops.auth_server.resource;


import online.kheops.auth_server.NotAlbumScopeTypeException;
import online.kheops.auth_server.PACSAuthTokenBuilder;
import online.kheops.auth_server.album.AlbumForbiddenException;
import online.kheops.auth_server.album.AlbumNotFoundException;
import online.kheops.auth_server.KheopsPrincipalInterface;
import online.kheops.auth_server.album.BadQueryParametersException;
import online.kheops.auth_server.annotation.AlbumAccessSecured;
import online.kheops.auth_server.annotation.Secured;
import online.kheops.auth_server.annotation.UIDValidator;
import online.kheops.auth_server.marshaller.JSONAttributesListMarshaller;
import online.kheops.auth_server.series.Series;
import online.kheops.auth_server.study.StudyNotFoundException;
import online.kheops.auth_server.user.UserNotFoundException;
import online.kheops.auth_server.user.UserPermissionEnum;
import online.kheops.auth_server.util.PairListXTotalCount;
import online.kheops.auth_server.util.QIDOParams;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.json.JSONReader;
import org.dcm4che3.json.JSONWriter;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Context;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.*;
import static online.kheops.auth_server.sharing.Sending.availableSeriesUIDs;
import static online.kheops.auth_server.study.Studies.findAttributesByUserPKJOOQ;
import static online.kheops.auth_server.util.Consts.*;
import static online.kheops.auth_server.util.HttpHeaders.X_TOTAL_COUNT;
import static online.kheops.auth_server.util.JOOQTools.getDataSource;

@Path("/")
public class QIDOResource {

    private static final Logger LOG = Logger.getLogger(QIDOResource.class.getName());

    private static final Client CLIENT = ClientBuilder.newClient().register(JSONAttributesListMarshaller.class);

    @Context
    private UriInfo uriInfo;

    @Context
    ServletContext context;

    @Context
    private SecurityContext securityContext;

    @GET
    @Secured
    @AlbumAccessSecured
    @Path("studies")
    @Produces({"application/dicom+json;qs=1,multipart/related;type=\"application/dicom+xml\";qs=0.9,application/json;qs=0.8"})
    public Response getStudies(@QueryParam(ALBUM) String fromAlbumId,
                               @QueryParam(INBOX) Boolean fromInbox,
                               @QueryParam(QUERY_PARAMETER_OFFSET) Integer offset) {

        if (fromAlbumId != null && fromInbox != null) {
            return Response.status(BAD_REQUEST).entity("Use only {album} or {inbox} not both").build();
        }

        final KheopsPrincipalInterface kheopsPrincipal = ((KheopsPrincipalInterface)securityContext.getUserPrincipal());
        final long callingUserPk = kheopsPrincipal.getDBID();

        try {
            if(fromAlbumId != null && !kheopsPrincipal.hasAlbumPermission(UserPermissionEnum.READ_SERIES, fromAlbumId)) {
                return Response.status(FORBIDDEN).build();
            }
        } catch (AlbumNotFoundException e) {
            return Response.status(NOT_FOUND).entity(e.getMessage()).build();
        }

        if(fromInbox != null && !kheopsPrincipal.hasUserAccess()) {
            return Response.status(FORBIDDEN).build();
        }

        final PairListXTotalCount<Attributes> pair;
        final QIDOParams qidoParams;
        try (Connection connection = getDataSource().getConnection()) {
            qidoParams = new QIDOParams(kheopsPrincipal, uriInfo.getQueryParameters());
            pair = findAttributesByUserPKJOOQ(callingUserPk, qidoParams, connection);
            LOG.info("QueryParameters : " + uriInfo.getQueryParameters().toString());
        } catch (BadRequestException e) {
            LOG.log(Level.SEVERE, "Error 400 :", e);
            return Response.status(BAD_REQUEST).entity("The QIDO-RS Provider was unable to perform the query because the Service Provider cannot understand the query component. [" + e.getMessage() + "]").build();
        } catch (BadQueryParametersException e) {
            LOG.log(Level.INFO, e.getMessage(), e);
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        } catch (AlbumForbiddenException e) {
            return Response.status(FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while connecting to the database", e);
            return Response.status(INTERNAL_SERVER_ERROR).entity("Database Connection Error").build();
        }
        if(pair.getXTotalCount() == 0) {
            return Response.status(NO_CONTENT)
                    .header(X_TOTAL_COUNT, pair.getXTotalCount())
                    .build();
        }

        GenericEntity<List<Attributes>> genericAttributesList = new GenericEntity<List<Attributes>>(pair.getAttributesList()) {};
        Response.ResponseBuilder response = Response.ok(genericAttributesList)
                .header(X_TOTAL_COUNT, pair.getXTotalCount());

        final long remaining = pair.getXTotalCount() - (qidoParams.getOffset().orElse(0) + pair.getAttributesList().size());
        if ( remaining > 0) {
            // TODO fix {+service}
            response.header("Warning","Warning: 299 {+service}: There are "+ remaining +" additional results that can be requested");
        }

        final CacheControl cacheControl = new CacheControl();
        cacheControl.setPrivate(true);
        cacheControl.setNoCache(true);
        response.cacheControl(cacheControl);

        return response.build();
    }

    @GET
    @Secured
    @Path("studies/{StudyInstanceUID:([0-9]+[.])*[0-9]+}/series")
    @Produces({"application/dicom+json;qs=1,multipart/related;type=\"application/dicom+xml\";qs=0.9,application/json;qs=0.8"})
    public Response getSeries(@PathParam(StudyInstanceUID) @UIDValidator String studyInstanceUID,
                              @QueryParam(ALBUM) String fromAlbumId,
                              @QueryParam(INBOX) Boolean fromInbox,
                              @QueryParam(FAVORITE) Boolean favoriteFilter,
                              @QueryParam(QUERY_PARAMETER_OFFSET) Integer offset,
                              @QueryParam(QUERY_PARAMETER_LIMIT) Integer limit) {

        if (fromAlbumId != null && fromInbox != null) {
            return Response.status(BAD_REQUEST).entity("Use only {album} or {inbox} not both").build();
        }

        final boolean includeFieldFavorite;
        if(uriInfo.getQueryParameters().containsKey("includefield") && uriInfo.getQueryParameters().get("includefield").contains("12345")) {
            includeFieldFavorite = true;
        } else {
            includeFieldFavorite = false;
        }

        if(fromAlbumId == null && fromInbox == null) {
            if(includeFieldFavorite) {
                return Response.status(BAD_REQUEST).entity("If include field favorite(0x0001,2345), you must specify "+INBOX+"=true OR "+ALBUM+"=XX as query param").build();
            }
            if(favoriteFilter != null) {
                return Response.status(BAD_REQUEST).entity("If favorite is set, you must specify "+INBOX+"=true OR "+ALBUM+"=XX as query param").build();
            }
        }


        fromInbox = fromInbox != null;

        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = Integer.MAX_VALUE;
        }

        final KheopsPrincipalInterface kheopsPrincipal = ((KheopsPrincipalInterface)securityContext.getUserPrincipal());
        final long callingUserPk = kheopsPrincipal.getDBID();

        if (!kheopsPrincipal.hasStudyReadAccess(studyInstanceUID)) {
            return Response.status(NOT_FOUND).build();
        }

        //BEGIN kheopsPrincipal
        if (fromInbox && !kheopsPrincipal.hasUserAccess()) {
            return Response.status(FORBIDDEN).build();
        }

        try {
            if (fromAlbumId != null && fromAlbumId != kheopsPrincipal.getAlbumID()) {
                return Response.status(FORBIDDEN).build();
            } else if (fromAlbumId == null) {
                fromAlbumId = kheopsPrincipal.getAlbumID();
            }
        } catch (NotAlbumScopeTypeException e) { /*empty*/ }
        //END kheopsPrincipal

        final MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.putAll(uriInfo.getQueryParameters());
        queryParameters.remove(ALBUM);
        queryParameters.remove(INBOX);
        queryParameters.remove(QUERY_PARAMETER_OFFSET);
        queryParameters.remove(QUERY_PARAMETER_LIMIT);
        queryParameters.remove(FAVORITE);

        URI uri = UriBuilder.fromUri(getDicomWebURI()).path("studies/{StudyInstanceUID}/series").build(studyInstanceUID);
        String authToken = PACSAuthTokenBuilder.newBuilder().withStudyUID(studyInstanceUID).withAllSeries().build();

        WebTarget webTarget = CLIENT.target(uri);

        for (String parameter: queryParameters.keySet()) {
            webTarget = webTarget.queryParam(parameter, queryParameters.get(parameter).toArray());
        }

        final Set<String> availableSeriesUIDs;
        try {
            availableSeriesUIDs = availableSeriesUIDs(callingUserPk, studyInstanceUID, fromAlbumId, fromInbox);
        } catch (UserNotFoundException | AlbumNotFoundException | StudyNotFoundException e) {
            return Response.status(NOT_FOUND).entity(e.getMessage()).build();
        }

        if (availableSeriesUIDs.isEmpty()) {
            return Response.status(NO_CONTENT)
                    .header(X_TOTAL_COUNT, 0)
                    .build();
        }

        final List<Attributes> allSeries;
        try {
            allSeries = webTarget.request("application/dicom+json")
                    .header("Authorization", "Bearer " + authToken)
                    .get(new GenericType<List<Attributes>>() {});
        } catch (WebApplicationException | ProcessingException e) {
            LOG.log(Level.SEVERE, "Error getting the list of series from the DCM server", e);
            return Response.status(BAD_GATEWAY).build();
        }

        List<Attributes> availableSeries = new ArrayList<>();

        int skipped = 0;
        int totalAvailableSeries = 0;
        boolean favoriteValue = false;
        for (Attributes series: allSeries) {
            String seriesInstanceUID = series.getString(Tag.SeriesInstanceUID);
            if (availableSeriesUIDs.contains(seriesInstanceUID)) {
                totalAvailableSeries++;
                if(favoriteFilter != null || includeFieldFavorite) {
                    if(fromInbox) {
                        favoriteValue = Series.isFavorite(seriesInstanceUID, kheopsPrincipal.getUser());
                    } else {
                        favoriteValue = Series.isFavorite(seriesInstanceUID, fromAlbumId);
                    }
                }

                if (skipped >= offset) {
                    if(!(favoriteFilter != null && favoriteValue != favoriteFilter)) {
                        if (availableSeries.size() < limit) {
                            if (includeFieldFavorite) {
                                series.setString(0x00012345, VR.SH, String.valueOf(favoriteValue));
                            }
                            availableSeries.add(series);
                        }
                    }
                } else {
                    skipped++;
                }
            }
        }
        GenericEntity<List<Attributes>> genericAvailableSeries = new GenericEntity<List<Attributes>>(availableSeries) {};

        Response.ResponseBuilder responseBuilder = Response.ok(genericAvailableSeries)
                .header(X_TOTAL_COUNT, totalAvailableSeries);

        if (totalAvailableSeries > availableSeries.size() + offset) {
            int remaining = totalAvailableSeries - (offset + availableSeries.size());
            // TODO fix {+service}
            responseBuilder.header("Warning", "299 {+service}: There are " + remaining + " additional results that can be requested");
        }

        return responseBuilder.build();
    }

    @GET
    @Secured
    @Path("studies/{StudyInstanceUID:([0-9]+[.])*[0-9]+}/metadata")
    @Produces("application/dicom+json;qs=1,application/json;qs=0.9")
    public Response getStudiesMetadata(@PathParam(StudyInstanceUID) @UIDValidator String studyInstanceUID,
                                       @QueryParam(ALBUM) String fromAlbumId,
                                       @QueryParam(INBOX) Boolean fromInbox) {

        if ((fromAlbumId != null && fromInbox != null)) {
            return Response.status(BAD_REQUEST).entity("Use only {album} or {inbox} not both").build();
        }

        fromInbox = fromInbox != null;

        final KheopsPrincipalInterface kheopsPrincipal = ((KheopsPrincipalInterface)securityContext.getUserPrincipal());
        final long callingUserPk = kheopsPrincipal.getDBID();

        if (!kheopsPrincipal.hasStudyReadAccess(studyInstanceUID)) {
            return Response.status(NOT_FOUND).build();
        }

        //BEGIN kheopsPrincipal
        if (fromInbox && !kheopsPrincipal.hasUserAccess()) {
            return Response.status(FORBIDDEN).build();
        }

        try {
            if (fromAlbumId != null && fromAlbumId != kheopsPrincipal.getAlbumID()) {
                return Response.status(FORBIDDEN).build();
            } else if (fromAlbumId == null) {
                fromAlbumId = kheopsPrincipal.getAlbumID();
            }
        } catch (NotAlbumScopeTypeException e) { /*empty*/ }
        //END kheopsPrincipal

        URI uri = UriBuilder.fromUri(getDicomWebURI()).path("studies/{StudyInstanceUID}/metadata").build(studyInstanceUID);
        String authToken = PACSAuthTokenBuilder.newBuilder().withStudyUID(studyInstanceUID).withAllSeries().build();
        InputStream is = CLIENT.target(uri).request("application/dicom+json").header("Authorization", "Bearer "+authToken).get(InputStream.class);

        JsonParser parser = Json.createParser(is);
        JSONReader jsonReader = new JSONReader(parser);

        final Set<String> availableSeriesUIDs;
        try {
            availableSeriesUIDs = availableSeriesUIDs(callingUserPk, studyInstanceUID, fromAlbumId, fromInbox);
        } catch (UserNotFoundException | AlbumNotFoundException | StudyNotFoundException e) {
            return Response.status(NOT_FOUND).entity(e.getMessage()).build();
        }

        final StreamingOutput stream = os -> {
            final JsonGenerator generator = Json.createGenerator(os);
            final JSONWriter jsonWriter = new JSONWriter(generator);

            generator.writeStartArray();

            try {
                jsonReader.readDatasets((fmi, dataset) -> {
                    if (availableSeriesUIDs.contains(dataset.getString(Tag.SeriesInstanceUID))) {
                        jsonWriter.write(dataset);
                    }
                });
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error while processing metadata", e);
                throw new WebApplicationException(e);
            }

            generator.writeEnd();
            generator.flush();
        };

        return Response.ok(stream).build();
    }

    private URI getDicomWebURI() {
        try {
            return new URI(context.getInitParameter("online.kheops.pacs.uri"));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("online.kheops.pacs.uri is not a valid URI", e);
        }
    }

}

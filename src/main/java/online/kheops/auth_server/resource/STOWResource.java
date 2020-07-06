package online.kheops.auth_server.resource;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.NotAlbumScopeTypeException;
import online.kheops.auth_server.album.AlbumNotFoundException;
import online.kheops.auth_server.annotation.Secured;
import online.kheops.auth_server.annotation.UIDValidator;
import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.instances.InstancesNotFoundException;
import online.kheops.auth_server.principal.KheopsPrincipal;
import online.kheops.auth_server.series.SeriesNotFoundException;
import online.kheops.auth_server.study.StudyNotFoundException;
import online.kheops.auth_server.user.AlbumUserPermissions;
import online.kheops.auth_server.util.ErrorResponse;
import online.kheops.auth_server.webhook.FooHashMap;
import online.kheops.auth_server.webhook.Source;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.time.Instant;

import static javax.ws.rs.core.Response.Status.*;
import static online.kheops.auth_server.album.Albums.getAlbum;
import static online.kheops.auth_server.album.AlbumsSeries.getAlbumSeries;
import static online.kheops.auth_server.instances.Instances.getInstances;
import static online.kheops.auth_server.instances.Instances.instancesExist;
import static online.kheops.auth_server.report_provider.ReportProviderQueries.getReportProviderWithClientId;
import static online.kheops.auth_server.series.Series.getSeries;
import static online.kheops.auth_server.series.Series.seriesExist;
import static online.kheops.auth_server.study.Studies.getStudy;
import static online.kheops.auth_server.study.Studies.studyExist;
import static online.kheops.auth_server.user.Users.getUser;
import static online.kheops.auth_server.util.Consts.*;
import static online.kheops.auth_server.util.ErrorResponse.Message.*;

@Path("/")
public class STOWResource {
    
    @Context
    private SecurityContext securityContext;

    @Context
    private ServletContext context;

    @POST
    @Secured
    @Path("stow")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getStudies(
            @FormParam(ALBUM) String albumId,

            @FormParam("studyInstanceUID") @UIDValidator String studyInstanceUID,
            @FormParam("studyDate") String studyDate,
            @FormParam("studyTime") String studyTime,
            @FormParam("studyDescription") String studyDescription,
            @FormParam("timzoneOffsetFromUtc") String timzoneOffsetFromUtc,
            @FormParam("accessionNumber") String accessionNumber,
            @FormParam("referringPhysicianName") String referringPhysicianName,
            @FormParam("patientName") String patientName,
            @FormParam("patientId") String patientId,
            @FormParam("patientBirthDate") String patientBirthDate,
            @FormParam("patientSex") String patientSex,
            @FormParam("studyId") String studyId,

            @FormParam(SeriesInstanceUID) @UIDValidator String seriesInstanceUID,
            @FormParam("modality") String modality,
            @FormParam("seriesDescription") String seriesDescription,
            @FormParam("seriesNumber") int seriesNumber,
            @FormParam("bodyPartExamined") String bodyPartExamined,

            @FormParam("instancesUID") @UIDValidator String instancesUID)
            throws SeriesNotFoundException, StudyNotFoundException, AlbumNotFoundException {

        KheopsPrincipal kheopsPrincipal = (KheopsPrincipal) securityContext.getUserPrincipal();

        //si la destination est un album avons nous les droits d'écriture ?
        //si les droit sont limité à un album, avons-nous les droit d'écriture dessus ?
        //si on demande à écrire dans un album, et que nous avons un token limité à un album, est-il le même ?
        if (albumId == null) {
            try {
                albumId = kheopsPrincipal.getAlbumID();
            } catch (NotAlbumScopeTypeException | AlbumNotFoundException e) { /*empty*/ }
        }

        if (albumId != null) {
            if (!kheopsPrincipal.hasAlbumAccess(albumId)) {
                final ErrorResponse errorResponse = new ErrorResponse.ErrorResponseBuilder()
                        .message(ALBUM_NOT_FOUND)
                        .detail("The album does not exist or you don't have access")
                        .build();
                return Response.status(NOT_FOUND).entity(errorResponse).build();
            }
            if (!kheopsPrincipal.hasAlbumPermission(AlbumUserPermissions.ADD_SERIES, albumId)) {
                final ErrorResponse errorResponse = new ErrorResponse.ErrorResponseBuilder()
                        .message(AUTHORIZATION_ERROR)
                        .detail("You don't have the permission to add serries in this album")
                        .build();
                return Response.status(UNAUTHORIZED).entity(errorResponse).build();
            }
        }

        //avons nous le droit d'ajouter la séries en question ?
        if (!kheopsPrincipal.hasSeriesAddAccess(studyInstanceUID, seriesInstanceUID)) {
            return Response.status(UNAUTHORIZED).build();
        }

        //if instances exist
           //non => création(study? + series? + instance) + ajout dans la destination(si existe pas encore) (gestion de mutation / webhook)
           //oui => les infos sont-elles 100% identique avec ce qui est déjà connu ?
               //non => action non autorisée
               //oui => gestion de mutation / webhook ==> return already exist


        String destinationId = null;
        boolean isNewStudy = false;
        boolean isNewSeries = false;
        boolean isNewInstance = false;
        Source source = new Source();
        boolean isNewInDestination = false;

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            final Study study;
            final Series series;
            final Instances instances;

            if (instancesExist(instancesUID, em)) {
                //obtenir et comparer (study series)
                series = getSeries(studyInstanceUID, seriesInstanceUID, em);
                study = getStudy(studyInstanceUID, em);
                if (compareSeries(series, modality, seriesDescription, seriesNumber, bodyPartExamined, timzoneOffsetFromUtc, studyInstanceUID) &&
                        compareStudy(study, studyDate, studyTime, studyDescription, timzoneOffsetFromUtc, accessionNumber, referringPhysicianName, patientName, patientId, patientBirthDate, patientSex, studyId)) {
                    try {
                        instances = getInstances(instancesUID, em);
                    } catch (InstancesNotFoundException e) {
                        //error
                        return Response.status(BAD_REQUEST).build();
                    }
                } else {
                    //error
                    return Response.status(BAD_REQUEST).build();
                }
            } else if (seriesExist(studyInstanceUID, seriesInstanceUID, em)) {
                //obtenir et comparer (study series)
                series = getSeries(studyInstanceUID, seriesInstanceUID, em);
                study = getStudy(studyInstanceUID, em);
                if (compareSeries(series, modality, seriesDescription, seriesNumber, bodyPartExamined, timzoneOffsetFromUtc, studyInstanceUID) &&
                        compareStudy(study, studyDate, studyTime, studyDescription, timzoneOffsetFromUtc, accessionNumber, referringPhysicianName, patientName, patientId, patientBirthDate, patientSex, studyId)) {
                    //créer instances
                    instances = new Instances(instancesUID, series);
                    isNewInstance = true;
                    em.persist(instances);
                } else {
                    //error
                    return Response.status(BAD_REQUEST).build();
                }
            } else if (studyExist(studyInstanceUID, em)) {
                //obtenir et comparer (study)
                study = getStudy(studyInstanceUID, em);
                if (compareStudy(study, studyDate, studyTime, studyDescription, timzoneOffsetFromUtc, accessionNumber, referringPhysicianName, patientName, patientId, patientBirthDate, patientSex, studyId)) {
                    //créer instances + series
                    series = new Series(seriesInstanceUID, study);
                    series.setModality(modality);
                    series.setBodyPartExamined(bodyPartExamined);
                    series.setSeriesDescription(seriesDescription);
                    series.setSeriesNumber(seriesNumber);
                    series.setTimezoneOffsetFromUTC(timzoneOffsetFromUtc);

                    instances = new Instances(instancesUID, series);
                    isNewSeries = true;
                    isNewInstance = true;
                    em.persist(series);
                    em.persist(instances);
                } else {
                    //error
                    return Response.status(BAD_REQUEST).build();
                }
            } else {
                study = new Study(studyInstanceUID);
                study.setStudyDescription(studyDescription);
                study.setAccessionNumber(accessionNumber);
                study.setPatientBirthDate(patientBirthDate);
                study.setPatientName(patientName);
                study.setPatientID(patientId);
                study.setPatientSex(patientSex);
                study.setReferringPhysicianName(referringPhysicianName);
                study.setStudyDate(studyDate);
                study.setStudyTime(studyTime);
                study.setTimezoneOffsetFromUTC(timzoneOffsetFromUtc);
                study.setStudyID(studyId);
                series = new Series(seriesInstanceUID, study);
                series.setModality(modality);
                series.setBodyPartExamined(bodyPartExamined);
                series.setSeriesDescription(seriesDescription);
                series.setSeriesNumber(seriesNumber);
                series.setTimezoneOffsetFromUTC(timzoneOffsetFromUtc);
                instances = new Instances(instancesUID, series);
                isNewStudy = true;
                isNewSeries = true;
                isNewInstance = true;
                em.persist(study);
                em.persist(series);
                em.persist(instances);
            }

            //add series in destination if not present
            final Album destination;
            Album destinationHashMap = null;
            if (albumId == null) {
                destination = kheopsPrincipal.getUser().getInbox();
            } else {
                destination = getAlbum(albumId, em);
                destinationId = destination.getId();
                destinationHashMap = destination;
            }
            try {
                getAlbumSeries(destination, series, em);
            } catch (NoResultException e) {
                AlbumSeries albumSeries = new AlbumSeries(destination, series);
                isNewInDestination = true;
                em.persist(albumSeries);
            }

            tx.commit();

            //Webhook
            source.setUser(kheopsPrincipal.getUser());
            kheopsPrincipal.getCapability().ifPresent(capability -> source.setCapabilityToken(capability));
            kheopsPrincipal.getClientId().ifPresent(clienrtId -> source.setReportProviderClientId(getReportProviderWithClientId(clienrtId, em)));
            FooHashMap.getInstance().addHashMapData(study, series, instances, destinationHashMap, isNewStudy, isNewSeries, isNewInstance, source, isNewInDestination);
            FooHashMap.getInstance().setKheopsInstance(context.getInitParameter(HOST_ROOT_PARAMETER));
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }

        //LOG


        return Response.ok().build();
    }

    private static boolean compareSeries(Series series, String modality, String seriesDescription, int seriesNumber,
                                         String bodyPartExamined, String timzoneOffsetFromUtc, String studyInstanceUID) {
        return series.getModality().compareTo(modality) == 0 &&
                series.getSeriesDescription().compareTo(seriesDescription) == 0 &&
                series.getSeriesNumber() == seriesNumber &&
                series.getBodyPartExamined().compareTo(bodyPartExamined) == 0 &&
                series.getTimezoneOffsetFromUTC().compareTo(timzoneOffsetFromUtc) == 0 &&
                series.getStudy().getStudyInstanceUID().compareTo(studyInstanceUID) == 0;
    }

    private static boolean compareStudy(Study study, String studyDate, String studyTime, String studyDescription,
                                        String timzoneOffsetFromUtc, String accessionNumber,
                                        String referringPhysicianName, String patientName, String patientId,
                                        String patientBirthDate, String patientSex, String studyId) {
        return study.getStudyDate().compareTo(studyDate) == 0 &&
                study.getStudyTime().compareTo(studyTime) == 0 &&
                study.getStudyDescription().compareTo(studyDescription) == 0 &&
                study.getTimezoneOffsetFromUTC().compareTo(timzoneOffsetFromUtc) == 0 &&
                study.getAccessionNumber().compareTo(accessionNumber) == 0 &&
                study.getReferringPhysicianName().compareTo(referringPhysicianName) == 0 &&
                study.getPatientName().compareTo(patientName) == 0 &&
                study.getPatientID().compareTo(patientId) == 0 &&
                study.getPatientBirthDate().compareTo(patientBirthDate) == 0 &&
                study.getPatientSex().compareTo(patientSex) == 0 &&
                study.getStudyID().compareTo(studyId) == 0;
    }
}

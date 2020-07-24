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
import online.kheops.auth_server.util.KheopsLogBuilder;
import online.kheops.auth_server.webhook.FooHashMap;
import online.kheops.auth_server.webhook.Source;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.Response.Status.*;
import static online.kheops.auth_server.album.Albums.getAlbum;
import static online.kheops.auth_server.album.AlbumsSeries.getAlbumSeries;
import static online.kheops.auth_server.instances.Instances.getInstances;
import static online.kheops.auth_server.report_provider.ReportProviderQueries.getReportProviderWithClientId;
import static online.kheops.auth_server.series.Series.getSeries;
import static online.kheops.auth_server.study.Studies.getStudy;
import static online.kheops.auth_server.util.Consts.*;
import static online.kheops.auth_server.util.ErrorResponse.Message.*;

@Path("/")
public class STOWResource {

    @Context
    private SecurityContext securityContext;

    @Context
    private ServletContext context;

    private class StudyParam {
        String studyInstanceUID;
        String studyDate;
        String studyTime;
        String studyDescription;
        String timzoneOffsetFromUtc;
        String accessionNumber;
        String referringPhysicianName;
        String patientName;
        String patientId;
        String patientBirthDate;
        String patientSex;
        String studyId;
    }

    private class SeriesParam {
        String seriesInstanceUID;
        String studyInstanceUID;
        String modality;
        String seriesDescription;
        int seriesNumber;
        String bodyPartExamined;
        String timzoneOffsetFromUtc;

    }

    @POST
    @Secured
    @Path("stow")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
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

            @FormParam("seriesInstanceUID") @UIDValidator String seriesInstanceUID,
            @FormParam("modality") String modality,
            @FormParam("seriesDescription") String seriesDescription,
            @FormParam("seriesNumber") int seriesNumber,
            @FormParam("bodyPartExamined") String bodyPartExamined,

            @FormParam("instancesUID") @UIDValidator String instancesUID)
            throws AlbumNotFoundException {

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

        StudyParam studyParam = new StudyParam();
        studyParam.studyInstanceUID = studyInstanceUID;
        studyParam.studyDate = studyDate;
        studyParam.studyTime = studyTime;
        studyParam.studyDescription = studyDescription;
        studyParam.timzoneOffsetFromUtc = timzoneOffsetFromUtc;
        studyParam.accessionNumber = accessionNumber;
        studyParam.referringPhysicianName = referringPhysicianName;
        studyParam.patientName = patientName;
        studyParam.patientId = patientId;
        studyParam.patientBirthDate = patientBirthDate;
        studyParam.patientSex = patientSex;
        studyParam.studyId = studyId;

        SeriesParam seriesParam = new SeriesParam();
        seriesParam.bodyPartExamined = bodyPartExamined;
        seriesParam.modality = modality;
        seriesParam.seriesDescription = seriesDescription;
        seriesParam.seriesInstanceUID = seriesInstanceUID;
        seriesParam.timzoneOffsetFromUtc = timzoneOffsetFromUtc;
        seriesParam.studyInstanceUID = studyInstanceUID;

        boolean isNewStudy = false;
        boolean isNewSeries = false;
        boolean isNewInstance = false;
        boolean isNewInDestination = false;

        final Study study;
        final Series series;
        final Instances instance;

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        final GetOrCreateStudyResult getOrCreateStudyResult = getOrCreateStudy(studyParam, tx, em);
        study = getOrCreateStudyResult.getStudy();
        isNewStudy = getOrCreateStudyResult.isNewStudy();

        final GetOrCreateSeriesResult getOrCreateSeriesResult = getOrCreateSeries(seriesParam, study, tx, em);
        series = getOrCreateSeriesResult.getSeries();
        isNewSeries = getOrCreateSeriesResult.isNewSeries();

        final GetOrCreateInstanceResult getOrCreateInstanceResult = getOrCreateInstance(instancesUID, series, tx, em);
        instance = getOrCreateInstanceResult.getInstance();
        isNewInstance = getOrCreateInstanceResult.isNewInstance();


        if (!isNewSeries && !compareSeries(series, seriesParam)) {
            try {
                tx.begin();
                if (isNewInstance) {
                    em.remove(instance);
                }
                tx.commit();
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
                em.close();
                final ErrorResponse errorResponse = new ErrorResponse.ErrorResponseBuilder()
                        .message("Bad Request")
                        .detail("The series metadata is differente from Kheops")
                        .build();
                return Response.status(BAD_REQUEST).entity(errorResponse).build();
            }
        }

        if (!isNewStudy && !compareStudy(study, studyParam)) {
            try {
                tx.begin();
                if (isNewInstance) {
                    em.remove(instance);
                }
                if (isNewSeries) {
                    em.remove(series);
                }
                tx.commit();
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
                em.close();
                final ErrorResponse errorResponse = new ErrorResponse.ErrorResponseBuilder()
                        .message("Bad Request")
                        .detail("The study metadata is differente from Kheops")
                        .build();
                return Response.status(BAD_REQUEST).entity(errorResponse).build();
            }
        }

        Album destinationHashMap = null;
        try {
            tx.begin();
            //add series in destination if not present
            final Album destination;
            if (albumId == null) {
                destination = kheopsPrincipal.getUser().getInbox();
            } else {
                destination = getAlbum(albumId, em);
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
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }

        //Webhook
        final Source source = new Source(kheopsPrincipal.getUser());
        kheopsPrincipal.getCapability().ifPresent(source::setCapabilityToken);
        kheopsPrincipal.getClientId().ifPresent(clienrtId -> source.setReportProviderClientId(getReportProviderWithClientId(clienrtId, em)));
        FooHashMap.getInstance().addHashMapData(study, series, instance, destinationHashMap, isNewStudy, isNewSeries, isNewInstance, source, isNewInDestination);
        FooHashMap.getInstance().setKheopsInstance(context.getInitParameter(HOST_ROOT_PARAMETER));

        KheopsLogBuilder kheopsLogBuilder = kheopsPrincipal.getKheopsLogBuilder()
                .action(KheopsLogBuilder.ActionType.STOW)
                .study(studyInstanceUID)
                .series(seriesInstanceUID)
                .instances(instancesUID);
        if (albumId != null) {
            kheopsLogBuilder.album(albumId);
        }
        kheopsLogBuilder.log();

        return Response.status(NO_CONTENT).build();
    }

    private static boolean compareSeries(Series series, SeriesParam seriesParam) {
        return (series.getModality() == null ? seriesParam.modality == null : series.getModality().equals(seriesParam.modality)) &&
                (series.getSeriesDescription() == null ? seriesParam.seriesDescription == null : series.getSeriesDescription().equals(seriesParam.seriesDescription)) &&
                series.getSeriesNumber() == seriesParam.seriesNumber &&
                (series.getBodyPartExamined() == null ? seriesParam.bodyPartExamined == null : series.getBodyPartExamined().equals(seriesParam.bodyPartExamined)) &&
                (series.getTimezoneOffsetFromUTC() == null ? seriesParam.timzoneOffsetFromUtc == null : series.getTimezoneOffsetFromUTC().equals(seriesParam.timzoneOffsetFromUtc)) &&
                (series.getStudy().getStudyInstanceUID() == null ? seriesParam.studyInstanceUID == null : series.getStudy().getStudyInstanceUID().equals(seriesParam.studyInstanceUID));
    }

    private static boolean compareStudy(Study study, StudyParam studyParam) {
        return (study.getStudyDate() == null ? studyParam.studyDate == null : study.getStudyDate().equals(studyParam.studyDate)) &&
                (study.getStudyTime() == null ? studyParam.studyTime == null : study.getStudyTime().equals(studyParam.studyTime)) &&
                (study.getStudyDescription() == null ? studyParam.studyDescription == null : study.getStudyDescription().equals(studyParam.studyDescription)) &&
                (study.getTimezoneOffsetFromUTC() == null ? studyParam.timzoneOffsetFromUtc == null : study.getTimezoneOffsetFromUTC().equals(studyParam.timzoneOffsetFromUtc)) &&
                (study.getAccessionNumber() == null ? studyParam.accessionNumber == null : study.getAccessionNumber().equals(studyParam.accessionNumber)) &&
                (study.getReferringPhysicianName() == null ? studyParam.referringPhysicianName == null : study.getReferringPhysicianName().equals(studyParam.referringPhysicianName)) &&
                (study.getPatientName() == null ? studyParam.patientName == null : study.getPatientName().equals(studyParam.patientName)) &&
                (study.getPatientID() == null ? studyParam.patientId == null : study.getPatientID().equals(studyParam.patientId)) &&
                (study.getPatientBirthDate() == null ? studyParam.patientBirthDate == null : study.getPatientBirthDate().equals(studyParam.patientBirthDate)) &&
                (study.getPatientSex() == null ? studyParam.patientSex == null : study.getPatientSex().equals(studyParam.patientSex)) &&
                (study.getStudyID() == null ? studyParam.studyId == null : study.getStudyID().equals(studyParam.studyId));
    }

    private class GetOrCreateSeriesResult {
        private Series series;
        private boolean isNewSeries;

        public GetOrCreateSeriesResult(Series series, boolean isNewSeries) {
            this.series = series;
            this.isNewSeries = isNewSeries;
        }

        public Series getSeries() {
            return series;
        }

        public boolean isNewSeries() {
            return isNewSeries;
        }
    }

    private GetOrCreateSeriesResult getOrCreateSeries(SeriesParam seriesParam, Study study, EntityTransaction tx, EntityManager em) {
        boolean isNewSeries = false;
        Series series;
        try {
            tx.begin();
            try {
                series = getSeries(seriesParam.studyInstanceUID, seriesParam.seriesInstanceUID, em);
                tx.commit();

            } catch (SeriesNotFoundException e) {
                series = new Series(seriesParam.seriesInstanceUID, study);
                series.setModality(seriesParam.modality);
                series.setBodyPartExamined(seriesParam.bodyPartExamined);
                series.setSeriesDescription(seriesParam.seriesDescription);
                series.setSeriesNumber(seriesParam.seriesNumber);
                series.setTimezoneOffsetFromUTC(seriesParam.timzoneOffsetFromUtc);
                em.persist(series);
                tx.commit();
                isNewSeries = true;
            }
        } catch (PersistenceException e) {
            try {
                tx.rollback();
                tx.begin();
                series = getSeries(seriesParam.studyInstanceUID, seriesParam.seriesInstanceUID, em);
                tx.commit();
            } catch (SeriesNotFoundException unused) {
                throw new IllegalStateException();
            }
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return new GetOrCreateSeriesResult(series, isNewSeries);
    }

    private class GetOrCreateInstanceResult {
        private Instances instance;
        private boolean isNewInstance;

        public GetOrCreateInstanceResult(Instances instance, boolean isNewInstance) {
            this.instance = instance;
            this.isNewInstance = isNewInstance;
        }

        public Instances getInstance() {
            return instance;
        }

        public boolean isNewInstance() {
            return isNewInstance;
        }
    }

    private GetOrCreateInstanceResult getOrCreateInstance(String instancesUID, Series series, EntityTransaction tx, EntityManager em) {

        boolean isNewInstance = false;
        Instances instance;
        try  {
            tx.begin();
            try {
                instance = getInstances(instancesUID, em);
                tx.commit();
            } catch (InstancesNotFoundException e) {
                instance = new Instances(instancesUID, series);
                em.persist(instance);
                tx.commit();
                isNewInstance = true;
            }
        } catch(PersistenceException e) {
            try {
                tx.rollback();
                tx.begin();
                instance = getInstances(instancesUID, em);
                tx.commit();
            } catch (InstancesNotFoundException unused) {
                throw new IllegalStateException();
            }
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return new GetOrCreateInstanceResult(instance, isNewInstance);
    }

    private class GetOrCreateStudyResult {
        private Study study;
        private boolean isNewStudy;

        public GetOrCreateStudyResult(Study study, boolean isNewStudy) {
            this.study = study;
            this.isNewStudy = isNewStudy;
        }

        public Study getStudy() {
            return study;
        }

        public boolean isNewStudy() {
            return isNewStudy;
        }
    }

    private GetOrCreateStudyResult getOrCreateStudy(StudyParam studyParam, EntityTransaction tx, EntityManager em) {

        boolean isNewStudy = false;
        Study study;

        try {
            tx.begin();
            try {
                study = getStudy(studyParam.studyInstanceUID, em);
                tx.commit();

            } catch (StudyNotFoundException e) {
                study = new Study(studyParam.studyInstanceUID);
                study.setStudyDescription(studyParam.studyDescription);
                study.setAccessionNumber(studyParam.accessionNumber);
                study.setPatientBirthDate(studyParam.patientBirthDate);
                study.setPatientName(studyParam.patientName);
                study.setPatientID(studyParam.patientId);
                study.setPatientSex(studyParam.patientSex);
                study.setReferringPhysicianName(studyParam.referringPhysicianName);
                study.setStudyDate(studyParam.studyDate);
                study.setStudyTime(studyParam.studyTime);
                study.setTimezoneOffsetFromUTC(studyParam.timzoneOffsetFromUtc);
                study.setStudyID(studyParam.studyId);
                em.persist(study);
                tx.commit();
                isNewStudy = true;
            }

        } catch (PersistenceException e) {
            try {
                tx.rollback();
                tx.begin();
                study = getStudy(studyParam.studyInstanceUID, em);
                tx.commit();
            } catch (StudyNotFoundException unused) {
                throw new IllegalStateException();
            }
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return new GetOrCreateStudyResult(study, isNewStudy);
    }
}

package online.kheops.auth_server.series;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.album.AlbumNotFoundException;
import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.event.Events;
import online.kheops.auth_server.util.KheopsLogBuilder;
import online.kheops.auth_server.util.KheopsLogBuilder.*;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static online.kheops.auth_server.album.Albums.getAlbum;
import static online.kheops.auth_server.album.AlbumsSeries.getAlbumSeries;
import static online.kheops.auth_server.series.SeriesQueries.*;

public class Series {

    private Series() {
        throw new IllegalStateException("Utility class");
    }

    public static void safeAttributeSetString(Attributes attributes, int tag, VR vr, String string) {
        if (string != null) {
            attributes.setString(tag, vr, string);
        }
    }

    public static online.kheops.auth_server.entity.Series getSeries(String studyInstanceUID, String seriesInstanceUID, EntityManager em)
            throws SeriesNotFoundException{
        return findSeriesByStudyUIDandSeriesUID(studyInstanceUID, seriesInstanceUID, em);
    }
    
    public static online.kheops.auth_server.entity.Series getSeries(String seriesInstanceUID, EntityManager em)
            throws SeriesNotFoundException{
        return findSeriesBySeriesUID(seriesInstanceUID, em);
    }

    public static boolean seriesExist(String studyInstanceUID, String seriesInstanceUID, EntityManager em) {
        try {
            getSeries(studyInstanceUID,  seriesInstanceUID, em);
        } catch (SeriesNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean canAccessSeries(User user, String studyInstanceUID, String seriesInstanceUID, EntityManager em) {
        try {
            findSeriesByStudyUIDandSeriesUID(user, studyInstanceUID,  seriesInstanceUID, em);
            return true;
        }catch (SeriesNotFoundException e) {
            return false;
        }
    }

    public static boolean canAccessSeries(Album album, String studyInstanceUID, String seriesInstanceUID, EntityManager em) {
        try {
            findSeriesByStudyUIDandSeriesUIDFromAlbum(album, studyInstanceUID,  seriesInstanceUID, em);
            return true;
        }catch (SeriesNotFoundException e) {
            return false;
        }
    }


    public static void editFavorites(User callingUser, String studyInstanceUID, String seriesInstanceUID, String fromAlbumId, boolean favorite, KheopsLogBuilder kheopsLogBuilder)
            throws AlbumNotFoundException, SeriesNotFoundException {
        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            callingUser = em.merge(callingUser);
            final Album album;
            if (fromAlbumId == null) {
                album = callingUser.getInbox();
                kheopsLogBuilder.album("inbox");
            } else {
                album = getAlbum(fromAlbumId, em);
                kheopsLogBuilder.album(fromAlbumId);
            }
            final online.kheops.auth_server.entity.Series series = getSeries(studyInstanceUID, seriesInstanceUID, em);
            editSeriesFavorites(series, album, favorite, em);

            final Events.MutationType mutation;
            if (favorite) {
                mutation = Events.MutationType.ADD_FAV;
                kheopsLogBuilder.action(ActionType.ADD_FAVORITE_SERIES);
            } else {
                mutation = Events.MutationType.REMOVE_FAV;
                kheopsLogBuilder.action(ActionType.REMOVE_FAVORITE_SERIES);

            }
            final Mutation favSeriesMutation = Events.albumPostSeriesMutation(callingUser, album, mutation, series);
            em.persist(favSeriesMutation);
            album.updateLastEventTime();
            tx.commit();

            kheopsLogBuilder.series(seriesInstanceUID)
                    .study(studyInstanceUID)
                    .log();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    public static void editSeriesFavorites(online.kheops.auth_server.entity.Series series, Album album, boolean favorite, EntityManager em) {
        final AlbumSeries albumSeries = getAlbumSeries(album, series, em);
        albumSeries.setFavorite(favorite);
    }

    public static boolean isSeriesInInbox(User callingUser, online.kheops.auth_server.entity.Series series, EntityManager em) {
        try {
            findSeriesBySeriesAndUserInbox(callingUser, series, em);
            return true;
        } catch (SeriesNotFoundException e) {
            return false;
        }
    }

}

package online.kheops.auth_server.principal;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.NotAlbumScopeTypeException;
import online.kheops.auth_server.accesstoken.ViewerAccessToken;
import online.kheops.auth_server.album.AlbumNotFoundException;
import online.kheops.auth_server.accesstoken.AccessToken;
import online.kheops.auth_server.capability.ScopeType;
import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.entity.User;
import online.kheops.auth_server.series.SeriesNotFoundException;
import online.kheops.auth_server.user.UserNotFoundException;
import online.kheops.auth_server.user.AlbumUserPermissions;
import online.kheops.auth_server.util.KheopsLogBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static online.kheops.auth_server.album.Albums.*;
import static online.kheops.auth_server.series.Series.getSeries;
import static online.kheops.auth_server.series.SeriesQueries.findSeriesListByStudyUIDFromAlbum;
import static online.kheops.auth_server.series.SeriesQueries.findSeriesListByStudyUIDFromInbox;
import static online.kheops.auth_server.user.Users.getOrCreateUser;

public class ViewerPrincipal implements KheopsPrincipalInterface {

    private EntityManager em;
    private EntityTransaction tx;
    private final ViewerAccessToken viewerAccessToken;
    private final KheopsPrincipalInterface kheopsPrincipal;


    public ViewerPrincipal(ServletContext servletContext, ViewerAccessToken viewerAccessToken) {

        final AccessToken accessToken = viewerAccessToken.getAccessToken();

        final User user;
        try {
            user = getOrCreateUser(accessToken.getSubject());
        } catch (UserNotFoundException e) {
            throw new IllegalStateException(e);
        }
        kheopsPrincipal = accessToken.newPrincipal(servletContext, user);

        this.viewerAccessToken = viewerAccessToken;
    }

    @Override
    public long getDBID() {
        return kheopsPrincipal.getDBID();
    }
    //end old version

    @Override
    public String getName() { return kheopsPrincipal.getName(); }

    @Override
    public boolean hasSeriesReadAccess(String studyInstanceUID, String seriesInstanceUID) throws SeriesNotFoundException{

        if(!kheopsPrincipal.hasSeriesReadAccess(studyInstanceUID, seriesInstanceUID)) {
            return false;
        }

        this.em = EntityManagerListener.createEntityManager();
        this.tx = em.getTransaction();
        try {
            tx.begin();

            final List<Series> seriesList;
            if(viewerAccessToken.isInbox()) {
                seriesList = findSeriesListByStudyUIDFromInbox(getUser(), studyInstanceUID, em);
            } else {
                final Album album = getAlbum(viewerAccessToken.getSourceId(), em);
                seriesList = findSeriesListByStudyUIDFromAlbum(getUser(), album, studyInstanceUID, em);
            }

            if (seriesList.contains(getSeries(studyInstanceUID, seriesInstanceUID, em))) {
                return true;
            } else {
                return false;
            }
        } catch (AlbumNotFoundException e) {
            return false;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    @Override
    public boolean hasStudyReadAccess(String studyInstanceUID) {
        if(!kheopsPrincipal.hasStudyReadAccess(studyInstanceUID)) {
            return false;
        }

        if (studyInstanceUID.equals(viewerAccessToken.getStudyInstanceUID())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasUserAccess() { return false; }

    @Override
    public boolean hasSeriesWriteAccess(String studyInstanceUID, String seriesInstanceUID) { return false; }

    @Override
    public boolean hasStudyWriteAccess(String study) { return false; }

    @Override
    public boolean hasAlbumPermission(AlbumUserPermissions usersPermission, String albumId)
            throws AlbumNotFoundException {

        if (!kheopsPrincipal.hasAlbumPermission(usersPermission, albumId)) {
            return false;
        } else {
            this.em = EntityManagerListener.createEntityManager();
            this.tx = em.getTransaction();
            try {
                tx.begin();

                final User userMerge = em.merge(getUser());
                final Album album = getAlbum(albumId, em);

                if(!isMemberOfAlbum(userMerge, album, em)) {
                    throw new AlbumNotFoundException("Album id : " + albumId + " not found");
                }

                if(userMerge.getInbox() == album) {
                    throw new AlbumNotFoundException("Album id : " + albumId + " not found");
                }

                return usersPermission.hasViewerPermission(album);
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
                em.close();
            }
        }
    }

    @Override
    public boolean hasAlbumAccess(String albumId){
        try {
            return kheopsPrincipal.hasAlbumAccess(albumId) && !viewerAccessToken.isInbox() && viewerAccessToken.getSourceId().equals(albumId);
        } catch (AlbumNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean hasInboxAccess() {
        return viewerAccessToken.isInbox();
    }

    @Override
    public User getUser() { return kheopsPrincipal.getUser(); }

    @Override
    public ScopeType getScope() {
        if(!viewerAccessToken.isInbox() || kheopsPrincipal.getScope() == ScopeType.ALBUM) {
            return ScopeType.ALBUM;
        } else {
            return ScopeType.USER;
        }

    }

    @Override
    public String getAlbumID() throws NotAlbumScopeTypeException, AlbumNotFoundException {
        final String albumID;
        if(!viewerAccessToken.isInbox()) {
            albumID = viewerAccessToken.getSourceId();
        } else {
            throw new NotAlbumScopeTypeException("");
        }

        if(kheopsPrincipal.hasAlbumAccess(albumID)) {
            return albumID;
        } else {
            throw new AlbumNotFoundException("");
        }
    }

    @Override
    public Optional<String> getActingParty() {
        return kheopsPrincipal.getActingParty();
    }

    @Override
    public Optional<String> getAuthorizedParty() {
        return kheopsPrincipal.getAuthorizedParty();
    }

    @Override
    public Optional<String> getCapabilityTokenId() {
        return kheopsPrincipal.getCapabilityTokenId();
    }

    @Override
    public KheopsLogBuilder getKheopsLogBuilder() {
        return new KheopsLogBuilder().user(getUser().getKeycloakId()).tokenType(AccessToken.TokenType.VIEWER_TOKEN);
    }

    @Override
    public String toString() {
        return "[ViewerPrincipal user:" + getUser() + " scope:" + getScope() + " hasUserAccess:" + hasUserAccess() + " hasInboxAccess:" + hasInboxAccess() + "]";
    }

    @Override
    public Optional<List<String>> getStudyList() {
        return Optional.of(Collections.singletonList(viewerAccessToken.getStudyInstanceUID()));
    }
}

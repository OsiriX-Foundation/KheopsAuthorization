package online.kheops.auth_server.album;

import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.AlbumUser;
import online.kheops.auth_server.entity.User;
import online.kheops.auth_server.util.Consts;
import online.kheops.auth_server.util.PairListXTotalCount;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.core.MultivaluedMap;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static online.kheops.auth_server.album.AlbumResponses.recordToAlbumResponseForCapabilityToken;
import static online.kheops.auth_server.generated.tables.Users.USERS;
import static online.kheops.auth_server.util.JOOQTools.*;
import static online.kheops.auth_server.album.AlbumResponses.recordToAlbumResponse;
import static online.kheops.auth_server.generated.tables.Album.ALBUM;
import static online.kheops.auth_server.generated.tables.AlbumSeries.ALBUM_SERIES;
import static online.kheops.auth_server.generated.tables.AlbumUser.ALBUM_USER;
import static online.kheops.auth_server.generated.tables.Event.EVENT;
import static online.kheops.auth_server.generated.tables.Series.SERIES;
import static org.jooq.impl.DSL.*;

public class AlbumQueries {

    private static final Logger LOG = Logger.getLogger(AlbumQueries.class.getName());

    private AlbumQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static List<AlbumUser> findAlbumsUserByUser(User targetUser, EntityManager em) {
        return em.createQuery("SELECT a from AlbumUser a where :targetUser = a.user", AlbumUser.class)
                .setParameter("targetUser", targetUser)
                .getResultList();
    }

    public static Album findAlbumByPk(long albumPk, EntityManager em) throws NoResultException{
        return em.createQuery("SELECT a from Album a where :albumPk = a.pk", Album.class)
                .setParameter("albumPk", albumPk)
                .getSingleResult();
    }

    public static AlbumUser findAlbumUserByUserAndAlbum(User user, Album album, EntityManager em ) throws NoResultException {
        return em.createQuery("SELECT a from AlbumUser a where :targetUser = a.user and :targetAlbum = a.album", AlbumUser.class)
                .setParameter("targetUser", user)
                .setParameter("targetAlbum", album)
                .getSingleResult();
    }

    public static PairListXTotalCount<AlbumResponses.AlbumResponse> findAlbumsByUserPk(long userPK, MultivaluedMap<String, String> queryParameters)
            throws JOOQException, BadQueryParametersException {
        try (Connection connection = getDataSource().getConnection()) {

            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            SelectQuery query = create.selectQuery();

            ArrayList<Condition> conditionArrayList = new ArrayList<>();

            Field<Object> nbUsers = create.select(countDistinct(ALBUM_USER.PK))
                    .from(ALBUM_USER)
                    .where(ALBUM_USER.ALBUM_FK.eq(ALBUM.PK))
                    .asField();

            query.addSelect(ALBUM.PK.as("album_pk"),
                    ALBUM.NAME.as("album_name"),
                    isnull(ALBUM.DESCRIPTION,"NULL").as("album_description"),
                    ALBUM.CREATED_TIME.as("album_created_time"),
                    ALBUM.LAST_EVENT_TIME.as("album_last_event_time"),
                    nbUsers.as("number_of_users"),
                    countDistinct(EVENT.PK).as("number_of_comments"),
                    countDistinct(SERIES.STUDY_FK).as("number_of_studies"),
                    ALBUM.ADD_USER_PERMISSION.as("add_user_permission"),
                    ALBUM.DOWNLOAD_SERIES_PERMISSION.as("download_user_permission"),
                    ALBUM.SEND_SERIES_PERMISSION.as("send_series_permission"),
                    ALBUM.DELETE_SERIES_PERMISSION.as("delete_series_permision"),
                    ALBUM.ADD_SERIES_PERMISSION.as("add_series_permission"),
                    ALBUM.WRITE_COMMENTS_PERMISSION.as("write_comment_permission"),
                    ALBUM_USER.FAVORITE.as("favorite"),
                    ALBUM_USER.NEW_COMMENT_NOTIFICATIONS.as("new_comment_notifications"),
                    ALBUM_USER.NEW_SERIES_NOTIFICATIONS.as("new_series_notifications"),
                    ALBUM_USER.ADMIN.as("admin"),
                    groupConcatDistinct(SERIES.MODALITY).as("modalities"));

            query.addFrom(ALBUM);
            query.addJoin(ALBUM_SERIES,JoinType.LEFT_OUTER_JOIN, ALBUM_SERIES.ALBUM_FK.eq(ALBUM.PK));
            query.addJoin(SERIES,JoinType.LEFT_OUTER_JOIN, SERIES.PK.eq(ALBUM_SERIES.SERIES_FK));
            query.addJoin(ALBUM_USER, ALBUM_USER.ALBUM_FK.eq(ALBUM.PK));
            query.addJoin(USERS, ALBUM_USER.USER_FK.eq(USERS.PK));

            query.addJoin(EVENT, JoinType.LEFT_OUTER_JOIN, EVENT.ALBUM_FK.eq(ALBUM.PK)
                    .and(EVENT.EVENT_TYPE.eq("Comment"))
                    .and(EVENT.PRIVATE_TARGET_USER_FK.isNull()
                            .or(EVENT.PRIVATE_TARGET_USER_FK.eq(userPK))
                            .or(EVENT.USER_FK.eq(userPK))));

            conditionArrayList.add(ALBUM_USER.FAVORITE.isNotNull());
            conditionArrayList.add(createConditon(queryParameters, "name", ALBUM.NAME));
            conditionArrayList.add(favoriteCondition(queryParameters));
            conditionArrayList.add(createDateCondition(queryParameters, "created_time", ALBUM.CREATED_TIME));
            conditionArrayList.add(createDateCondition(queryParameters, "last_event_time", ALBUM.LAST_EVENT_TIME));
            conditionArrayList.add(ALBUM.PK.notEqual(USERS.INBOX_FK));
            query.addConditions(ALBUM_USER.USER_FK.eq(userPK).and(ALBUM_USER.ALBUM_FK.eq(ALBUM.PK)));

            for (Condition c : conditionArrayList) {
                if (c != null) {
                    query.addConditions(c);
                }
            }

            if (queryParameters.containsKey(Consts.QUERY_PARAMETER_LIMIT)) {
                query.addLimit(getLimit(queryParameters));
            }

            if (queryParameters.containsKey(Consts.QUERY_PARAMETER_OFFSET)) {
                query.addOffset(getOffset(queryParameters));
            }

            query.addOrderBy(getOrderBy(queryParameters, create));

            query.addGroupBy(ALBUM.PK, ALBUM_USER.PK);

            Result<Record18<BigDecimal, String, String, String, String, Long, Long, Long, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, String>> result = query.fetch();

            final List<AlbumResponses.AlbumResponse> albumResponses = new ArrayList<>();

            for(Record r : result) {
                albumResponses.add(recordToAlbumResponse(r));
            }

            final int albumTotalCount = getAlbumTotalCount(userPK, conditionArrayList, connection);

            return new PairListXTotalCount<>(albumTotalCount, albumResponses);
        } catch (BadQueryParametersException e) {
            throw new BadQueryParametersException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JOOQException("Error during request");
        }
    }

    public static AlbumResponses.AlbumResponse findAlbumByUserPkAndAlbumPk(long albumPk, long userPK)
            throws JOOQException {
        try (Connection connection = getDataSource().getConnection()) {

            final DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            final SelectQuery query = create.selectQuery();

            Field<Object> nbUsers = create.select(countDistinct(ALBUM_USER.PK))
                    .from(ALBUM_USER)
                    .where(ALBUM_USER.ALBUM_FK.eq(albumPk))
                    .asField();

            query.addSelect(ALBUM.PK.as("album_pk"),
                    ALBUM.NAME.as("album_name"),
                    isnull(ALBUM.DESCRIPTION,"NULL").as("album_description"),
                    ALBUM.CREATED_TIME.as("album_created_time"),
                    ALBUM.LAST_EVENT_TIME.as("album_last_event_time"),
                    nbUsers.as("number_of_users"),
                    countDistinct(EVENT.PK).as("number_of_comments"),
                    countDistinct(SERIES.STUDY_FK).as("number_of_studies"),
                    ALBUM.ADD_USER_PERMISSION.as("add_user_permission"),
                    ALBUM.DOWNLOAD_SERIES_PERMISSION.as("download_user_permission"),
                    ALBUM.SEND_SERIES_PERMISSION.as("send_series_permission"),
                    ALBUM.DELETE_SERIES_PERMISSION.as("delete_series_permision"),
                    ALBUM.ADD_SERIES_PERMISSION.as("add_series_permission"),
                    ALBUM.WRITE_COMMENTS_PERMISSION.as("write_comment_permission"),
                    ALBUM_USER.FAVORITE.as("favorite"),
                    ALBUM_USER.NEW_COMMENT_NOTIFICATIONS.as("new_comment_notifications"),
                    ALBUM_USER.NEW_SERIES_NOTIFICATIONS.as("new_series_notifications"),
                    ALBUM_USER.ADMIN.as("admin"),
                    groupConcatDistinct(SERIES.MODALITY).as("modalities"));

            query.addFrom(ALBUM);
            query.addJoin(ALBUM_SERIES,JoinType.LEFT_OUTER_JOIN, ALBUM_SERIES.ALBUM_FK.eq(ALBUM.PK));
            query.addJoin(SERIES,JoinType.LEFT_OUTER_JOIN, SERIES.PK.eq(ALBUM_SERIES.SERIES_FK));
            query.addJoin(ALBUM_USER, ALBUM_USER.ALBUM_FK.eq(ALBUM.PK));

            query.addJoin(EVENT, JoinType.LEFT_OUTER_JOIN, EVENT.ALBUM_FK.eq(ALBUM.PK)
                    .and(EVENT.EVENT_TYPE.eq("Comment"))
                    .and(EVENT.PRIVATE_TARGET_USER_FK.isNull()
                            .or(EVENT.PRIVATE_TARGET_USER_FK.eq(userPK))
                            .or(EVENT.USER_FK.eq(userPK))));

            query.addConditions(ALBUM.PK.eq(albumPk));
            query.addConditions(ALBUM_USER.FAVORITE.isNotNull());
            query.addConditions(ALBUM_USER.USER_FK.eq(userPK));

            query.addGroupBy(ALBUM.PK, ALBUM_USER.PK);

            Record18<BigDecimal, String, String, String, String, Long, Long, Long, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, String> result = (Record18<BigDecimal, String, String, String, String, Long, Long, Long, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, String>) query.fetchOne();

            return recordToAlbumResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JOOQException("Error during request");
        }
    }

    public static AlbumResponses.AlbumResponse findAlbumByAlbumPk(long albumPk)
            throws JOOQException {
        try (Connection connection = getDataSource().getConnection()) {

            final DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            final SelectQuery query = create.selectQuery();

            Field<Object> nbUsers = create.select(countDistinct(ALBUM_USER.PK))
                    .from(ALBUM_USER)
                    .where(ALBUM_USER.ALBUM_FK.eq(ALBUM.PK))
                    .asField();

            query.addSelect(isnull(ALBUM.PK,"NULL").as("album_pk"),
                    isnull(ALBUM.NAME,"NULL").as("album_name"),
                    isnull(ALBUM.DESCRIPTION,"NULL").as("album_description"),
                    isnull(countDistinct(EVENT.PK),"NULL").as("number_of_comments"),
                    isnull(countDistinct(SERIES.STUDY_FK),"NULL").as("number_of_studies"),
                    groupConcatDistinct(SERIES.MODALITY).as("modalities"));

            query.addFrom(ALBUM);
            query.addJoin(ALBUM_SERIES,JoinType.LEFT_OUTER_JOIN, ALBUM_SERIES.ALBUM_FK.eq(ALBUM.PK));
            query.addJoin(SERIES,JoinType.LEFT_OUTER_JOIN, SERIES.PK.eq(ALBUM_SERIES.SERIES_FK));
            query.addJoin(ALBUM_USER, ALBUM_USER.ALBUM_FK.eq(ALBUM.PK));

            query.addJoin(EVENT, JoinType.LEFT_OUTER_JOIN, EVENT.ALBUM_FK.eq(ALBUM.PK)
                    .and(EVENT.EVENT_TYPE.eq("Comment"))
                    .and(EVENT.PRIVATE_TARGET_USER_FK.isNull()));

            query.addConditions(ALBUM.PK.eq(albumPk));;

            query.addGroupBy(ALBUM.PK);
            Record6<BigDecimal, String, String, Long, Long, String> result = (Record6<BigDecimal, String, String, Long, Long, String>) query.fetchOne();

            return recordToAlbumResponseForCapabilityToken(result);
        } catch (Exception e) {
            throw new JOOQException("Error during request");
        }
    }

    private static int getAlbumTotalCount(long userPk, ArrayList<Condition> conditionArrayList, Connection connection) {
        final DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
        final SelectQuery query = create.selectQuery();
        query.addSelect(countDistinct(ALBUM.PK));
        query.addFrom(ALBUM);
        query.addJoin(ALBUM_USER, ALBUM_USER.ALBUM_FK.eq(ALBUM.PK).and(ALBUM_USER.USER_FK.eq(userPk)));
        query.addJoin(USERS, USERS.PK.eq(ALBUM_USER.USER_FK));
        for (Condition c : conditionArrayList) {
            if (c != null) {
                query.addConditions(c);
            }
        }

        return (int) query.fetchOne().getValue(0);
    }

    private static SortField getOrderBy(MultivaluedMap<String, String> queryParameters, DSLContext create) throws  BadQueryParametersException{
        if (queryParameters.containsKey("sort")) {
            Field ord;

            final Boolean ascDesc = queryParameters.get("sort").get(0).startsWith("-");
            final String orderByParameter = queryParameters.get("sort").get(0).replace("-", "");

            if (orderByParameter.compareTo("created_time") == 0) ord = ALBUM.CREATED_TIME;
            else if (orderByParameter.compareTo("last_event_time") == 0) ord = ALBUM.LAST_EVENT_TIME;
            else if (orderByParameter.compareTo("name") == 0) ord = ALBUM.NAME;
            else if (orderByParameter.compareTo("number_of_users") == 0) {
                final Field<Object> fieldNumberOfUsers = create.select(countDistinct(ALBUM_USER.PK)).asField();
                ord = fieldNumberOfUsers;
            }
            else if (orderByParameter.compareTo("number_of_studies") == 0) {
                final Field<Object> fieldNumberOfStudies = create.select(countDistinct(EVENT.PK)).asField();
                ord = fieldNumberOfStudies;
            }
            else if (orderByParameter.compareTo("number_of_comments") == 0) {
                final Field<Object> fieldNumberOfComments = create.select(countDistinct(SERIES.STUDY_FK)).asField();
                ord = fieldNumberOfComments;
            }
            else throw new BadQueryParametersException("sort: " + queryParameters.get("sort").get(0));

            return ascDesc ? ord.desc() : ord.asc();
        }
        //Default sort
        return ALBUM.CREATED_TIME.desc();
    }


    private static Condition createConditon(MultivaluedMap<String, String> queryParameters, String key, TableField column)
    throws BadQueryParametersException {
        if (queryParameters.containsKey(key)) {
            String parameterNoStar = queryParameters.get(key).get(0).replace("*", "");

            String parameter = queryParameters.get(key).get(0);
            if (parameterNoStar.length() == 0) {
                return null;
            }
            if ("null".equalsIgnoreCase(parameterNoStar)) {
                return column.isNull();
            } else {
                Condition condition;
                if (parameter.startsWith("*") && parameter.endsWith("*")) {
                    condition = column.lower().contains(parameterNoStar.toLowerCase());
                } else if (parameter.startsWith("*")) {
                    condition = column.lower().endsWith(parameterNoStar.toLowerCase());
                } else if (parameter.endsWith("*")) {
                    condition = column.lower().startsWith(parameterNoStar.toLowerCase());
                } else {
                    condition = column.lower().equal(parameterNoStar.toLowerCase());
                }

                if (isFuzzyMatching(queryParameters)) {
                    Condition fuzzyCondition = condition("SOUNDEX('"+parameterNoStar+"') = SOUNDEX("+column.getName()+")");
                    return condition.or(fuzzyCondition);
                }
                return condition;
            }
        } else {
            return null;
        }
    }

    private static Condition favoriteCondition(MultivaluedMap<String, String> queryParameters)
            throws BadQueryParametersException {
        if (queryParameters.containsKey("favorite")) {
            if (queryParameters.get("favorite").get(0).compareTo("true") == 0) {
                return ALBUM_USER.FAVORITE.isTrue();
            } else if (queryParameters.get("favorite").get(0).compareTo("false") == 0) {
                return ALBUM_USER.FAVORITE.isFalse();
            } else {
                throw new BadQueryParametersException("favorite: " + queryParameters.get("favorite").get(0));
            }
        }
        return null;
    }
}

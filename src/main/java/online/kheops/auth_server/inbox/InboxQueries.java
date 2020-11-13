package online.kheops.auth_server.inbox;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.album.JOOQException;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

import static online.kheops.auth_server.generated.Tables.*;
import static org.jooq.impl.DSL.*;

public class InboxQueries {

    private InboxQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static InboxInfoResponse getInboxInfo(long userPk) throws JOOQException {

        try (Connection connection = EntityManagerListener.getConnection()) {

            final DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            final SelectQuery<Record> query = create.selectQuery();

            query.addSelect(countDistinct(SERIES.STUDY_FK).as("number_of_studies"),
                    countDistinct(SERIES.PK).as("number_of_series"),
                    isnull(sum(SERIES.NUMBER_OF_SERIES_RELATED_INSTANCES), 0).as("number_of_instances"),
                    groupConcatDistinct(SERIES.MODALITY).as("modalities"));

            query.addFrom(USERS);
            query.addJoin(ALBUMS, ALBUMS.PK.eq(USERS.INBOX_FK));
            query.addJoin(ALBUM_SERIES,JoinType.LEFT_OUTER_JOIN, ALBUM_SERIES.ALBUM_FK.eq(ALBUMS.PK));
            query.addJoin(SERIES,JoinType.LEFT_OUTER_JOIN, SERIES.PK.eq(ALBUM_SERIES.SERIES_FK));
            query.addJoin(STUDIES,JoinType.LEFT_OUTER_JOIN, STUDIES.PK.eq(SERIES.STUDY_FK));

            query.addConditions(USERS.PK.eq(userPk));

            final Record result = query.fetchOne();

            return new InboxInfoResponse(result);
        } catch (SQLException e) {
            throw new JOOQException("Error during request", e);
        }
    }



}

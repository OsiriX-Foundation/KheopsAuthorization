package online.kheops.auth_server.util;

import com.google.common.collect.ImmutableList;

import javax.ws.rs.Priorities;
import java.time.Duration;

public class Consts {

    private Consts() {
        throw new IllegalStateException("Utility class");
    }

    public static final String StudyInstanceUID = "StudyInstanceUID";
    public static final String SeriesInstanceUID = "SeriesInstanceUID";

    public static final String QUERY_PARAMETER_LIMIT = "limit";
    public static final String QUERY_PARAMETER_OFFSET = "offset";
    public static final String QUERY_PARAMETER_FUZZY_MATCHING = "fuzzymatching";
    public static final String QUERY_PARAMETER_SORT = "sort";

    public static final String ALBUM = "album";
    public static final String INBOX = "inbox";

    public static final String INCLUDE_FIELD = "includefield";

    public abstract static class USER_IN_ROLE {
        private USER_IN_ROLE() {}
        public static final String CAPABILITY = "capability";
        public static final String VIEWER_TOKEN = "viewerToken";
        public static final String RP_TOKEN = "reportProviderToken";
    }

    public static final int NUMBER_OF_RETRY_WEBHOOK = 5;
    public static final int SECONDE_BEFORE_RETRY_WEBHOOK = 5;
    public static final ImmutableList<String> VALID_SCHEMES_WEBHOOK_URL = ImmutableList.of("http", "https");

    public static final String HOST_ROOT_PARAMETER = "online.kheops.root.uri";

    //AlbumQueryParams.java
    public static final String FAVORITE = "favorite";
    public static final int CUSTOM_DICOM_TAG_FAVORITE = 0x00012345;
    public static final String COMMENTS = "comments";
    public static final int CUSTOM_DICOM_TAG_COMMENTS = 0x00012346;
    public static final String NAME = "name";
    public static final String CREATED_TIME = "created_time";
    public static final String LAST_EVENT_TIME = "last_event_time";

    public static final long CAPABILITY_LEEWAY_SECOND = 5;

    public static final int USER_ACCESS_PRIORITY = Priorities.USER + 1;
    public static final int ALBUM_ACCESS_PRIORITY = Priorities.USER + 2;
    public static final int ALBUM_PERMISSION_ACCESS_PRIORITY = Priorities.USER + 3;
    public static final int UID_VALIDATOR_PRIORITY = Priorities.USER - 1;
    public static final int VIEWER_TOKEN_ACCESS_PRIORITY = Priorities.USER - 2;

    //size column in db
    public static class DB_COLUMN_SIZE {
        public static final int ALBUM_NAME = 255;
        public static final int WEBHOOK_NAME = 255;
        public static final int WEBHOOK_URL = 1024;
        public static final int WEBHOOK_SECRET = 1024;
        public static final int ALBUM_DESCRIPTION = 2048;
        public static final int COMMENT = 1024;
        public static final int CAPABILITY_DESCRIPTION = 255;
    }

    //JSON JWE
    public static class JWE {
        public static final String TOKEN = "token";
        public static final String SOURCE_ID = "sourceId";
        public static final String IS_INBOX = "isInbox";
        public static final String STUDY_INSTANCE_UID = "studyInstanceUID";
        public static final String EXP = "exp";
        public static final String SCOPE = "scope";
    }



}

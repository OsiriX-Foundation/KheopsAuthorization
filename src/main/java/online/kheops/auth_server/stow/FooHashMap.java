package online.kheops.auth_server.stow;

import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.entity.Study;
import online.kheops.auth_server.webhook.Source;


public interface FooHashMap {

    void addHashMapData(Study study, Series series,
                        Album destination, boolean isInbox,
                        boolean isNewStudy, boolean isNewSeries,
                        Source source, boolean isNewInDestination);
}

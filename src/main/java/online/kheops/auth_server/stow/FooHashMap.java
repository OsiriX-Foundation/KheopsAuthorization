package online.kheops.auth_server.stow;

import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.Instances;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.entity.Study;
import online.kheops.auth_server.webhook.Source;


public interface FooHashMap {

    void addHashMapData(Study study, Series series, Instances instances,
                        Album destination, boolean isInbox,
                        boolean isNewStudy, boolean isNewSeries, boolean isNewInstance,
                        Source source, boolean isNewInDestination);
}

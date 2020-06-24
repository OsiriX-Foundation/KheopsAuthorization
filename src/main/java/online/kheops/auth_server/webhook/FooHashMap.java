package online.kheops.auth_server.webhook;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FooHashMap {

    private static final Logger LOG = Logger.getLogger(FooHashMap.class.getName());

    private static FooHashMap instance = null;
    private final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final int TIME_TO_LIVE = 10;//seconds

    private Level0 level0;


    private FooHashMap() {
        level0 = new Level0();
    }

    public static synchronized FooHashMap getInstance() {
        if(instance != null) {
            return instance;
        }
        return instance = new FooHashMap();
    }

    public void addHashMapData(String studyUID, String seriesUID, String instancesUID, String destination, boolean isNewStudy, boolean isNewSeries, boolean isNewInstance, Source source) {
        SCHEDULER.schedule(() -> adddata(studyUID, seriesUID, instancesUID, isNewStudy,isNewSeries,isNewInstance, destination, source), 0, TimeUnit.SECONDS);
    }



    private void adddata(String studyUID, String seriesUID, String instancesUID, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, String destination, Source source) {
        if (level0.containsStudy(studyUID)) {
            level0.cancelScheduledFuture(studyUID);
        }
        level0.put(SCHEDULER.schedule(() -> callWebhook(studyUID), TIME_TO_LIVE, TimeUnit.SECONDS), studyUID, seriesUID, instancesUID, isNewStudy, isNewSeries, isNewInstances, destination, source);
    }


    private void callWebhook(String studyUID) {
        String log = "callWebhook:";
        log += level0.toString(studyUID);
        LOG.info(log);
        level0.remove(studyUID);
    }
}

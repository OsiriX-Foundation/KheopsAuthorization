package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FooHashMap {

    private static final Logger LOG = Logger.getLogger(FooHashMap.class.getName());

    private static FooHashMap instance = null;
    private final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final int TIME_TO_LIVE = 10;//seconds

    private HashMapStudyData hashMapData;
    private HashMap<String, ScheduledFuture<?>> hashMap30sec;


    private FooHashMap() {
        hashMapData = new HashMapStudyData();
        hashMap30sec = new HashMap<>();
    }

    public static synchronized FooHashMap getInstance() {
        if(instance != null) {
            return instance;
        }
        return instance = new FooHashMap();
    }

    public void addHashMapData(String studyUID, String seriesUID, String instancesUID, String destination) {
        SCHEDULER.schedule(() -> adddata(studyUID, seriesUID, instancesUID, destination), 0, TimeUnit.SECONDS);
    }


    private void adddata(String studyUID, String seriesUID, String instancesUID, String destination) {
        if (hashMap30sec.containsKey(studyUID)) {
            hashMap30sec.get(studyUID).cancel(true);
        }
        hashMapData.put(studyUID, seriesUID, instancesUID, destination);
        hashMap30sec.put(studyUID, SCHEDULER.schedule(() -> callWebhook(studyUID), TIME_TO_LIVE, TimeUnit.SECONDS));
    }

    private void callWebhook(String studyUID) {
        String log = "callWebhook: study:"+studyUID + " series/instances:";
        for (Map.Entry<String, PairDataInstanceDestination> i:hashMapData.get(studyUID).entrySet()) {
            log += i.getKey() +"/[";
            for (String j:i.getValue().getInstances()) {
                log += j + ",";
            }
            log += "destination: ";
            for (String j:i.getValue().getDestinations()) {
                log += j + ",";
            }
            log = log.substring(0,log.length()-1);
            log += "]--";
        }
        log = log.substring(0,log.length()-2);
        LOG.info(log);
        hashMap30sec.remove(studyUID);
        hashMapData.remove(studyUID);
    }
}

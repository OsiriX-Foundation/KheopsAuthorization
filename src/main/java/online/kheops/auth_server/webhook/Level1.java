package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class Level1 {
    private ScheduledFuture scheduledFuture;
    private boolean isNewStudy;
    private HashMap<String, Level2> level2;

    public Level1(ScheduledFuture scheduledFuture, boolean isNewStudy) {
        level2 = new HashMap<>();
        this.scheduledFuture = scheduledFuture;
        this.isNewStudy = isNewStudy;
    }

    public void addSeries(ScheduledFuture scheduledFuture, String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, String destination, Source source) {
        this.scheduledFuture = scheduledFuture;
        if (level2.containsKey(seriesUID)) {
            level2.get(seriesUID).addInstances(instancesUID, isNewInstances, destination, source);
        } else {
            level2.put(seriesUID, new Level2(instancesUID, isNewSeries, isNewInstances, destination, source));
        }
    }

    public boolean isNewStudy() { return isNewStudy; }
    public HashMap<String, Level2> getSeries() { return level2; }
    public boolean cancelScheduledFuture() {
        return scheduledFuture.cancel(true);
    }

    @Override
    public String toString() {
                String s = "{" +
                "isNewStudy=" + isNewStudy + " ";
        for(Map.Entry<String, Level2> level2Entry:level2.entrySet()) {
            s += "series:"+level2Entry.getKey() +" instances:"+ level2Entry.getValue().toString();
        }
        return s += '}';
    }
}

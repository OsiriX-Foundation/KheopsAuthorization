package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class Level1 {
    private ScheduledFuture scheduledFuture;
    private boolean isNewStudy;
    private HashMap<Source, Level2> level2;

    public Level1(ScheduledFuture scheduledFuture, boolean isNewStudy) {
        level2 = new HashMap<>();
        this.scheduledFuture = scheduledFuture;
        this.isNewStudy = isNewStudy;
    }

    public void addSeries(ScheduledFuture scheduledFuture, String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, Source source, String destination, boolean isNewInDestination) {
        this.scheduledFuture = scheduledFuture;
        if (level2.containsKey(seriesUID)) {
            level2.get(source).addInstances(seriesUID, instancesUID, isNewSeries, isNewInstances, destination, isNewInDestination);
        } else {
            level2.put(source, new Level2(seriesUID, instancesUID, isNewSeries, isNewInstances, destination, isNewInDestination));
        }
    }

    public boolean isNewStudy() { return isNewStudy; }
    public HashMap<Source, Level2> getSeries() { return level2; }
    public boolean cancelScheduledFuture() {
        return scheduledFuture.cancel(true);
    }

    @Override
    public String toString() {
                String s = "{" +
                "isNewStudy=" + isNewStudy + " ";
        for(Map.Entry<Source, Level2> level2Entry:level2.entrySet()) {
            s += "\n\tsource:"+level2Entry.getKey() +""+ level2Entry.getValue().toString();
        }
        return s += '}';
    }
}

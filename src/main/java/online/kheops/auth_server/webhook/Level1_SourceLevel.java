package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.Instances;
import online.kheops.auth_server.entity.Series;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class Level1_SourceLevel {
    private ScheduledFuture scheduledFuture;
    private boolean isNewStudy;
    private HashMap<Source, Level2_DestinationLevel> level2;

    public Level1_SourceLevel(ScheduledFuture scheduledFuture, boolean isNewStudy) {
        level2 = new HashMap<>();
        this.scheduledFuture = scheduledFuture;
        this.isNewStudy = isNewStudy;
    }

    public void addSeries(ScheduledFuture scheduledFuture, Series series, Instances instances, boolean isNewSeries, boolean isNewInstances, Source source, Album destination, boolean isNewInDestination) {
        this.scheduledFuture = scheduledFuture;
        if (level2.containsKey(source)) {
            level2.get(source).addDestination(series, instances, isNewSeries, isNewInstances, destination, isNewInDestination);
        } else {
            level2.put(source, new Level2_DestinationLevel(series, instances, isNewSeries, isNewInstances, destination, isNewInDestination));
        }
        if (isNewStudy) {
            this.isNewStudy = isNewStudy;
        }
    }

    public boolean isNewStudy() { return isNewStudy; }
    public Map<Source, Level2_DestinationLevel> getSources() { return level2; }
    public Level2_DestinationLevel getSource(Source source) { return level2.get(source); }
    public boolean cancelScheduledFuture() {
        return scheduledFuture.cancel(true);
    }

    @Override
    public String toString() {
                String s = "\n\t{isNewStudy=" + isNewStudy + " ";
        for(Map.Entry<Source, Level2_DestinationLevel> level2Entry:level2.entrySet()) {
            s += "\n\tsource:"+level2Entry.getKey() +""+ level2Entry.getValue().toString();
        }
        return s += '}';
    }
}

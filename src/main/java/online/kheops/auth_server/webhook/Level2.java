package online.kheops.auth_server.webhook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Level2 {
    //             Destination
    private HashMap<String, Level3> level3;

    public Level2() {
        level3 = new HashMap<>();
    }
    public Level2(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, String destination, boolean isNewInDestination) {
        level3 = new HashMap<>();
        level3.put(destination, new Level3(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination));
    }

    public void addDestination(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, String destination, boolean isNewInDestination) {
        if (level3.containsKey(destination)) {
            level3.get(destination).addSeries(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination);
        } else {
            level3.put(destination, new Level3(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination));
        }
    }

    public HashMap<String, Level3> getDestinations() { return level3; }
    public Level3 getDestination(String destination) { return level3.get(destination); }

    @Override
    public String toString() {
        String s = "{";
        for(Map.Entry<String, Level3> level3Entry:level3.entrySet()) {
            s += "\n\t\tdestination:" + level3Entry.getKey() + level3Entry.getValue().toString();
        }
        return s + '}';
    }
}

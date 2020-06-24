package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.Map;

public class Level2 {
    private boolean isNewSeries;
    private HashMap<String, Level3> level3;

    public Level2(boolean isNewSeries) {
        level3 = new HashMap<>();
        this.isNewSeries = isNewSeries;
    }
    public Level2(String instancesUID, boolean isNewSeries, boolean isNewInstances, String destination, Source source) {
        level3 = new HashMap<>();
        level3.put(instancesUID, new Level3(isNewInstances, destination, source));
        this.isNewSeries = isNewSeries;
    }

    public void addInstances(String instancesUID, boolean isNewInstances, String destination, Source source) {
        if (level3.containsKey(instancesUID)) {
            level3.get(instancesUID).addDestination(destination, source);
        } else {
            level3.put(instancesUID, new Level3(isNewInstances, destination, source));
        }
    }

    public boolean isNewSeries() { return isNewSeries; }
    public HashMap<String, Level3> getInstances() { return level3; }

    @Override
    public String toString() {
        String s = "{" +
                "isNewSeries=" + isNewSeries + " ";
        for(Map.Entry<String, Level3> level3Entry:level3.entrySet()) {
            s += "instanceUID:" +level3Entry.getKey() + level3Entry.getValue().toString();
        }
        return s + '}';
    }
}

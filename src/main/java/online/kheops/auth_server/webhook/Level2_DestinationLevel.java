package online.kheops.auth_server.webhook;

import java.util.*;

public class Level2_DestinationLevel {
    //             Destination
    private HashMap<String, Level3_SeriesLevel> level3;
    private HashMap<String, Set<String>> seriesNewInstances;

    public Level2_DestinationLevel(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, String destination, boolean isNewInDestination) {
        level3 = new HashMap<>();
        seriesNewInstances = new HashMap<>();
        level3.put(destination, new Level3_SeriesLevel(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination));
        if (isNewInstances) {
            final Set<String> instanceSet = new HashSet<>();
            instanceSet.add(instancesUID);
            seriesNewInstances.put(seriesUID, instanceSet);
        }
    }

    public void addDestination(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, String destination, boolean isNewInDestination) {
        if (level3.containsKey(destination)) {
            level3.get(destination).addSeries(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination);
        } else {
            level3.put(destination, new Level3_SeriesLevel(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination));
        }
        if (isNewInstances) {
            if (seriesNewInstances.containsKey(seriesUID)) {
                seriesNewInstances.get(seriesUID).add(instancesUID);
            } else {
                final Set<String> instanceSet = new HashSet<>();
                instanceSet.add(instancesUID);
                seriesNewInstances.put(seriesUID, instanceSet);
            }
        }
    }

    public Set<String> getSeriesNewInstances(String seriesUID) {
        return seriesNewInstances.get(seriesUID);
    }
    public HashMap<String, Set<String>> getSeriesNewInstances() { return seriesNewInstances; }

    public HashMap<String, Level3_SeriesLevel> getDestinations() { return level3; }
    public Level3_SeriesLevel getDestination(String destination) { return level3.get(destination); }

    @Override
    public String toString() {
        String s = "{";
        for(Map.Entry<String, Level3_SeriesLevel> level3Entry:level3.entrySet()) {
            s += "\n\t\tdestination:" + level3Entry.getKey() + level3Entry.getValue().toString();
        }
        return s + '}';
    }
}

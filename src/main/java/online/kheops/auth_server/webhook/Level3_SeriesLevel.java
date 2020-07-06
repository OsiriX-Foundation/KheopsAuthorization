package online.kheops.auth_server.webhook;

import java.util.*;

public class Level3_SeriesLevel {
    //              SeriesUID
    private HashMap<String, Level4_InstancesLevel> series;

    public Level3_SeriesLevel(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        this.series = new HashMap<>();
        addSeries(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination);
    }


    public void addSeries(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        if (series != null) {
            if (series.containsKey(seriesUID)) {
                series.get(seriesUID).addInstances(instancesUID, isNewSeries, isNewInstances);
            } else {
                series.put(seriesUID, new Level4_InstancesLevel(instancesUID, isNewSeries, isNewInstances, isNewInDestination));
            }
        }
    }

    public HashMap<String, Level4_InstancesLevel> getSeries() { return series; }
    public Level4_InstancesLevel getSeries(String series) { return this.series.get(series); }

    @Override
    public String toString() {
        String s = "\n\t\t\t{";
        for(Map.Entry<String, Level4_InstancesLevel> stringSetEntry:series.entrySet()) {
            s += "{series:" +stringSetEntry.getKey() +
                    stringSetEntry.getValue()+ "}";
        }
        s += '}';
        return s;
    }
}

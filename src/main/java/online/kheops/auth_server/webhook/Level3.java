package online.kheops.auth_server.webhook;

import java.util.*;

public class Level3 {
    //              SeriesUID
    private HashMap<String, Level4> series;

    public Level3(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        this.series = new HashMap<>();
        addSeries(seriesUID, instancesUID, isNewSeries, isNewInstances, isNewInDestination);
    }


    public void addSeries(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        if (series != null) {
            if (series.containsKey(seriesUID)) {
                series.get(seriesUID).addInstances(instancesUID, isNewInstances);
            } else {
                series.put(seriesUID, new Level4(instancesUID, isNewSeries, isNewInstances, isNewInDestination));
            }
        }
    }

    public HashMap<String, Level4> getSeries() { return series; }

    @Override
    public String toString() {
        String s = "\n\t\t\t{";
        for(Map.Entry<String, Level4> stringSetEntry:series.entrySet()) {
            s += "{series:" +stringSetEntry.getKey() +
                    stringSetEntry.getValue()+ "}";
        }
        s += '}';
        return s;
    }
}

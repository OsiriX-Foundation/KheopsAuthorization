package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.Instances;
import online.kheops.auth_server.entity.Series;

import java.util.*;

public class Level3_SeriesLevel {
    //              SeriesUID
    private Map<Series, Level4_InstancesLevel> series;

    public Level3_SeriesLevel(Series series, Instances instances, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        this.series = new HashMap<>();
        addSeries(series, instances, isNewSeries, isNewInstances, isNewInDestination);
    }


    public void addSeries(Series series, Instances instances, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        if (this.series != null) {
            if (this.series.containsKey(series)) {
                this.series.get(series).addInstances(instances, isNewSeries, isNewInstances);
            } else {
                this.series.put(series, new Level4_InstancesLevel(instances, isNewSeries, isNewInstances, isNewInDestination));
            }
        }
    }

    public Map<Series, Level4_InstancesLevel> getSeries() { return series; }
    public Level4_InstancesLevel getSeries(Series series) { return this.series.get(series); }

    @Override
    public String toString() {
        String s = "\n\t\t\t{";
        for(Map.Entry<Series, Level4_InstancesLevel> stringSetEntry:series.entrySet()) {
            s += "{series:" +stringSetEntry.getKey() +
                    stringSetEntry.getValue()+ "}";
        }
        s += '}';
        return s;
    }
}

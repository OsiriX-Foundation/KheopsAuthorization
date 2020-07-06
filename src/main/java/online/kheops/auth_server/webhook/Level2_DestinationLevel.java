package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.Instances;
import online.kheops.auth_server.entity.Series;

import java.util.*;

public class Level2_DestinationLevel {
    //             Destination
    private HashMap<Album, Level3_SeriesLevel> level3;
    private HashMap<Series, Set<Instances>> seriesNewInstances;

    public Level2_DestinationLevel(Series series, Instances instances, boolean isNewSeries, boolean isNewInstances, Album destination, boolean isNewInDestination) {
        level3 = new HashMap<>();
        seriesNewInstances = new HashMap<>();
        level3.put(destination, new Level3_SeriesLevel(series, instances, isNewSeries, isNewInstances, isNewInDestination));
        if (isNewInstances) {
            final Set<Instances> instanceSet = new HashSet<>();
            instanceSet.add(instances);
            seriesNewInstances.put(series, instanceSet);
        }
    }

    public void addDestination(Series series, Instances instances, boolean isNewSeries, boolean isNewInstances, Album destination, boolean isNewInDestination) {
        if (level3.containsKey(destination)) {
            level3.get(destination).addSeries(series, instances, isNewSeries, isNewInstances, isNewInDestination);
        } else {
            level3.put(destination, new Level3_SeriesLevel(series, instances, isNewSeries, isNewInstances, isNewInDestination));
        }
        if (isNewInstances) {
            if (seriesNewInstances.containsKey(series)) {
                seriesNewInstances.get(series).add(instances);
            } else {
                final Set<Instances> instanceSet = new HashSet<>();
                instanceSet.add(instances);
                seriesNewInstances.put(series, instanceSet);
            }
        }
    }

    public Set<Instances> getSeriesNewInstances(Series series) {
        return seriesNewInstances.containsKey(series) ? seriesNewInstances.get(series) : new HashSet<>();
    }
    public HashMap<Series, Set<Instances>> getSeriesNewInstances() { return seriesNewInstances; }

    public HashMap<Album, Level3_SeriesLevel> getDestinations() { return level3; }
    public Level3_SeriesLevel getDestination(String destination) { return level3.get(destination); }

    @Override
    public String toString() {
        String s = "{";
        for(Map.Entry<Album, Level3_SeriesLevel> level3Entry:level3.entrySet()) {
            s += "\n\t\tdestination:" + level3Entry.getKey() + level3Entry.getValue().toString();
        }
        return s + '}';
    }
}

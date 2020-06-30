package online.kheops.auth_server.webhook;

import java.util.*;

public class Level3 {

    private HashMap<String, Boolean> instances;
    private boolean isNewInDestination;

    public Level3(String seriesUID, String instancesUID, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        this.instances = new HashMap<>();
        addInstances(instancesUID, isNewInstances);
        this.isNewInDestination = isNewInDestination;

    }


    public void addInstances(String instancesUID, boolean isNewInstances) {
        if (instances != null) {
            if (!instances.containsKey(instancesUID)) {
                instances.put(instancesUID, isNewInstances);
            }
        }
    }

    @Override
    public String toString() {
        String s = "\n\t\t\t{";
        for(Map.Entry<String, Boolean> stringSetEntry:destinations.entrySet()) {
            s += "{destinations:" +stringSetEntry.getKey() +
                    " new_in_destination:" + stringSetEntry.getValue()+ "}";
        }
        s += "\n\t\t\t";
        for(Map.Entry<String, Boolean> stringSetEntry:instances.entrySet()) {
            s += "{instances:" +stringSetEntry.getKey() +
                    " is_new:" + stringSetEntry.getValue() + "}";
        }
        s += '}';
        return s;
    }
}

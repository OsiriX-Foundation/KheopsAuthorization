package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Level4_InstancesLevel {
    private boolean isNewSeries;
    private boolean isNewInDestination;
    private HashMap<String, Boolean> instances;

    public Level4_InstancesLevel(String instancesUID, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        this.isNewSeries = isNewSeries;
        this.isNewInDestination = isNewInDestination;
        this.instances = new HashMap<>();
        instances.put(instancesUID, isNewInstances);
    }

    public void addInstances(String instancesUID, boolean isNewSeries, boolean isNewInstances)  {
        if (instances.containsKey(instancesUID)) {
            if (isNewInstances) {
                instances.put(instancesUID, isNewInstances);
            }
        } else {
            instances.put(instancesUID, isNewInstances);
        }
        if (isNewSeries) {
            this.isNewSeries = isNewSeries;
        }
    }

    public HashMap<String, Boolean> getInstances() { return instances; }
    public Set<String> getInstancesUIDLst() { return instances.keySet(); }

    public boolean isNewInstances(String instanceUID) { return instances.get(instanceUID); }

    public boolean isNewSeries() { return isNewSeries; }
    public boolean isNewInDestination() { return isNewInDestination; }

    @Override
    public String toString() {
        String s = "\n\t\t\t\t{";
        s += "is_new_series:" + isNewSeries;
        s += "\n\t\t\t\tis_new_in_destination:" + isNewInDestination;
        for(Map.Entry<String, Boolean> stringSetEntry:instances.entrySet()) {
            s += "\n\t\t\t\t{instances:" +stringSetEntry.getKey() +
                    " is_new_instances:"+stringSetEntry.getValue()+ "}";
        }
        return s;
    }
}

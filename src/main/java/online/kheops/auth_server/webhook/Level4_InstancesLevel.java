package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.Instances;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Level4_InstancesLevel {
    private boolean isNewSeries;
    private boolean isNewInDestination;
    private HashMap<Instances, Boolean> instances;

    public Level4_InstancesLevel(Instances instances, boolean isNewSeries, boolean isNewInstances, boolean isNewInDestination) {
        this.isNewSeries = isNewSeries;
        this.isNewInDestination = isNewInDestination;
        this.instances = new HashMap<>();
        this.instances.put(instances, isNewInstances);
    }

    public void addInstances(Instances instances, boolean isNewSeries, boolean isNewInstances)  {
        if (this.instances.containsKey(instances)) {
            if (isNewInstances) {
                this.instances.put(instances, isNewInstances);
            }
        } else {
            this.instances.put(instances, isNewInstances);
        }
        if (isNewSeries) {
            this.isNewSeries = isNewSeries;
        }
    }

    public Map<Instances, Boolean> getInstances() { return instances; }
    public Set<Instances> getInstancesUIDLst() { return instances.keySet(); }


    public boolean isNewSeries() { return isNewSeries; }
    public boolean isNewInDestination() { return isNewInDestination; }

    @Override
    public String toString() {
        String s = "\n\t\t\t\t{";
        s += "is_new_series:" + isNewSeries;
        s += "\n\t\t\t\tis_new_in_destination:" + isNewInDestination;
        for(Map.Entry<Instances, Boolean> stringSetEntry:instances.entrySet()) {
            s += "\n\t\t\t\t{instances:" +stringSetEntry.getKey() +
                    " is_new_instances:"+stringSetEntry.getValue()+ "}";
        }
        return s;
    }
}

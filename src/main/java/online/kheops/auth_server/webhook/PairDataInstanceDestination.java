package online.kheops.auth_server.webhook;


import java.util.HashSet;
import java.util.Set;

public class PairDataInstanceDestination {

    private Set<String> destinations;
    private Set<String> instances;

    public PairDataInstanceDestination() {
        destinations = new HashSet<>();
        instances = new HashSet<>();
    }

    public void put(String instancesUID, String destination) {
        if (destination != null) {
            destinations.add(destination);
        }
        instances.add(instancesUID);
    }

    public Set<String> getDestinations() {
        return destinations;
    }

    public Set<String> getInstances() {
        return instances;
    }
}

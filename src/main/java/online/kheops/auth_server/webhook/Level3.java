package online.kheops.auth_server.webhook;

import java.util.*;

public class Level3 {
    private HashMap<String, Set<Source>> destinations;
    private boolean isNewInstances;

    public Level3(boolean isNewInstances, String destination, Source source) {
        this.destinations = new HashMap<>();
        addDestination(destination, source);
        this.isNewInstances = isNewInstances;
    }

    public void addDestination(String destination, Source source) {
        if (destination != null) {
            if (destinations.containsKey(destination)) {
                destinations.get(destination).add(source);
            } else {
                Set<Source> sources = new HashSet<>();
                sources.add(source);
                destinations.put(destination, sources);
            }
        }
    }

    @Override
    public String toString() {
        String s = "{";
        for(Map.Entry<String, Set<Source>> stringSetEntry:destinations.entrySet()) {
            s += "destinations:" +stringSetEntry.getKey();
            for (Source source: stringSetEntry.getValue()) {
                s += " " + source.toString();
            }
        }
        s += ", isNewInstances=" + isNewInstances +
                '}';
        return s;
    }
}

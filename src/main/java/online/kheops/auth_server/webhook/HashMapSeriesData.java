package online.kheops.auth_server.webhook;


import java.util.ArrayList;
import java.util.HashMap;

public class HashMapSeriesData extends HashMap<String, PairDataInstanceDestination>{

    public void put(String seriesUID, String instancesUID, String destination) {
       if (this.containsKey(seriesUID)) {
           this.get(seriesUID).put(instancesUID, destination);
       } else {
           PairDataInstanceDestination pairDataInstanceDestination = new PairDataInstanceDestination();
           pairDataInstanceDestination.put(instancesUID, destination);
           this.put(seriesUID, pairDataInstanceDestination);
       }
    }
}

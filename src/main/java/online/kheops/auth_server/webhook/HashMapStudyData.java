package online.kheops.auth_server.webhook;


import java.util.HashMap;

public class HashMapStudyData extends HashMap<String, HashMapSeriesData>{


    public void put(String studyUID, String seriesUID, String instancesUID, String destination) {
        if (this.containsKey(studyUID)) {
            this.get(studyUID).put(seriesUID, instancesUID, destination);
        } else {
            HashMapSeriesData hashMapSeriesData = new HashMapSeriesData();
            hashMapSeriesData.put(seriesUID, instancesUID, destination);
            this.put(studyUID, hashMapSeriesData);
        }
    }
}

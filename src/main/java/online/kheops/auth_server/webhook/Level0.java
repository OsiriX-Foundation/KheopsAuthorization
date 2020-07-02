package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

public class Level0 {
    //              StudyUID
    private HashMap<String, Level1> studyHashMap;

    public Level0() {
        studyHashMap = new HashMap<>();
    }

    public boolean containsStudy(String studyUID) {
        return studyHashMap.containsKey(studyUID);
    }

    public Level1 get(String studyUID) {
        return studyHashMap.get(studyUID);
    }

    public Level1 put(ScheduledFuture scheduledFuture, String studyUID, String seriesUID, String instancesUID, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, Source source, String destination, boolean isNewInDestination) {
        if (studyHashMap.containsKey(studyUID)) {
            studyHashMap.get(studyUID).addSeries(scheduledFuture, seriesUID, instancesUID, isNewSeries, isNewInstances, source, destination, isNewInDestination);
            return studyHashMap.get(studyUID);
        } else {
            Level1 level1 = new Level1(scheduledFuture, isNewStudy);
            level1.addSeries(scheduledFuture, seriesUID, instancesUID, isNewSeries, isNewInstances, source, destination, isNewInDestination);
            return studyHashMap.put(studyUID, level1);
        }
    }


    public String toString(String studyUID) {
        if(this.containsStudy(studyUID)) {
            return "\nstudy:" + studyUID + studyHashMap.get(studyUID).toString();
        } else {
            return "empty";
        }
    }

    public Level1 remove(String studyUID) {
        return studyHashMap.remove(studyUID);
    }
}

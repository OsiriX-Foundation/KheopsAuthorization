package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

public class Level0_StudyLevel {
    //              StudyUID
    private HashMap<String, Level1_SourceLevel> studyHashMap;

    public Level0_StudyLevel() {
        studyHashMap = new HashMap<>();
    }

    public boolean containsStudy(String studyUID) {
        return studyHashMap.containsKey(studyUID);
    }

    public Level1_SourceLevel get(String studyUID) {
        return studyHashMap.get(studyUID);
    }

    public Level1_SourceLevel put(ScheduledFuture scheduledFuture, String studyUID, String seriesUID, String instancesUID, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, Source source, String destination, boolean isNewInDestination) {
        if (studyHashMap.containsKey(studyUID)) {
            studyHashMap.get(studyUID).addSeries(scheduledFuture, seriesUID, instancesUID, isNewSeries, isNewInstances, source, destination, isNewInDestination);
            return studyHashMap.get(studyUID);
        } else {
            Level1_SourceLevel level1SourceLevel = new Level1_SourceLevel(scheduledFuture, isNewStudy);
            level1SourceLevel.addSeries(scheduledFuture, seriesUID, instancesUID, isNewSeries, isNewInstances, source, destination, isNewInDestination);
            return studyHashMap.put(studyUID, level1SourceLevel);
        }
    }


    public String toString(String studyUID) {
        if(this.containsStudy(studyUID)) {
            return "\nstudy:" + studyUID + studyHashMap.get(studyUID).toString();
        } else {
            return "empty";
        }
    }

    public Level1_SourceLevel remove(String studyUID) {
        return studyHashMap.remove(studyUID);
    }
}

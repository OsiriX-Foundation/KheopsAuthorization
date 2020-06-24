package online.kheops.auth_server.webhook;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

public class Level0 extends HashMap<String, Level1> {

    public boolean containsStudy(String studyUID) {
        return super.containsKey(studyUID);
    }
    public boolean cancelScheduledFuture(String studyUID) {
        return this.get(studyUID).cancelScheduledFuture();
    }

    public Level1 put(ScheduledFuture scheduledFuture, String studyUID, String seriesUID, String instancesUID, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, String destination, Source source) {
        if (containsKey(studyUID)) {
            get(studyUID).addSeries(scheduledFuture, seriesUID, instancesUID, isNewSeries, isNewInstances, destination, source);
            return get(studyUID);
        } else {
            Level1 level1 = new Level1(scheduledFuture, isNewStudy);
            level1.addSeries(scheduledFuture, seriesUID, instancesUID, isNewSeries, isNewInstances, destination, source);
            return super.put(studyUID, level1);
        }
    }


    public String toString(String studyUID) {
        if(this.containsStudy(studyUID)) {
            return "study:" + studyUID + this.get(studyUID).toString();
        } else {
            return "empty";
        }
    }
}

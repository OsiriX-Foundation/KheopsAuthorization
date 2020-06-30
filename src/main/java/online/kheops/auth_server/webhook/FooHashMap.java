package online.kheops.auth_server.webhook;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.series.SeriesNotFoundException;
import online.kheops.auth_server.series.SeriesResponse;
import online.kheops.auth_server.util.ErrorResponse;
import online.kheops.auth_server.util.KheopsLogBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static online.kheops.auth_server.series.Series.getSeries;
import static online.kheops.auth_server.series.SeriesQueries.findSeriesListByStudyUIDFromInbox;
import static online.kheops.auth_server.util.ErrorResponse.Message.SERIES_NOT_FOUND;

public class FooHashMap {

    private static final Logger LOG = Logger.getLogger(FooHashMap.class.getName());

    private static FooHashMap instance = null;
    private final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final int TIME_TO_LIVE = 10;//seconds

    private Level0 level0;


    private FooHashMap() {
        level0 = new Level0();
    }

    public static synchronized FooHashMap getInstance() {
        if(instance != null) {
            return instance;
        }
        return instance = new FooHashMap();
    }

    public void addHashMapData(String studyUID, String seriesUID, String instancesUID, String destination, boolean isNewStudy, boolean isNewSeries, boolean isNewInstance, Source source, boolean isNewInDestination) {
        SCHEDULER.schedule(() -> addData(studyUID, seriesUID, instancesUID, isNewStudy, isNewSeries, isNewInstance, destination, source, isNewInDestination), 0, TimeUnit.SECONDS);
    }


    private void addData(String studyUID, String seriesUID, String instancesUID, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, String destination, Source source, boolean isNewInDestination) {
        if (level0.containsStudy(studyUID)) {
            level0.get(studyUID).cancelScheduledFuture();
        }
        level0.put(SCHEDULER.schedule(() -> callWebhook(studyUID), TIME_TO_LIVE, TimeUnit.SECONDS), studyUID, seriesUID, instancesUID, isNewStudy, isNewSeries, isNewInstances, source, destination, isNewInDestination);
    }


    private void callWebhook(String studyUID) {
        String log = "callWebhook:";
        log += level0.toString(studyUID);
        LOG.info(log);

        Level1 levl1 = level0.get(studyUID);

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            if (levl1.isNewStudy()) {
                //envoyer uniquement aux destination en fonction de chaque source
                HashMap<String, SeriesResponse> seriesResponseHashMap = new HashMap<>();
                ArrayList<Source> sourceLst = new ArrayList<>();
                for (Map.Entry<String, Level2> level2Entry : levl1.getSeries().entrySet()) {
                    //pour chaque series
                    for (Source source :level2Entry.getValue().getSources()) {

                    }
                    seriesResponseHashMap.put(level2Entry.getKey(), new SeriesResponse(getSeries(level2Entry.getKey(), em)));
                }

                seriesResponseHashMap.keySet();
            } else {

            }

            tx.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }

        level0.remove(studyUID);
    }

}

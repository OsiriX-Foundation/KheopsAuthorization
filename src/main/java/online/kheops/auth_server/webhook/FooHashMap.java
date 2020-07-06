package online.kheops.auth_server.webhook;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static online.kheops.auth_server.album.AlbumQueries.findAlbumsWithEnabledNewSeriesWebhooks;
import static online.kheops.auth_server.album.Albums.getAlbum;
import static online.kheops.auth_server.instances.Instances.getInstances;
import static online.kheops.auth_server.series.Series.getSeries;
import static online.kheops.auth_server.study.Studies.getStudy;

public class FooHashMap {

    private static final Logger LOG = Logger.getLogger(FooHashMap.class.getName());

    private static FooHashMap instance = null;
    private static String kheopsInstance;
    private final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final int TIME_TO_LIVE = 10;//seconds

    private Level0_StudyLevel level0StudyLevel;


    private FooHashMap() {
        level0StudyLevel = new Level0_StudyLevel();
    }

    public static synchronized FooHashMap getInstance() {
        if(instance != null) {
            return instance;
        }
        instance = new FooHashMap();
        return instance;
    }

    public void setKheopsInstance(String kheopsInstance) {
        this.kheopsInstance = kheopsInstance;
    }

    public void addHashMapData(Study study, Series series, Instances instances, Album destination, boolean isNewStudy, boolean isNewSeries, boolean isNewInstance, Source source, boolean isNewInDestination) {
        SCHEDULER.schedule(() -> addData(study, series, instances, isNewStudy, isNewSeries, isNewInstance, destination, source, isNewInDestination), 0, TimeUnit.SECONDS);
    }


    private void addData(Study study, Series series, Instances instances, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, Album destination, Source source, boolean isNewInDestination) {
        if (level0StudyLevel.containsStudy(study)) {
            level0StudyLevel.get(study).cancelScheduledFuture();
        }
        level0StudyLevel.put(SCHEDULER.schedule(() -> callWebhook(study), TIME_TO_LIVE, TimeUnit.SECONDS), study, series, instances, isNewStudy, isNewSeries, isNewInstances, source, destination, isNewInDestination);
    }


    private void callWebhook(Study study) {
        String log = "callWebhook:";
        log += level0StudyLevel.toString(study);
        LOG.info(log);

        final Level1_SourceLevel level1SourceLevel = level0StudyLevel.get(study);

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            study = em.merge(study);

            for (Map.Entry<Source, Level2_DestinationLevel> entry : level1SourceLevel.getSources().entrySet()) {
                final Source source = entry.getKey();
                final Level2_DestinationLevel level2DestinationLevel = entry.getValue();

                if (level1SourceLevel.isNewStudy()) {
                    for (Map.Entry<Album, Level3_SeriesLevel> entry1 : level2DestinationLevel.getDestinations().entrySet()) {
                        final Album album = em.merge(entry1.getKey());
                        final Level3_SeriesLevel level3SeriesLevel = entry1.getValue();

                        if (!album.getWebhooks().isEmpty()) {
                            //if destination contain webhooks

                            final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                    .setDestination(album.getId())
                                    .isUpload()
                                    .setIsManualTrigger(false)
                                    .setStudy(study, kheopsInstance)
                                    .setSource(source)
                                    .setKheopsInstance(kheopsInstance);

                            for (Map.Entry<Series, Level4_InstancesLevel> entry2 : level3SeriesLevel.getSeries().entrySet()) {
                                final Series series = em.merge(entry2.getKey());
                                final Level4_InstancesLevel level4InstancesLevel = entry2.getValue();

                                newSeriesWebhookBuilder.addSeries(series);
                                for (Instances instance : level4InstancesLevel.getInstancesUIDLst()) {
                                    newSeriesWebhookBuilder.addInstances(instance);
                                }
                                for (Instances instance : level2DestinationLevel.getSeriesNewInstances(series)) {
                                    newSeriesWebhookBuilder.addInstances(instance);
                                }
                            }

                            for (Webhook webhook : album.getWebhooks()) {
                                if (webhook.isEnabled() && webhook.isNewSeries()) {
                                    final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                    newSeriesWebhookBuilder.getSeriesInstancesHashMap().forEach((series, instances) -> webhookTrigger.addSeries(series));
                                    em.persist(webhookTrigger);
                                    new WebhookAsyncRequest(webhook, newSeriesWebhookBuilder.build(), webhookTrigger).firstRequest();
                                }
                            }
                        }
                    }
                } else {
                    for (Album album : findAlbumsWithEnabledNewSeriesWebhooks(study.getStudyInstanceUID(), em)) {

                        final ArrayList<Series> seriesLstForWebhookTrigger = new ArrayList<>();
                        final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                .setDestination(album.getId())
                                .isUpload()
                                .setIsManualTrigger(false)
                                .setStudy(study, kheopsInstance)
                                .setSource(source)
                                .setKheopsInstance(kheopsInstance);

                        if (level2DestinationLevel.getDestinations().containsKey(album.getId())) {
                            for (Map.Entry<Series, Level4_InstancesLevel> entry2 : level2DestinationLevel.getDestination(album.getId()).getSeries().entrySet()) {
                                final Series series = em.merge(entry2.getKey());
                                final Level4_InstancesLevel level4InstancesLevel = entry2.getValue();
                                seriesLstForWebhookTrigger.add(series);
                                newSeriesWebhookBuilder.addSeries(series);
                                for (Instances instance : level4InstancesLevel.getInstances().keySet()) {
                                    newSeriesWebhookBuilder.addInstances(instance);
                                }
                                for (Instances instance : level2DestinationLevel.getSeriesNewInstances(series)) {
                                    newSeriesWebhookBuilder.addInstances(instance);
                                }
                            }
                        } else {
                            for (Map.Entry<Series, Set<Instances>> seriesUIDSetInstancesUID : level2DestinationLevel.getSeriesNewInstances().entrySet()) {
                                final Series series = em.merge(seriesUIDSetInstancesUID.getKey());
                                final Set<Instances> instancesUIDSet = seriesUIDSetInstancesUID.getValue();
                                if (album.containsSeries(series, em)) {
                                    seriesLstForWebhookTrigger.add(series);
                                    newSeriesWebhookBuilder.addSeries(series);
                                    for (Instances i : instancesUIDSet) {
                                        newSeriesWebhookBuilder.addInstances(i);
                                    }
                                }
                            }
                        }

                        if (!seriesLstForWebhookTrigger.isEmpty()) {
                            for (Webhook webhook : album.getWebhooks()) {
                                if (webhook.isEnabled() && webhook.isNewSeries()) {
                                    final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                    seriesLstForWebhookTrigger.forEach(webhookTrigger::addSeries);
                                    em.persist(webhookTrigger);
                                    new WebhookAsyncRequest(webhook, newSeriesWebhookBuilder.build(), webhookTrigger).firstRequest();
                                }
                            }
                        }
                    }
                }
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

        level0StudyLevel.remove(study);
    }

}

package online.kheops.auth_server.webhook;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.event.Events;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static online.kheops.auth_server.album.AlbumQueries.findAlbumsWithEnabledNewSeriesWebhooks;
import static online.kheops.auth_server.event.Events.albumPostStudyMutation;

public class FooHashMap {

    private static final Logger LOG = Logger.getLogger(FooHashMap.class.getName());

    private static FooHashMap instance = null;
    private static String kheopsInstance;
    private final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final int TIME_TO_LIVE = 30;//seconds

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

            final List<WebhookAsyncRequest> webhookAsyncRequests = new ArrayList<>();

            for (Map.Entry<Source, Level2_DestinationLevel> entry : level1SourceLevel.getSources().entrySet()) {
                final Source source = entry.getKey();
                final Level2_DestinationLevel level2DestinationLevel = entry.getValue();

                if (level1SourceLevel.isNewStudy()) {
                    for (Map.Entry<Album, Level3_SeriesLevel> entry1 : level2DestinationLevel.getDestinations().entrySet()) {
                        final Album album = em.merge(entry1.getKey());
                        final Level3_SeriesLevel level3SeriesLevel = entry1.getValue();

                        if (!album.getWebhooksNewSeriesEnabled().isEmpty()) {
                            //if destination contain webhooks

                            final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                    .setDestination(album.getId())
                                    .isUpload()
                                    .isAutomatedTrigger()
                                    .setStudy(study)
                                    .setSource(source)
                                    .setKheopsInstance(kheopsInstance);

                            for (Map.Entry<Series, Level4_InstancesLevel> entry2 : level3SeriesLevel.getSeries().entrySet()) {
                                final Series series = em.merge(entry2.getKey());
                                final Level4_InstancesLevel level4InstancesLevel = entry2.getValue();
                                newSeriesWebhookBuilder.addSeries(series);
                                level4InstancesLevel.getInstancesUIDLst().forEach(newSeriesWebhookBuilder::addInstances);
                                level2DestinationLevel.getSeriesNewInstances(series).forEach(newSeriesWebhookBuilder::addInstances);
                            }

                            for (Webhook webhook : album.getWebhooksNewSeriesEnabled()) {
                                final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                newSeriesWebhookBuilder.getSeriesInstancesHashMap().forEach((series, instances) -> webhookTrigger.addSeries(series));
                                em.persist(webhookTrigger);
                                webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, newSeriesWebhookBuilder.build(), webhookTrigger));
                            }
                        }

                        final Mutation mutation;
                        if (source.getCapabilityToken().isPresent()) {
                            mutation = albumPostStudyMutation(em.merge(source.getCapabilityToken().get()), album, Events.MutationType.IMPORT_STUDY, study, new ArrayList<>(level3SeriesLevel.getSeries().keySet()));
                        } else {
                            mutation = albumPostStudyMutation(em.merge(source.getUser()), album, Events.MutationType.IMPORT_STUDY, study, new ArrayList<>(level3SeriesLevel.getSeries().keySet()));
                        }
                        em.persist(mutation);
                    }
                } else {
                    final List<Album> albumLst = findAlbumsWithEnabledNewSeriesWebhooks(study.getStudyInstanceUID(), em);
                    for (Album album : albumLst) {

                        final List<Series> seriesLstForWebhookTrigger = new ArrayList<>();
                        final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                .setDestination(album.getId())
                                .isUpload()
                                .isAutomatedTrigger()
                                .setStudy(study)
                                .setSource(source)
                                .setKheopsInstance(kheopsInstance);

                        if (level2DestinationLevel.getDestinations().containsKey(album)) {
                            List<Series> newSeriesInDestinationLst = new ArrayList<>();
                            for (Map.Entry<Series, Level4_InstancesLevel> entry2 : level2DestinationLevel.getDestination(album).getSeries().entrySet()) {
                                final Series series = em.merge(entry2.getKey());
                                final Level4_InstancesLevel level4InstancesLevel = entry2.getValue();
                                seriesLstForWebhookTrigger.add(series);
                                newSeriesWebhookBuilder.addSeries(series);
                                level4InstancesLevel.getInstancesUIDLst().forEach(newSeriesWebhookBuilder::addInstances);
                                level2DestinationLevel.getSeriesNewInstances(series).forEach(newSeriesWebhookBuilder::addInstances);
                                if (level4InstancesLevel.isNewInDestination()) {
                                    newSeriesInDestinationLst.add(series);
                                }
                            }

                            if (!newSeriesInDestinationLst.isEmpty()) {
                                final Mutation mutation;
                                if (source.getCapabilityToken().isPresent()) {
                                    mutation = albumPostStudyMutation(em.merge(source.getCapabilityToken().get()), album, Events.MutationType.IMPORT_STUDY, study, newSeriesInDestinationLst);
                                } else {
                                    mutation = albumPostStudyMutation(em.merge(source.getUser()), album, Events.MutationType.IMPORT_STUDY, study, newSeriesInDestinationLst);
                                }
                                em.persist(mutation);
                            }
                        } else {
                            for (Map.Entry<Series, Set<Instances>> seriesUIDSetInstancesUID : level2DestinationLevel.getSeriesNewInstances().entrySet()) {
                                final Series series = em.merge(seriesUIDSetInstancesUID.getKey());
                                if (album.containsSeries(series, em)) {
                                    seriesLstForWebhookTrigger.add(series);
                                    newSeriesWebhookBuilder.addSeries(series);
                                    seriesUIDSetInstancesUID.getValue().forEach(newSeriesWebhookBuilder::addInstances);
                                }
                            }
                        }

                        if (!seriesLstForWebhookTrigger.isEmpty()) {
                            for (Webhook webhook : album.getWebhooksNewSeriesEnabled()) {
                                final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                seriesLstForWebhookTrigger.forEach(webhookTrigger::addSeries);
                                em.persist(webhookTrigger);
                                webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, newSeriesWebhookBuilder.build(), webhookTrigger));
                            }
                        }
                    }
                }
            }

            /*
            new study in destination (with list of series)=>si fooHashMap NE contient PAS la study
            new series in destination =>si fooHashMap NE contient PAS la séries
            ?? new instances ?? (update series) or (add instances in series)
             */


            tx.commit();
            for (WebhookAsyncRequest webhookAsyncRequest : webhookAsyncRequests) {
                webhookAsyncRequest.firstRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
            level0StudyLevel.remove(study);
        }
    }

}

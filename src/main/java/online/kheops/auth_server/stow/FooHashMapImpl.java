package online.kheops.auth_server.stow;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.KheopsInstance;
import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.event.Events;
import online.kheops.auth_server.event.MutationType;
import online.kheops.auth_server.webhook.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static online.kheops.auth_server.album.AlbumQueries.findAlbumsWithEnabledNewSeriesWebhooks;
import static online.kheops.auth_server.event.Events.albumPostStudyMutation;

public class FooHashMapImpl implements FooHashMap{

    @Inject
    KheopsInstance kheopsInstance;

    private static final Logger LOG = Logger.getLogger(FooHashMapImpl.class.getName());

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int TIME_TO_LIVE = 10;//seconds

    private static Level0_StudyLevel level0StudyLevel = new Level0_StudyLevel();

    public FooHashMapImpl() { /*empty*/ }

    public void addHashMapData(Study study, Series series, Instances instances, Album destination, boolean isInbox, boolean isNewStudy, boolean isNewSeries, boolean isNewInstance, Source source, boolean isNewInDestination) {
        scheduler.schedule(() -> addData(study, series, instances, isNewStudy, isNewSeries, isNewInstance, destination, isInbox, source, isNewInDestination), 0, TimeUnit.SECONDS);
    }


    private void addData(Study study, Series series, Instances instances, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, Album destination, boolean isInbox, Source source, boolean isNewInDestination) {
        if (level0StudyLevel.containsStudy(study)) {
            level0StudyLevel.get(study).cancelScheduledFuture();
        }
        level0StudyLevel.put(scheduler.schedule(() -> callWebhook(study), TIME_TO_LIVE, TimeUnit.SECONDS), study, series, instances, isNewStudy, isNewSeries, isNewInstances, source, destination, isInbox, isNewInDestination);
    }

    private void callWebhook(Study studyIn) {
        String log = "callWebhook:";
        log += level0StudyLevel.toString(studyIn);
        LOG.info(log);

        final Level1_SourceLevel level1SourceLevel = level0StudyLevel.get(studyIn);

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            final Study study = em.merge(studyIn);

            final List<WebhookAsyncRequest> webhookAsyncRequests = new ArrayList<>();

            for (Map.Entry<Source, Level2_DestinationLevel> entry : level1SourceLevel.getSources().entrySet()) {
                final Source source = entry.getKey();
                final Level2_DestinationLevel level2DestinationLevel = entry.getValue();

                if (level1SourceLevel.isNewStudy()) {
                    for (Map.Entry<Album, Level3_SeriesLevel> entry1 : level2DestinationLevel.getDestinations().entrySet()) {
                        final Album album = em.merge(entry1.getKey());
                        final Level3_SeriesLevel level3SeriesLevel = entry1.getValue();

                        if (!level3SeriesLevel.isInbox()) {

                            if (!album.getWebhooksNewSeriesEnabled().isEmpty()) {
                                //if destination contain webhooks

                                final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                        .setDestination(album.getId())
                                        .isUpload()
                                        .isAutomatedTrigger()
                                        .setKheopsInstance(kheopsInstance.get())
                                        .setStudy(study)
                                        .setSource(source);

                                for (Map.Entry<Series, Level4_InstancesLevel> entry2 : level3SeriesLevel.getSeries().entrySet()) {
                                    final Series series = em.merge(entry2.getKey());
                                    final Level4_InstancesLevel level4InstancesLevel = entry2.getValue();
                                    newSeriesWebhookBuilder.addSeries(series);
                                    level4InstancesLevel.getInstancesUIDLst().forEach(newSeriesWebhookBuilder::addInstances);
                                    level2DestinationLevel.getSeriesNewInstances(series).forEach(newSeriesWebhookBuilder::addInstances);
                                }

                                final NewSeriesWebhook newSeriesWebhook = newSeriesWebhookBuilder.build();
                                for (Webhook webhook : album.getWebhooksNewSeriesEnabled()) {
                                    final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                    newSeriesWebhookBuilder.getSeriesInstancesHashMap().forEach((series, instances) -> webhookTrigger.addSeries(series));
                                    em.persist(webhookTrigger);
                                    webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, newSeriesWebhook, webhookTrigger));
                                }
                            }

                            source.getCapabilityToken().ifPresentOrElse(
                                    capability ->
                                            em.persist(albumPostStudyMutation(em.merge(capability), album, MutationType.IMPORT_STUDY, study, new ArrayList<>(level3SeriesLevel.getSeries().keySet()))),
                                    () ->
                                            em.persist(albumPostStudyMutation(em.merge(source.getUser()), album, MutationType.IMPORT_STUDY, study, new ArrayList<>(level3SeriesLevel.getSeries().keySet())))
                            );
                        }
                    }
                } else {
                    final List<Album> albumLst = findAlbumsWithEnabledNewSeriesWebhooks(study.getStudyInstanceUID(), em);
                    for (Album album : albumLst) {

                        final List<Series> seriesLstForWebhookTrigger = new ArrayList<>();
                        final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                .setDestination(album.getId())
                                .isUpload()
                                .isAutomatedTrigger()
                                .setKheopsInstance(kheopsInstance.get())
                                .setStudy(study)
                                .setSource(source);

                        if (level2DestinationLevel.getDestinations().containsKey(album)) {
                            if (!level2DestinationLevel.getDestination(album).isInbox()) {
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
                                    source.getCapabilityToken().ifPresentOrElse(
                                            capability ->
                                                    em.persist(albumPostStudyMutation(em.merge(capability), album, MutationType.IMPORT_STUDY, study, newSeriesInDestinationLst)),
                                            () ->
                                                    em.persist(albumPostStudyMutation(em.merge(source.getUser()), album, MutationType.IMPORT_STUDY, study, newSeriesInDestinationLst))
                                    );
                                }
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
                            final NewSeriesWebhook newSeriesWebhook = newSeriesWebhookBuilder.build();
                            for (Webhook webhook : album.getWebhooksNewSeriesEnabled()) {
                                final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                seriesLstForWebhookTrigger.forEach(webhookTrigger::addSeries);
                                em.persist(webhookTrigger);
                                webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, newSeriesWebhook, webhookTrigger));
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
            LOG.log(Level.WARNING,"", e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
            level0StudyLevel.remove(studyIn);
        }
    }

}

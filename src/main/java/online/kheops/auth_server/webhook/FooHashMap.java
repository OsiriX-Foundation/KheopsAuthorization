package online.kheops.auth_server.webhook;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.entity.Webhook;
import online.kheops.auth_server.entity.WebhookTrigger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static online.kheops.auth_server.album.AlbumQueries.findAlbumLstForWebhook;
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

    public void addHashMapData(String studyUID, String seriesUID, String instancesUID, String destination, boolean isNewStudy, boolean isNewSeries, boolean isNewInstance, Source source, boolean isNewInDestination) {
        SCHEDULER.schedule(() -> addData(studyUID, seriesUID, instancesUID, isNewStudy, isNewSeries, isNewInstance, destination, source, isNewInDestination), 0, TimeUnit.SECONDS);
    }


    private void addData(String studyUID, String seriesUID, String instancesUID, boolean isNewStudy, boolean isNewSeries, boolean isNewInstances, String destination, Source source, boolean isNewInDestination) {
        if (level0StudyLevel.containsStudy(studyUID)) {
            level0StudyLevel.get(studyUID).cancelScheduledFuture();
        }
        level0StudyLevel.put(SCHEDULER.schedule(() -> callWebhook(studyUID), TIME_TO_LIVE, TimeUnit.SECONDS), studyUID, seriesUID, instancesUID, isNewStudy, isNewSeries, isNewInstances, source, destination, isNewInDestination);
    }


    private void callWebhook(String studyUID) {
        String log = "callWebhook:";
        log += level0StudyLevel.toString(studyUID);
        LOG.info(log);

        final Level1_SourceLevel level1SourceLevel = level0StudyLevel.get(studyUID);

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            if (level1SourceLevel.isNewStudy()) {
                //new study => call webhook only from destination
                for (Map.Entry<Source, Level2_DestinationLevel> entry : level1SourceLevel.getSources().entrySet()) {
                    final Source source = entry.getKey();
                    final Level2_DestinationLevel level2DestinationLevel = entry.getValue();

                    for (Map.Entry<String, Level3_SeriesLevel> entry1 : level2DestinationLevel.getDestinations().entrySet()) {
                        final String albumDestinationId = entry1.getKey();
                        final Level3_SeriesLevel level3SeriesLevel = entry1.getValue();

                        final Album album = getAlbum(albumDestinationId, em);
                        if  (!album.getWebhooks().isEmpty()) {
                            //if destination contain webhooks

                            final NewSeriesWebhook.Builder builder = NewSeriesWebhook.builder()
                                    .setDestination(albumDestinationId)
                                    .isUpload()
                                    .setIsManualTrigger(false)
                                    .setStudy(getStudy(studyUID, em), kheopsInstance)
                                    .setSource(source)
                                    .setKheopsInstance(kheopsInstance);

                            for (Map.Entry<String, Level4_InstancesLevel> entry2 : level3SeriesLevel.getSeries().entrySet()) {
                                final String seriesUID = entry2.getKey();
                                final Level4_InstancesLevel level4InstancesLevel = entry2.getValue();

                                final Series series = getSeries(seriesUID, em);
                                builder.addSeries(series);
                                for (String instanceUID : level4InstancesLevel.getInstancesUIDLst()) {
                                    builder.addInstances(getInstances(instanceUID, em));
                                }
                                for (String instanceUID : level2DestinationLevel.getSeriesNewInstances(seriesUID)) {
                                    builder.addInstances(getInstances(instanceUID, em));
                                }
                            }

                            for (Webhook webhook : album.getWebhooks()) {
                                if (webhook.isEnabled() && webhook.isNewSeries()) {
                                    final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                    builder.getSeriesInstancesHashMap().forEach((series, instances) -> webhookTrigger.addSeries(series));
                                    em.persist(webhookTrigger);
                                    new WebhookAsyncRequest(webhook, builder.build(), webhookTrigger).firstRequest();
                                }
                            }
                        }
                    }
                }








            } else {
                //pas une nouvelle study mais des series (nouvelle + ancienne)
                for (Source source : level1SourceLevel.getSources().keySet()) {







                    //obtenir la liste des albums qui contienent la study et des webhooks actif avec new_series
                    final List<Album> albumsLst = findAlbumLstForWebhook(studyUID, em);
                    for (String destination : level1SourceLevel.getSource(source).getDestinations().keySet()) {
                        final Album album = getAlbum(destination, em);
                        albumsLst.remove(album);


                        final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                .setDestination(destination)
                                .isUpload()
                                .setIsManualTrigger(false)
                                .setStudy(getStudy(studyUID, em), kheopsInstance)
                                .setSource(source)
                                .setKheopsInstance(kheopsInstance);

                        final ArrayList<Series> seriesLstForWebhookTrigger = new ArrayList<>();

                        for (String seriesUID : level1SourceLevel.getSource(source).getDestination(destination).getSeries().keySet()) {
                            final Series series = getSeries(seriesUID, em);
                            seriesLstForWebhookTrigger.add(series);
                            newSeriesWebhookBuilder.addSeries(series);
                            for (String instanceUID : level1SourceLevel.getSource(source).getDestination(destination).getSeries(seriesUID).getInstances().keySet()) {
                                newSeriesWebhookBuilder.addInstances(getInstances(instanceUID, em));
                            }
                            for (String instanceUID : level1SourceLevel.getSource(source).getSeriesNewInstances(seriesUID)) {
                                newSeriesWebhookBuilder.addInstances(getInstances(instanceUID, em));
                            }
                        }

                        //send in all enabled newSeries webhook in the destination

                        for (Webhook webhook : album.getWebhooks()) {
                            if (webhook.isEnabled() && webhook.isNewSeries()) {
                                final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                seriesLstForWebhookTrigger.forEach(webhookTrigger::addSeries);
                                em.persist(webhookTrigger);
                                new WebhookAsyncRequest(webhook, newSeriesWebhookBuilder.build(), webhookTrigger).firstRequest();
                            }
                        }
                    }







                    //gestion des albums non destination
                    if (!level1SourceLevel.getSource(source).getSeriesNewInstances().isEmpty()) {
                        for(Album albumDestination : albumsLst) {
                            final ArrayList<Series> seriesLstForWebhookTrigger = new ArrayList<>();
                            final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                                    .setDestination(albumDestination.getId())
                                    .isUpload()
                                    .setIsManualTrigger(false)
                                    .setStudy(getStudy(studyUID, em), kheopsInstance)
                                    .setSource(source)
                                    .setKheopsInstance(kheopsInstance);
                            for (Map.Entry<String, Set<String>> seriesUIDSetInstancesUID : level1SourceLevel.getSource(source).getSeriesNewInstances().entrySet()) {
                                final String seriesUID = seriesUIDSetInstancesUID.getKey();
                                final Set<String> instancesUIDSet = seriesUIDSetInstancesUID.getValue();
                                final Series series = getSeries(seriesUID, em);
                                if (albumDestination.containsSeries(series, em)) {
                                    seriesLstForWebhookTrigger.add(series);
                                    newSeriesWebhookBuilder.addSeries(series);
                                    for (String i : instancesUIDSet) {
                                        newSeriesWebhookBuilder.addInstances(getInstances(i, em));
                                    }
                                }
                            }
                            if (!seriesLstForWebhookTrigger.isEmpty()) {
                                for (Webhook webhook : albumDestination.getWebhooks()) {
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

        level0StudyLevel.remove(studyUID);
    }

}

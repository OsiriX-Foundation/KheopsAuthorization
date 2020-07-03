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

    private Level0 level0;


    private FooHashMap() {
        level0 = new Level0();
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
        if (level0.containsStudy(studyUID)) {
            level0.get(studyUID).cancelScheduledFuture();
        }
        level0.put(SCHEDULER.schedule(() -> callWebhook(studyUID), TIME_TO_LIVE, TimeUnit.SECONDS), studyUID, seriesUID, instancesUID, isNewStudy, isNewSeries, isNewInstances, source, destination, isNewInDestination);
    }


    private void callWebhook(String studyUID) {
        String log = "callWebhook:";
        log += level0.toString(studyUID);
        LOG.info(log);

        final Level1 level1 = level0.get(studyUID);

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            if (level1.isNewStudy()) {
                //new study => call webhook only from destination
                for (Map.Entry<Source, Level2> entry : level1.getSources().entrySet()) {
                    final Source source = entry.getKey();
                    final Level2 level2 = entry.getValue();

                    for (Map.Entry<String, Level3> entry1 : level2.getDestinations().entrySet()) {
                        final String albumDestinationId = entry1.getKey();
                        final Level3 level3 = entry1.getValue();

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

                            for (Map.Entry<String, Level4> entry2 : level3.getSeries().entrySet()) {
                                final String seriesUID = entry2.getKey();
                                final Level4 level4 = entry2.getValue();

                                final Series series = getSeries(seriesUID, em);
                                builder.addSeries(series);
                                for (String instanceUID : level4.getInstancesUIDLst()) {
                                    builder.addInstances(getInstances(instanceUID, em));
                                }
                            }

                            final List<WebhookAsyncRequest> webhookAsyncRequests = new ArrayList<>();
                            for (Webhook webhook : album.getWebhooks()) {
                                if (webhook.isEnabled() && webhook.isNewSeries()) {
                                    final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                    builder.getSeriesInstancesHashMap().forEach((series, instances) -> webhookTrigger.addSeries(series));
                                    em.persist(webhookTrigger);
                                    webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, builder.build(), webhookTrigger));
                                }
                            }
                            for (WebhookAsyncRequest webhookAsyncRequest : webhookAsyncRequests) {
                                webhookAsyncRequest.firstRequest();
                            }
                        }
                    }
                }








            } else {
                //pas une nouvelle study mais des series (nouvelle + ancienne)
                for (Source source : level1.getSources().keySet()) {


                    //debug
                    //extraire la liste des NOUVELLES instances  quelque soit la dstination et quelque soit la séries (nouvelle ou pas)
                    final HashMap<Series, Set<String>> testLstOfNewInstances = new HashMap<>();
                    for (String destination : level1.getSource(source).getDestinations().keySet()) {
                        for (String seriesUID : level1.getSource(source).getDestination(destination).getSeries().keySet()) {
                            final Series series = getSeries(seriesUID, em);
                            for (String instanceUID : level1.getSource(source).getDestination(destination).getSeries(seriesUID).getInstances().keySet()) {
                                if (level1.getSource(source).getDestination(destination).getSeries(seriesUID).isNewInstances(instanceUID)) {
                                    if (testLstOfNewInstances.containsKey(series)) {
                                        testLstOfNewInstances.get(series).add(instanceUID);
                                    } else {
                                        Set<String> lst = new HashSet<>();
                                        lst.add(instanceUID);
                                        testLstOfNewInstances.put(series, lst);
                                    }
                                }
                            }
                        }
                    }
                    //end debug




                    //obtenir la liste des albums qui contienent la study et des webhooks actif avec new_series
                    final List<Album> albumsLst = findAlbumLstForWebhook(studyUID, em);
                    HashMap<Series, Set<String>> newInstancesLstBySeries = new HashMap<>();
                    for (String destination : level1.getSource(source).getDestinations().keySet()) {
                        final Album album = getAlbum(destination, em);
                        albumsLst.remove(album);


                        final NewSeriesWebhook.Builder builder = NewSeriesWebhook.builder()
                                .setDestination(destination)
                                .isUpload()
                                .setIsManualTrigger(false)
                                .setStudy(getStudy(studyUID, em), kheopsInstance)
                                .setSource(source)
                                .setKheopsInstance(kheopsInstance);

                        final ArrayList<Series> seriesLstForWebhookTrigger = new ArrayList<>();

                        for (String seriesUID : level1.getSource(source).getDestination(destination).getSeries().keySet()) {
                            final Series series = getSeries(seriesUID, em);
                            final boolean isNewSeries = level1.getSource(source).getDestination(destination).getSeries(seriesUID).isNewSeries();
                            seriesLstForWebhookTrigger.add(series);
                            builder.addSeries(series);
                            for (String instanceUID : level1.getSource(source).getDestination(destination).getSeries(seriesUID).getInstances().keySet()) {
                                builder.addInstances(getInstances(instanceUID, em));
                                if (!isNewSeries && level1.getSource(source).getDestination(destination).getSeries(seriesUID).isNewInstances(instanceUID)) {
                                    if (newInstancesLstBySeries.containsKey(series)) {
                                        newInstancesLstBySeries.get(series).add(instanceUID);
                                    } else {
                                        Set<String> lst = new HashSet<>();
                                        lst.add(instanceUID);
                                        newInstancesLstBySeries.put(series, lst);
                                    }
                                }
                            }
                            if (testLstOfNewInstances.containsKey(series)) {
                                for (String instanceUID : testLstOfNewInstances.get(series)) {
                                    builder.addInstances(getInstances(instanceUID, em));
                                }
                            }
                        }

                        //send in all enabled newSeries webhook in the destination
                        final List<WebhookAsyncRequest> webhookAsyncRequests = new ArrayList<>();

                        for (Webhook webhook : album.getWebhooks()) {
                            if (webhook.isEnabled() && webhook.isNewSeries()) {
                                final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                seriesLstForWebhookTrigger.forEach(webhookTrigger::addSeries);
                                em.persist(webhookTrigger);
                                webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, builder.build(), webhookTrigger));
                            }
                        }
                        for (WebhookAsyncRequest webhookAsyncRequest : webhookAsyncRequests) {
                            webhookAsyncRequest.firstRequest();
                        }
                    }







                    //gestion des albums non destination
                    if (!newInstancesLstBySeries.isEmpty()) {
                        for(Album albumDestination : albumsLst) {
                            final ArrayList<Series> seriesLst = new ArrayList<>();
                            final NewSeriesWebhook.Builder builder2 = NewSeriesWebhook.builder()
                                    .setDestination(albumDestination.getId())
                                    .isUpload()
                                    .setIsManualTrigger(false)
                                    .setStudy(getStudy(studyUID, em), kheopsInstance)
                                    .setSource(source)
                                    .setKheopsInstance(kheopsInstance);
                            for (Series seriesWithNewInstances : newInstancesLstBySeries.keySet()) {
                                if (albumDestination.containsSeries(seriesWithNewInstances, em)) {
                                    seriesLst.add(seriesWithNewInstances);
                                    builder2.addSeries(seriesWithNewInstances);
                                    for (String i : newInstancesLstBySeries.get(seriesWithNewInstances)) {
                                        builder2.addInstances(getInstances(i, em));
                                    }
                                }
                            }
                            if (!seriesLst.isEmpty()) {
                                final List<WebhookAsyncRequest> webhookAsyncRequests2 = new ArrayList<>();

                                for (Webhook webhook : albumDestination.getWebhooks()) {
                                    if (webhook.isEnabled() && webhook.isNewSeries()) {
                                        final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                        seriesLst.forEach(webhookTrigger::addSeries);
                                        em.persist(webhookTrigger);
                                        webhookAsyncRequests2.add(new WebhookAsyncRequest(webhook, builder2.build(), webhookTrigger));
                                    }
                                }
                                for (WebhookAsyncRequest webhookAsyncRequest : webhookAsyncRequests2) {
                                    webhookAsyncRequest.firstRequest();
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

        level0.remove(studyUID);
    }

}

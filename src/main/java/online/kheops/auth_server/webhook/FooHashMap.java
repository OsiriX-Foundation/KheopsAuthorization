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

        Level1 level1 = level0.get(studyUID);

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            if (level1.isNewStudy()) {
                //nouvelle study on enoie uniquement aux destination en fonction de chaque source

                for (Source source:level1.getSources().keySet()) {
                    for (String destination:level1.getSources().get(source).getDestination().keySet()) {
                        NewSeriesWebhook.Builder builder = NewSeriesWebhook.builder();
                        builder.setDestination(destination)
                                .isUpload()
                                .setIsManualTrigger(false)
                                .setStudy(getStudy(studyUID, em), kheopsInstance)
                                .setSource(source)
                                .setKheopsInstance(kheopsInstance);

                        ArrayList<Series> seriesLst = new ArrayList<>();

                        for (String seriesUID:level1.getSources().get(source).getDestination().get(destination).getSeries().keySet()) {
                            final Series series = getSeries(seriesUID, em);
                            builder.addSeries(series);
                            seriesLst.add(series);
                            for (String instanceUID:level1.getSources().get(source).getDestination().get(destination).getSeries().get(seriesUID).getInstances().keySet()) {
                                // 1 studyUID => all source => all destination => all series => all instances
                                builder.addInstances(getInstances(instanceUID, em));
                            }
                        }

                        //send in all webhook in the destination
                        final List<WebhookAsyncRequest> webhookAsyncRequests = new ArrayList<>();
                        final Album album = getAlbum(destination, em);

                        for (Webhook webhook : album.getWebhooks()) {
                            if (webhook.isEnabled() && webhook.isNewSeries()) {
                                final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                seriesLst.forEach(webhookTrigger::addSeries);
                                em.persist(webhookTrigger);
                                webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, builder.build(), webhookTrigger));
                            }
                        }
                        for (WebhookAsyncRequest webhookAsyncRequest : webhookAsyncRequests) {
                            webhookAsyncRequest.firstRequest();
                        }
                    }
                }
            } else {
                //pas une nouvelle study mais des series (nouvelle + ancienne)
                for (Source source:level1.getSources().keySet()) {
                    //obtenir la liste des albums qui contienent la study et des webhooks actif avec new_series
                    List<Album> albumsLst = findAlbumLstForWebhook(studyUID, em);
                    HashMap<Series, Set<String>> newInstancesLst = new HashMap<>();
                    for (String destination:level1.getSources().get(source).getDestination().keySet()) {
                        final Album album = getAlbum(destination, em);
                        albumsLst.remove(album);

                        NewSeriesWebhook.Builder builder = NewSeriesWebhook.builder() ;
                        builder.setDestination(destination)
                                .isUpload()
                                .setIsManualTrigger(false)
                                .setStudy(getStudy(studyUID, em), kheopsInstance)
                                .setSource(source)
                                .setKheopsInstance(kheopsInstance);

                        ArrayList<Series> newSeriesLst = new ArrayList<>();
                        ArrayList<Series> oldSeriesLst = new ArrayList<>();

                        for (String seriesUID:level1.getSources().get(source).getDestination().get(destination).getSeries().keySet()) {
                            final Series series = getSeries(seriesUID, em);
                            final boolean isNewSeries = level1.getSources().get(source).getDestination().get(destination).getSeries().get(seriesUID).isNewSeries();
                            if (isNewSeries) {
                                newSeriesLst.add(series);
                                //uniquement à la destination
                            } else {
                                //si pas de nouvelle instances => uniquement à la destination
                                //sinon a tous les albums qui ont la séries
                                oldSeriesLst.add(series);
                            }
                            builder.addSeries(series);
                            for (String instanceUID:level1.getSources().get(source).getDestination().get(destination).getSeries().get(seriesUID).getInstances().keySet()) {
                                builder.addInstances(getInstances(instanceUID, em));
                                if (level1.getSources().get(source).getDestination().get(destination).getSeries().get(seriesUID).getInstances().get(instanceUID)) {
                                    if(!isNewSeries) {
                                        if (newInstancesLst.containsKey(series)) {
                                            newInstancesLst.get(series).add(instanceUID);
                                        } else {
                                            Set<String> lst = new HashSet<>();
                                            lst.add(instanceUID);
                                            newInstancesLst.put(series, lst);
                                        }
                                    }
                                }
                            }
                        }

                        //send in all webhook in the destination
                        final List<WebhookAsyncRequest> webhookAsyncRequests = new ArrayList<>();

                        for (Webhook webhook : album.getWebhooks()) {
                            if (webhook.isEnabled() && webhook.isNewSeries()) {
                                final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                newSeriesLst.forEach(webhookTrigger::addSeries);
                                oldSeriesLst.forEach(webhookTrigger::addSeries);
                                em.persist(webhookTrigger);
                                webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, builder.build(), webhookTrigger));
                            }
                        }
                        for (WebhookAsyncRequest webhookAsyncRequest : webhookAsyncRequests) {
                            webhookAsyncRequest.firstRequest();
                        }

                        //gestion des albums non destination
                        for(Album al:albumsLst) {
                            NewSeriesWebhook.Builder builder2 = NewSeriesWebhook.builder();
                            builder2.setDestination(al.getId())
                                    .isUpload()
                                    .setIsManualTrigger(false)
                                    .setStudy(getStudy(studyUID, em), kheopsInstance)
                                    .setSource(source)
                                    .setKheopsInstance(kheopsInstance);
                            for(Series s: newInstancesLst.keySet()) {
                                if (al.containsSeries(s, em)) {
                                    builder2.addSeries(s);
                                    for(String i:newInstancesLst.get(s)) {
                                        builder2.addInstances(getInstances(i, em));
                                    }
                                }
                            }
                            final List<WebhookAsyncRequest> webhookAsyncRequests2 = new ArrayList<>();

                            for (Webhook webhook : album.getWebhooks()) {
                                if (webhook.isEnabled() && webhook.isNewSeries()) {
                                    final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                                    newSeriesLst.forEach(webhookTrigger::addSeries);
                                    oldSeriesLst.forEach(webhookTrigger::addSeries);
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

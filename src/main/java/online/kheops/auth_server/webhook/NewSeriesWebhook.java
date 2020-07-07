package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.report_provider.ReportProviderResponse;
import online.kheops.auth_server.study.StudyResponse;
import online.kheops.auth_server.user.UserResponse;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NewSeriesWebhook implements WebhookResult{

    @XmlElement(name = "host")
    private String kheopsInstance;
    @XmlElement(name = "album_id")
    private String albumId;
    @XmlElement(name = "event_time")
    private LocalDateTime eventTime;
    @XmlElement(name = "source")
    private UserResponse sourceUser;
    @XmlElement(name = "is_manual_trigger")
    private boolean isManualTrigger;
    @XmlElement(name = "import_source")
    private String importSource;

    @XmlElement(name = "updated_study")
    private StudyResponse updatedStudy;


    private NewSeriesWebhook() { /*empty*/ }

    private NewSeriesWebhook(Builder builder) {
        kheopsInstance = builder.kheopsInstance;
        albumId = builder.albumId;
        eventTime = LocalDateTime.now();
        sourceUser = builder.sourceUser;
        isManualTrigger = builder.isManualTrigger;
        importSource = builder.importSource;
        updatedStudy = builder.updatedStudy;
    }

    public static Builder builder() { return new Builder(); }

    public NewSeriesWebhook(String albumId, AlbumUser sourceUser, Study study, String kheopsInstance, boolean isManualTrigger) {
        this(albumId, sourceUser, kheopsInstance, isManualTrigger);
        updatedStudy = new StudyResponse(study, kheopsInstance);
    }

    public NewSeriesWebhook(String albumId, AlbumUser sourceUser, Series series, String kheopsInstance, boolean isManualTrigger) {
        this(albumId, sourceUser, kheopsInstance, isManualTrigger);
        updatedStudy = new StudyResponse(series.getStudy(), kheopsInstance);
        updatedStudy.addSeries(series);
    }

    public NewSeriesWebhook(String albumId, AlbumUser sourceUser, String kheopsInstance, boolean isManualTrigger) {
        this.kheopsInstance = kheopsInstance;
        this.albumId = albumId;
        this.eventTime = LocalDateTime.now();
        this.sourceUser = new UserResponse(sourceUser);
        this.isManualTrigger = isManualTrigger;
        importSource = "send";
    }

    public NewSeriesWebhook(String albumId, User user, String kheopsInstance, boolean isManualTrigger) {
        this.kheopsInstance = kheopsInstance;
        this.albumId = albumId;
        this.eventTime = LocalDateTime.now();
        this.sourceUser = new UserResponse(user);
        this.isManualTrigger = isManualTrigger;
        importSource = "send";
    }

    public void addSeries(Series series) {
        if(updatedStudy == null) {
            updatedStudy = new StudyResponse(series.getStudy(), kheopsInstance);
        }
        updatedStudy.addSeries(series);
    }

    public void setReportProvider(ReportProvider reportProvider) { sourceUser.setReportProvider(reportProvider, ReportProviderResponse.Type.WEBHOOK); }

    public void setCapabilityToken(Capability capability) { sourceUser.setCapabilityToken(capability); }

    public boolean containSeries() {
        return updatedStudy.containSeries();
    }

    public void setFetch() {
        importSource = "upload";
    }



    @Override
    public WebhookType getType() {
        return WebhookType.NEW_SERIES;
    }


    public static class Builder {
        private String kheopsInstance;
        private String albumId;
        private UserResponse sourceUser;
        private boolean isManualTrigger;
        private String importSource;
        private StudyResponse updatedStudy;
        private HashMap<Series,Set<Instances>> seriesInstancesHashMap = new HashMap<>();

        public Builder() {
        }

        public Builder setDestination(String destination) {
            if (destination == null) {
                throw new IllegalStateException("destiation is null");
            }
            albumId = destination;
            return this;
        }

        public Builder setKheopsInstance(String kheopsInstance) {
            if (kheopsInstance == null) {
                throw new IllegalStateException("instance is null");
            }
            this.kheopsInstance = kheopsInstance;
            return this;
        }

        public Builder setSource(Source source) {
            if (source == null) {
                throw new IllegalStateException("source is null");
            }
            sourceUser = new UserResponse(source.getUser().get());
            source.getCapabilityTokenId().ifPresent(capability -> sourceUser.setCapabilityToken(capability));
            source.getReportProvider().ifPresent(reportProvider -> sourceUser.setReportProvider(reportProvider, ReportProviderResponse.Type.WEBHOOK));
            return this;
        }

        public Builder setIsManualTrigger(boolean isManualTrigger) {
            this.isManualTrigger = isManualTrigger;
            return this;
        }

        public Builder isUpload() {
            importSource = "upload";
            return this;
        }

        public Builder isSent() {
            importSource = "send";
            return this;
        }

        public Builder setStudy(Study study, String kheopsInstance) {
            if (updatedStudy != null) {
                throw new IllegalStateException("updatedStudy is already set");
            }
            this.updatedStudy = new StudyResponse(study, kheopsInstance);
            return this;
        }

        public Builder addSeries(Series series) {
            if (!seriesInstancesHashMap.containsKey(series)) {
                seriesInstancesHashMap.put(series, new HashSet<>());
            }
            //updatedStudy.addSeries(series);
            return this;
        }

        public Builder addInstances(Instances instances) {
            if (!seriesInstancesHashMap.containsKey(instances.getSeries())) {
                seriesInstancesHashMap.put(instances.getSeries(), new HashSet<>());
            }
            seriesInstancesHashMap.get(instances.getSeries()).add(instances);

            //updatedStudy.addInstances(instances);
            return this;
        }

        public Map<Series, Set<Instances>> getSeriesInstancesHashMap() { return seriesInstancesHashMap; }

        public NewSeriesWebhook build() {
            for(Series s:seriesInstancesHashMap.keySet()) {
                updatedStudy.addSeriesWithInstances(s, seriesInstancesHashMap.get(s));
            }
            return new NewSeriesWebhook(this);
        }

    }
}

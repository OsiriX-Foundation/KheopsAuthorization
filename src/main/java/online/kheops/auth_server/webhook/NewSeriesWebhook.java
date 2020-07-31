package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.study.StudyResponse;
import online.kheops.auth_server.user.UserResponse;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static online.kheops.auth_server.report_provider.ReportProviderResponse.Type.WEBHOOK;

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
        this.kheopsInstance = builder.kheopInstance;
        albumId = builder.albumId;
        eventTime = LocalDateTime.now();
        sourceUser = builder.sourceUser;
        isManualTrigger = builder.isManualTrigger;
        importSource = builder.importSource;
        updatedStudy = builder.updatedStudy;
    }

    public static Builder builder() { return new Builder(); }

    @Override
    public WebhookType getType() {
        return WebhookType.NEW_SERIES;
    }

    public static class Builder {
        private String albumId;
        private UserResponse sourceUser;
        private boolean isManualTrigger;
        private String importSource;
        private StudyResponse updatedStudy;
        private String kheopInstance;
        private Map<Series,Set<Instances>> seriesInstancesHashMap = new HashMap<>();

        public Builder() { /*empty*/ }

        public Builder setDestination(String destination) {
            if (destination == null) {
                throw new IllegalStateException("destiation is null");
            }
            albumId = destination;
            return this;
        }

        public Builder setSource(Source source) {
            if (source == null) {
                throw new IllegalStateException("source is null");
            }
            sourceUser = new UserResponse(source.getUser());
            source.getCapabilityToken().ifPresent(capability -> sourceUser.setCapabilityToken(capability));
            source.getReportProvider().ifPresent(reportProvider -> sourceUser.setReportProvider(reportProvider, WEBHOOK));
            return this;
        }

        public Builder setSource(AlbumUser sourceUser) {
            this.sourceUser = new UserResponse(sourceUser);
            return this;
        }

        public Builder setCapabilityToken(Capability capability) {
            if (sourceUser != null) {
                sourceUser.setCapabilityToken(capability);
            } else {
                throw new IllegalStateException();
            }
            return this;
        }

        public Builder setReportProvider(ReportProvider reportProvider) {
            if (sourceUser != null) {
                sourceUser.setReportProvider(reportProvider, WEBHOOK);
            } else {
                throw new IllegalStateException();
            }
            return this;
        }

        public Builder isManualTrigger() {
            this.isManualTrigger = true;
            return this;
        }

        public Builder isAutomatedTrigger() {
            this.isManualTrigger = false;
            return this;
        }

        public Builder setKheopsInstance(String kheopsInstance) {
            this.kheopInstance = kheopsInstance;
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

        public Builder setStudy(Study study) {
            if (updatedStudy != null) {
                throw new IllegalStateException("updatedStudy is already set");
            }
            this.updatedStudy = new StudyResponse(study, false, kheopInstance);
            return this;
        }

        public Builder addSeries(Series series) {
            seriesInstancesHashMap.computeIfAbsent(series, series1 -> new HashSet<>());
            return this;
        }

        public Builder addInstances(Instances instances) {
            seriesInstancesHashMap.computeIfAbsent(instances.getSeries(), series1 -> new HashSet<>()).add(instances);
            return this;
        }

        public Map<Series, Set<Instances>> getSeriesInstancesHashMap() { return seriesInstancesHashMap; }

        public boolean containSeries() { return !seriesInstancesHashMap.isEmpty(); }

        public NewSeriesWebhook build() {
            seriesInstancesHashMap.forEach(updatedStudy::addSeriesWithInstances);
            return new NewSeriesWebhook(this);
        }

    }
}
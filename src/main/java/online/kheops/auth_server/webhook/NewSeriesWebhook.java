package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.report_provider.ReportProviderResponse;
import online.kheops.auth_server.series.SeriesResponse;
import online.kheops.auth_server.study.StudyResponse;
import online.kheops.auth_server.user.UserResponse;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NewSeriesWebhook implements WebhookResult{

    @XmlElement(name = "host")
    private String instance;
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
    @XmlElement(name = "instances")
    private Set<String> instances;

    @XmlElement(name = "updated_study")
    private StudyResponse updatedStudy;


    private NewSeriesWebhook() { /*empty*/ }

    public NewSeriesWebhook(String albumId, AlbumUser sourceUser, Study study, String instance, boolean isManualTrigger) {
        this(albumId, sourceUser, instance, isManualTrigger);
        updatedStudy = new StudyResponse(study, instance);
    }

    public NewSeriesWebhook(String albumId, AlbumUser sourceUser, Series series, String instance, boolean isManualTrigger) {
        this(albumId, sourceUser, instance, isManualTrigger);
        updatedStudy = new StudyResponse(series.getStudy(), instance);
        updatedStudy.addSeries(series);
    }

    public NewSeriesWebhook(String albumId, AlbumUser sourceUser, String instance, boolean isManualTrigger) {
        this.instance = instance;
        this.albumId = albumId;
        this.eventTime = LocalDateTime.now();
        this.sourceUser = new UserResponse(sourceUser);
        this.isManualTrigger = isManualTrigger;
        importSource = "send";
    }

    public NewSeriesWebhook(String albumId, User user, String instance, boolean isManualTrigger) {
        this.instance = instance;
        this.albumId = albumId;
        this.eventTime = LocalDateTime.now();
        this.sourceUser = new UserResponse(user);
        this.isManualTrigger = isManualTrigger;
        importSource = "send";
    }

    public void addSeries(Series series) {
        if(updatedStudy == null) {
            updatedStudy = new StudyResponse(series.getStudy(), instance);
        }
        updatedStudy.addSeries(series);
    }

    public void addInstances(String instance) {
        if(instances == null) {
            instances = new HashSet<>();
        }
        instances.add(instance);
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


    public class Builder {
        private String instance;
        private String albumId;
        private LocalDateTime eventTime;
        private UserResponse sourceUser;
        private boolean isManualTrigger;
        private String importSource;
        private Set<String> instances;
        private Set<SeriesResponse> series;
        private StudyResponse updatedStudy;



        public Builder() {
        }
    }
}

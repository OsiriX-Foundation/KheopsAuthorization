package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.Webhook;
import online.kheops.auth_server.entity.WebhookTrigger;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class WebhookResponse {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "url")
    private String url;
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "use_secret")
    private boolean useSecret;
    @XmlElement(name = "events")
    private List<String> events;
    @XmlElement(name = "enabled")
    private boolean enabled;
    @XmlElement(name = "last_triggers")
    private List<WebhookLastTriggerResponse> lastTriggers;
    @XmlElement(name = "number_of_triggers")
    private Integer numberOfTriggers;
    @XmlElement(name = "triggers")
    private List<WebhookTriggerResponse> fullTriggers;

    private WebhookResponse() { /*Empty*/ }

    public WebhookResponse(Webhook webhook) {

        id = webhook.getId();
        name = webhook.getName();
        url = webhook.getUrl();
        useSecret = webhook.useSecret();
        events = new ArrayList<>();
        if(webhook.isNewSeries()) {
            events.add(WebhookType.NEW_SERIES.name().toLowerCase());
        }
        if(webhook.isNewUser()) {
            events.add(WebhookType.NEW_USER.name().toLowerCase());
        }

        enabled = webhook.isEnabled();
        numberOfTriggers = webhook.getWebhookTriggers().size();
    }

    public void addTrigger(WebhookTrigger webhookTrigger) {
        if (lastTriggers == null) {
            lastTriggers = new ArrayList<>();
        }
        this.lastTriggers.add(new WebhookLastTriggerResponse(webhookTrigger));

    }

    public void addFullTriggers(WebhookTrigger webhookTrigger) {
        if (fullTriggers == null) {
            fullTriggers = new ArrayList<>();
        }
        this.fullTriggers.add(new WebhookTriggerResponse(webhookTrigger));
    }
}

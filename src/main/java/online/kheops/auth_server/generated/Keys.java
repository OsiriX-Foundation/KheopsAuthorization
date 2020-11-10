/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated;


import online.kheops.auth_server.generated.tables.AlbumSeries;
import online.kheops.auth_server.generated.tables.AlbumUser;
import online.kheops.auth_server.generated.tables.Albums;
import online.kheops.auth_server.generated.tables.Capabilities;
import online.kheops.auth_server.generated.tables.EventSeries;
import online.kheops.auth_server.generated.tables.Events;
import online.kheops.auth_server.generated.tables.Instances;
import online.kheops.auth_server.generated.tables.ReportProviders;
import online.kheops.auth_server.generated.tables.Series;
import online.kheops.auth_server.generated.tables.Studies;
import online.kheops.auth_server.generated.tables.Users;
import online.kheops.auth_server.generated.tables.WebhookAttempts;
import online.kheops.auth_server.generated.tables.WebhookTriggerSeries;
import online.kheops.auth_server.generated.tables.WebhookTriggers;
import online.kheops.auth_server.generated.tables.Webhooks;
import online.kheops.auth_server.generated.tables.records.AlbumSeriesRecord;
import online.kheops.auth_server.generated.tables.records.AlbumUserRecord;
import online.kheops.auth_server.generated.tables.records.AlbumsRecord;
import online.kheops.auth_server.generated.tables.records.CapabilitiesRecord;
import online.kheops.auth_server.generated.tables.records.EventSeriesRecord;
import online.kheops.auth_server.generated.tables.records.EventsRecord;
import online.kheops.auth_server.generated.tables.records.InstancesRecord;
import online.kheops.auth_server.generated.tables.records.ReportProvidersRecord;
import online.kheops.auth_server.generated.tables.records.SeriesRecord;
import online.kheops.auth_server.generated.tables.records.StudiesRecord;
import online.kheops.auth_server.generated.tables.records.UsersRecord;
import online.kheops.auth_server.generated.tables.records.WebhookAttemptsRecord;
import online.kheops.auth_server.generated.tables.records.WebhookTriggerSeriesRecord;
import online.kheops.auth_server.generated.tables.records.WebhookTriggersRecord;
import online.kheops.auth_server.generated.tables.records.WebhooksRecord;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>public</code> schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<AlbumSeriesRecord, Long> IDENTITY_ALBUM_SERIES = Identities0.IDENTITY_ALBUM_SERIES;
    public static final Identity<AlbumUserRecord, Long> IDENTITY_ALBUM_USER = Identities0.IDENTITY_ALBUM_USER;
    public static final Identity<AlbumsRecord, Long> IDENTITY_ALBUMS = Identities0.IDENTITY_ALBUMS;
    public static final Identity<CapabilitiesRecord, Long> IDENTITY_CAPABILITIES = Identities0.IDENTITY_CAPABILITIES;
    public static final Identity<EventSeriesRecord, Long> IDENTITY_EVENT_SERIES = Identities0.IDENTITY_EVENT_SERIES;
    public static final Identity<EventsRecord, Long> IDENTITY_EVENTS = Identities0.IDENTITY_EVENTS;
    public static final Identity<InstancesRecord, Long> IDENTITY_INSTANCES = Identities0.IDENTITY_INSTANCES;
    public static final Identity<ReportProvidersRecord, Long> IDENTITY_REPORT_PROVIDERS = Identities0.IDENTITY_REPORT_PROVIDERS;
    public static final Identity<SeriesRecord, Long> IDENTITY_SERIES = Identities0.IDENTITY_SERIES;
    public static final Identity<StudiesRecord, Long> IDENTITY_STUDIES = Identities0.IDENTITY_STUDIES;
    public static final Identity<UsersRecord, Long> IDENTITY_USERS = Identities0.IDENTITY_USERS;
    public static final Identity<WebhookAttemptsRecord, Long> IDENTITY_WEBHOOK_ATTEMPTS = Identities0.IDENTITY_WEBHOOK_ATTEMPTS;
    public static final Identity<WebhookTriggerSeriesRecord, Long> IDENTITY_WEBHOOK_TRIGGER_SERIES = Identities0.IDENTITY_WEBHOOK_TRIGGER_SERIES;
    public static final Identity<WebhookTriggersRecord, Long> IDENTITY_WEBHOOK_TRIGGERS = Identities0.IDENTITY_WEBHOOK_TRIGGERS;
    public static final Identity<WebhooksRecord, Long> IDENTITY_WEBHOOKS = Identities0.IDENTITY_WEBHOOKS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AlbumSeriesRecord> ALBUM_SERIES_PK = UniqueKeys0.ALBUM_SERIES_PK;
    public static final UniqueKey<AlbumSeriesRecord> ALBUM_SERIES_UNIQUE = UniqueKeys0.ALBUM_SERIES_UNIQUE;
    public static final UniqueKey<AlbumUserRecord> ALBUM_USER_PK = UniqueKeys0.ALBUM_USER_PK;
    public static final UniqueKey<AlbumUserRecord> ALBUM_USER_UNIQUE = UniqueKeys0.ALBUM_USER_UNIQUE;
    public static final UniqueKey<AlbumsRecord> ALBUM_PK = UniqueKeys0.ALBUM_PK;
    public static final UniqueKey<AlbumsRecord> ALBUMS_ID_UNIQUE = UniqueKeys0.ALBUMS_ID_UNIQUE;
    public static final UniqueKey<CapabilitiesRecord> CAPABILITIES_PK = UniqueKeys0.CAPABILITIES_PK;
    public static final UniqueKey<CapabilitiesRecord> CAPABILITIES_ID_UNIQUE = UniqueKeys0.CAPABILITIES_ID_UNIQUE;
    public static final UniqueKey<CapabilitiesRecord> CAPABILITIES_SECRET_UNIQUE = UniqueKeys0.CAPABILITIES_SECRET_UNIQUE;
    public static final UniqueKey<EventSeriesRecord> EVENT_SERIES_PK = UniqueKeys0.EVENT_SERIES_PK;
    public static final UniqueKey<EventSeriesRecord> EVENT_SERIES_UNIQUE = UniqueKeys0.EVENT_SERIES_UNIQUE;
    public static final UniqueKey<EventsRecord> EVENT_PK = UniqueKeys0.EVENT_PK;
    public static final UniqueKey<InstancesRecord> INSTANCES_PK = UniqueKeys0.INSTANCES_PK;
    public static final UniqueKey<InstancesRecord> INSTANCES_PK_UNIQUE = UniqueKeys0.INSTANCES_PK_UNIQUE;
    public static final UniqueKey<InstancesRecord> INSTANCES_UID_UNIQUE = UniqueKeys0.INSTANCES_UID_UNIQUE;
    public static final UniqueKey<ReportProvidersRecord> REPORT_PROVIDERS_PK = UniqueKeys0.REPORT_PROVIDERS_PK;
    public static final UniqueKey<ReportProvidersRecord> REPORT_PROVIDERS_CLIENT_ID_UNIQUE = UniqueKeys0.REPORT_PROVIDERS_CLIENT_ID_UNIQUE;
    public static final UniqueKey<SeriesRecord> SERIES_PK = UniqueKeys0.SERIES_PK;
    public static final UniqueKey<SeriesRecord> SERIES_UID_UNIQUE = UniqueKeys0.SERIES_UID_UNIQUE;
    public static final UniqueKey<StudiesRecord> STUDIES_PK = UniqueKeys0.STUDIES_PK;
    public static final UniqueKey<StudiesRecord> STUDY_UID_UNIQUE = UniqueKeys0.STUDY_UID_UNIQUE;
    public static final UniqueKey<UsersRecord> USERS_PK = UniqueKeys0.USERS_PK;
    public static final UniqueKey<UsersRecord> SUB_UNIQUE = UniqueKeys0.SUB_UNIQUE;
    public static final UniqueKey<UsersRecord> INBOX_FK_UNIQUE = UniqueKeys0.INBOX_FK_UNIQUE;
    public static final UniqueKey<UsersRecord> USERS_EMAIL_KEY = UniqueKeys0.USERS_EMAIL_KEY;
    public static final UniqueKey<WebhookAttemptsRecord> WEBHOOK_ATTEMPT_PK = UniqueKeys0.WEBHOOK_ATTEMPT_PK;
    public static final UniqueKey<WebhookTriggerSeriesRecord> WEBHOOK_TRIGGER_SERIES_PK = UniqueKeys0.WEBHOOK_TRIGGER_SERIES_PK;
    public static final UniqueKey<WebhookTriggerSeriesRecord> WEBHOOK_TRIGGER_SERIES_UNIQUE = UniqueKeys0.WEBHOOK_TRIGGER_SERIES_UNIQUE;
    public static final UniqueKey<WebhookTriggersRecord> WEBHOOK_TRIGGERS_PK = UniqueKeys0.WEBHOOK_TRIGGERS_PK;
    public static final UniqueKey<WebhookTriggersRecord> WEBHOOK_TRIGGERS_ID_UNIQUE = UniqueKeys0.WEBHOOK_TRIGGERS_ID_UNIQUE;
    public static final UniqueKey<WebhooksRecord> WEBHOOKS_PK = UniqueKeys0.WEBHOOKS_PK;
    public static final UniqueKey<WebhooksRecord> WEBHOOKS_ID_UNIQUE = UniqueKeys0.WEBHOOKS_ID_UNIQUE;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<AlbumSeriesRecord, AlbumsRecord> ALBUM_SERIES__ALBUM_SERIES_ALBUM_FK_FKEY = ForeignKeys0.ALBUM_SERIES__ALBUM_SERIES_ALBUM_FK_FKEY;
    public static final ForeignKey<AlbumSeriesRecord, SeriesRecord> ALBUM_SERIES__ALBUM_SERIES_SERIES_FK_FKEY = ForeignKeys0.ALBUM_SERIES__ALBUM_SERIES_SERIES_FK_FKEY;
    public static final ForeignKey<AlbumUserRecord, AlbumsRecord> ALBUM_USER__ALBUM_USER_ALBUM_FK_FKEY = ForeignKeys0.ALBUM_USER__ALBUM_USER_ALBUM_FK_FKEY;
    public static final ForeignKey<AlbumUserRecord, UsersRecord> ALBUM_USER__ALBUM_USER_USER_FK_FKEY = ForeignKeys0.ALBUM_USER__ALBUM_USER_USER_FK_FKEY;
    public static final ForeignKey<CapabilitiesRecord, UsersRecord> CAPABILITIES__CAPABILITIES_USER_FK_FKEY = ForeignKeys0.CAPABILITIES__CAPABILITIES_USER_FK_FKEY;
    public static final ForeignKey<CapabilitiesRecord, AlbumsRecord> CAPABILITIES__CAPABILITIES_ALBUM_FK_FKEY = ForeignKeys0.CAPABILITIES__CAPABILITIES_ALBUM_FK_FKEY;
    public static final ForeignKey<EventSeriesRecord, EventsRecord> EVENT_SERIES__EVENT_SERIES_EVENT_FK_FKEY = ForeignKeys0.EVENT_SERIES__EVENT_SERIES_EVENT_FK_FKEY;
    public static final ForeignKey<EventSeriesRecord, SeriesRecord> EVENT_SERIES__EVENT_SERIES_SERIES_FK_FKEY = ForeignKeys0.EVENT_SERIES__EVENT_SERIES_SERIES_FK_FKEY;
    public static final ForeignKey<EventsRecord, AlbumsRecord> EVENTS__EVENT_ALBUM_FK_FKEY = ForeignKeys0.EVENTS__EVENT_ALBUM_FK_FKEY;
    public static final ForeignKey<EventsRecord, StudiesRecord> EVENTS__EVENT_STUDY_FK_FKEY = ForeignKeys0.EVENTS__EVENT_STUDY_FK_FKEY;
    public static final ForeignKey<EventsRecord, UsersRecord> EVENTS__EVENT_USER_FK_FKEY = ForeignKeys0.EVENTS__EVENT_USER_FK_FKEY;
    public static final ForeignKey<EventsRecord, CapabilitiesRecord> EVENTS__EVENT_CAPABILITY_FK_FKEY = ForeignKeys0.EVENTS__EVENT_CAPABILITY_FK_FKEY;
    public static final ForeignKey<EventsRecord, UsersRecord> EVENTS__EVENT_PRIVATE_TARGET_USER_FK_FKEY = ForeignKeys0.EVENTS__EVENT_PRIVATE_TARGET_USER_FK_FKEY;
    public static final ForeignKey<EventsRecord, UsersRecord> EVENTS__EVENT_TO_USER_FK_FKEY = ForeignKeys0.EVENTS__EVENT_TO_USER_FK_FKEY;
    public static final ForeignKey<EventsRecord, ReportProvidersRecord> EVENTS__EVENT_REPORT_PROVIDER_FK_FKEY = ForeignKeys0.EVENTS__EVENT_REPORT_PROVIDER_FK_FKEY;
    public static final ForeignKey<InstancesRecord, SeriesRecord> INSTANCES__INSTANCES_SERIES_FK_FKEY = ForeignKeys0.INSTANCES__INSTANCES_SERIES_FK_FKEY;
    public static final ForeignKey<ReportProvidersRecord, AlbumsRecord> REPORT_PROVIDERS__REPORT_PROVIDERS_ALBUM_FK_FKEY = ForeignKeys0.REPORT_PROVIDERS__REPORT_PROVIDERS_ALBUM_FK_FKEY;
    public static final ForeignKey<SeriesRecord, StudiesRecord> SERIES__SERIES_STUDY_FK_FKEY = ForeignKeys0.SERIES__SERIES_STUDY_FK_FKEY;
    public static final ForeignKey<UsersRecord, AlbumsRecord> USERS__USERS_INBOX_FK_FKEY = ForeignKeys0.USERS__USERS_INBOX_FK_FKEY;
    public static final ForeignKey<WebhookAttemptsRecord, WebhookTriggersRecord> WEBHOOK_ATTEMPTS__WEBHOOK_ATTEMPTS_WEBHOOK_TRIGGERS_FK_FKEY = ForeignKeys0.WEBHOOK_ATTEMPTS__WEBHOOK_ATTEMPTS_WEBHOOK_TRIGGERS_FK_FKEY;
    public static final ForeignKey<WebhookTriggerSeriesRecord, WebhookTriggersRecord> WEBHOOK_TRIGGER_SERIES__WEBHOOK_TRIGGER_SERIES_WEBHOOK_TRIGGER_FK_FKEY = ForeignKeys0.WEBHOOK_TRIGGER_SERIES__WEBHOOK_TRIGGER_SERIES_WEBHOOK_TRIGGER_FK_FKEY;
    public static final ForeignKey<WebhookTriggerSeriesRecord, SeriesRecord> WEBHOOK_TRIGGER_SERIES__WEBHOOK_TRIGGER_SERIES_SERIES_FK_FKEY = ForeignKeys0.WEBHOOK_TRIGGER_SERIES__WEBHOOK_TRIGGER_SERIES_SERIES_FK_FKEY;
    public static final ForeignKey<WebhookTriggersRecord, WebhooksRecord> WEBHOOK_TRIGGERS__WEBHOOK_TRIGGERS_WEBHOOK_FK_FKEY = ForeignKeys0.WEBHOOK_TRIGGERS__WEBHOOK_TRIGGERS_WEBHOOK_FK_FKEY;
    public static final ForeignKey<WebhookTriggersRecord, UsersRecord> WEBHOOK_TRIGGERS__WEBHOOK_TRIGGERS_USER_FK_FKEY = ForeignKeys0.WEBHOOK_TRIGGERS__WEBHOOK_TRIGGERS_USER_FK_FKEY;
    public static final ForeignKey<WebhooksRecord, UsersRecord> WEBHOOKS__WEBHOOK_USER_FK_FKEY = ForeignKeys0.WEBHOOKS__WEBHOOK_USER_FK_FKEY;
    public static final ForeignKey<WebhooksRecord, AlbumsRecord> WEBHOOKS__WEBHOOK_ALBUM_FK_FKEY = ForeignKeys0.WEBHOOKS__WEBHOOK_ALBUM_FK_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<AlbumSeriesRecord, Long> IDENTITY_ALBUM_SERIES = Internal.createIdentity(AlbumSeries.ALBUM_SERIES, AlbumSeries.ALBUM_SERIES.PK);
        public static Identity<AlbumUserRecord, Long> IDENTITY_ALBUM_USER = Internal.createIdentity(AlbumUser.ALBUM_USER, AlbumUser.ALBUM_USER.PK);
        public static Identity<AlbumsRecord, Long> IDENTITY_ALBUMS = Internal.createIdentity(Albums.ALBUMS, Albums.ALBUMS.PK);
        public static Identity<CapabilitiesRecord, Long> IDENTITY_CAPABILITIES = Internal.createIdentity(Capabilities.CAPABILITIES, Capabilities.CAPABILITIES.PK);
        public static Identity<EventSeriesRecord, Long> IDENTITY_EVENT_SERIES = Internal.createIdentity(EventSeries.EVENT_SERIES, EventSeries.EVENT_SERIES.PK);
        public static Identity<EventsRecord, Long> IDENTITY_EVENTS = Internal.createIdentity(Events.EVENTS, Events.EVENTS.PK);
        public static Identity<InstancesRecord, Long> IDENTITY_INSTANCES = Internal.createIdentity(Instances.INSTANCES, Instances.INSTANCES.PK);
        public static Identity<ReportProvidersRecord, Long> IDENTITY_REPORT_PROVIDERS = Internal.createIdentity(ReportProviders.REPORT_PROVIDERS, ReportProviders.REPORT_PROVIDERS.PK);
        public static Identity<SeriesRecord, Long> IDENTITY_SERIES = Internal.createIdentity(Series.SERIES, Series.SERIES.PK);
        public static Identity<StudiesRecord, Long> IDENTITY_STUDIES = Internal.createIdentity(Studies.STUDIES, Studies.STUDIES.PK);
        public static Identity<UsersRecord, Long> IDENTITY_USERS = Internal.createIdentity(Users.USERS, Users.USERS.PK);
        public static Identity<WebhookAttemptsRecord, Long> IDENTITY_WEBHOOK_ATTEMPTS = Internal.createIdentity(WebhookAttempts.WEBHOOK_ATTEMPTS, WebhookAttempts.WEBHOOK_ATTEMPTS.PK);
        public static Identity<WebhookTriggerSeriesRecord, Long> IDENTITY_WEBHOOK_TRIGGER_SERIES = Internal.createIdentity(WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES, WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES.PK);
        public static Identity<WebhookTriggersRecord, Long> IDENTITY_WEBHOOK_TRIGGERS = Internal.createIdentity(WebhookTriggers.WEBHOOK_TRIGGERS, WebhookTriggers.WEBHOOK_TRIGGERS.PK);
        public static Identity<WebhooksRecord, Long> IDENTITY_WEBHOOKS = Internal.createIdentity(Webhooks.WEBHOOKS, Webhooks.WEBHOOKS.PK);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<AlbumSeriesRecord> ALBUM_SERIES_PK = Internal.createUniqueKey(AlbumSeries.ALBUM_SERIES, "album_series_pk", new TableField[] { AlbumSeries.ALBUM_SERIES.PK }, true);
        public static final UniqueKey<AlbumSeriesRecord> ALBUM_SERIES_UNIQUE = Internal.createUniqueKey(AlbumSeries.ALBUM_SERIES, "album_series_unique", new TableField[] { AlbumSeries.ALBUM_SERIES.ALBUM_FK, AlbumSeries.ALBUM_SERIES.SERIES_FK }, true);
        public static final UniqueKey<AlbumUserRecord> ALBUM_USER_PK = Internal.createUniqueKey(AlbumUser.ALBUM_USER, "album_user_pk", new TableField[] { AlbumUser.ALBUM_USER.PK }, true);
        public static final UniqueKey<AlbumUserRecord> ALBUM_USER_UNIQUE = Internal.createUniqueKey(AlbumUser.ALBUM_USER, "album_user_unique", new TableField[] { AlbumUser.ALBUM_USER.ALBUM_FK, AlbumUser.ALBUM_USER.USER_FK }, true);
        public static final UniqueKey<AlbumsRecord> ALBUM_PK = Internal.createUniqueKey(Albums.ALBUMS, "album_pk", new TableField[] { Albums.ALBUMS.PK }, true);
        public static final UniqueKey<AlbumsRecord> ALBUMS_ID_UNIQUE = Internal.createUniqueKey(Albums.ALBUMS, "albums_id_unique", new TableField[] { Albums.ALBUMS.ID }, true);
        public static final UniqueKey<CapabilitiesRecord> CAPABILITIES_PK = Internal.createUniqueKey(Capabilities.CAPABILITIES, "capabilities_pk", new TableField[] { Capabilities.CAPABILITIES.PK }, true);
        public static final UniqueKey<CapabilitiesRecord> CAPABILITIES_ID_UNIQUE = Internal.createUniqueKey(Capabilities.CAPABILITIES, "capabilities_id_unique", new TableField[] { Capabilities.CAPABILITIES.ID }, true);
        public static final UniqueKey<CapabilitiesRecord> CAPABILITIES_SECRET_UNIQUE = Internal.createUniqueKey(Capabilities.CAPABILITIES, "capabilities_secret_unique", new TableField[] { Capabilities.CAPABILITIES.SECRET }, true);
        public static final UniqueKey<EventSeriesRecord> EVENT_SERIES_PK = Internal.createUniqueKey(EventSeries.EVENT_SERIES, "event_series_pk", new TableField[] { EventSeries.EVENT_SERIES.PK }, true);
        public static final UniqueKey<EventSeriesRecord> EVENT_SERIES_UNIQUE = Internal.createUniqueKey(EventSeries.EVENT_SERIES, "event_series_unique", new TableField[] { EventSeries.EVENT_SERIES.EVENT_FK, EventSeries.EVENT_SERIES.SERIES_FK }, true);
        public static final UniqueKey<EventsRecord> EVENT_PK = Internal.createUniqueKey(Events.EVENTS, "event_pk", new TableField[] { Events.EVENTS.PK }, true);
        public static final UniqueKey<InstancesRecord> INSTANCES_PK = Internal.createUniqueKey(Instances.INSTANCES, "instances_pk", new TableField[] { Instances.INSTANCES.PK }, true);
        public static final UniqueKey<InstancesRecord> INSTANCES_PK_UNIQUE = Internal.createUniqueKey(Instances.INSTANCES, "instances_pk_unique", new TableField[] { Instances.INSTANCES.PK }, true);
        public static final UniqueKey<InstancesRecord> INSTANCES_UID_UNIQUE = Internal.createUniqueKey(Instances.INSTANCES, "instances_uid_unique", new TableField[] { Instances.INSTANCES.INSTANCE_UID }, true);
        public static final UniqueKey<ReportProvidersRecord> REPORT_PROVIDERS_PK = Internal.createUniqueKey(ReportProviders.REPORT_PROVIDERS, "report_providers_pk", new TableField[] { ReportProviders.REPORT_PROVIDERS.PK }, true);
        public static final UniqueKey<ReportProvidersRecord> REPORT_PROVIDERS_CLIENT_ID_UNIQUE = Internal.createUniqueKey(ReportProviders.REPORT_PROVIDERS, "report_providers_client_id_unique", new TableField[] { ReportProviders.REPORT_PROVIDERS.CLIENT_ID }, true);
        public static final UniqueKey<SeriesRecord> SERIES_PK = Internal.createUniqueKey(Series.SERIES, "series_pk", new TableField[] { Series.SERIES.PK }, true);
        public static final UniqueKey<SeriesRecord> SERIES_UID_UNIQUE = Internal.createUniqueKey(Series.SERIES, "series_uid_unique", new TableField[] { Series.SERIES.SERIES_UID }, true);
        public static final UniqueKey<StudiesRecord> STUDIES_PK = Internal.createUniqueKey(Studies.STUDIES, "studies_pk", new TableField[] { Studies.STUDIES.PK }, true);
        public static final UniqueKey<StudiesRecord> STUDY_UID_UNIQUE = Internal.createUniqueKey(Studies.STUDIES, "study_uid_unique", new TableField[] { Studies.STUDIES.STUDY_UID }, true);
        public static final UniqueKey<UsersRecord> USERS_PK = Internal.createUniqueKey(Users.USERS, "users_pk", new TableField[] { Users.USERS.PK }, true);
        public static final UniqueKey<UsersRecord> SUB_UNIQUE = Internal.createUniqueKey(Users.USERS, "sub_unique", new TableField[] { Users.USERS.SUB }, true);
        public static final UniqueKey<UsersRecord> INBOX_FK_UNIQUE = Internal.createUniqueKey(Users.USERS, "inbox_fk_unique", new TableField[] { Users.USERS.INBOX_FK }, true);
        public static final UniqueKey<UsersRecord> USERS_EMAIL_KEY = Internal.createUniqueKey(Users.USERS, "users_email_key", new TableField[] { Users.USERS.EMAIL }, true);
        public static final UniqueKey<WebhookAttemptsRecord> WEBHOOK_ATTEMPT_PK = Internal.createUniqueKey(WebhookAttempts.WEBHOOK_ATTEMPTS, "webhook_attempt_pk", new TableField[] { WebhookAttempts.WEBHOOK_ATTEMPTS.PK }, true);
        public static final UniqueKey<WebhookTriggerSeriesRecord> WEBHOOK_TRIGGER_SERIES_PK = Internal.createUniqueKey(WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES, "webhook_trigger_series_pk", new TableField[] { WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES.PK }, true);
        public static final UniqueKey<WebhookTriggerSeriesRecord> WEBHOOK_TRIGGER_SERIES_UNIQUE = Internal.createUniqueKey(WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES, "webhook_trigger_series_unique", new TableField[] { WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES.WEBHOOK_TRIGGER_FK, WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES.SERIES_FK }, true);
        public static final UniqueKey<WebhookTriggersRecord> WEBHOOK_TRIGGERS_PK = Internal.createUniqueKey(WebhookTriggers.WEBHOOK_TRIGGERS, "webhook_triggers_pk", new TableField[] { WebhookTriggers.WEBHOOK_TRIGGERS.PK }, true);
        public static final UniqueKey<WebhookTriggersRecord> WEBHOOK_TRIGGERS_ID_UNIQUE = Internal.createUniqueKey(WebhookTriggers.WEBHOOK_TRIGGERS, "webhook_triggers_id_unique", new TableField[] { WebhookTriggers.WEBHOOK_TRIGGERS.ID }, true);
        public static final UniqueKey<WebhooksRecord> WEBHOOKS_PK = Internal.createUniqueKey(Webhooks.WEBHOOKS, "webhooks_pk", new TableField[] { Webhooks.WEBHOOKS.PK }, true);
        public static final UniqueKey<WebhooksRecord> WEBHOOKS_ID_UNIQUE = Internal.createUniqueKey(Webhooks.WEBHOOKS, "webhooks_id_unique", new TableField[] { Webhooks.WEBHOOKS.ID }, true);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<AlbumSeriesRecord, AlbumsRecord> ALBUM_SERIES__ALBUM_SERIES_ALBUM_FK_FKEY = Internal.createForeignKey(Keys.ALBUM_PK, AlbumSeries.ALBUM_SERIES, "album_series_album_fk_fkey", new TableField[] { AlbumSeries.ALBUM_SERIES.ALBUM_FK }, true);
        public static final ForeignKey<AlbumSeriesRecord, SeriesRecord> ALBUM_SERIES__ALBUM_SERIES_SERIES_FK_FKEY = Internal.createForeignKey(Keys.SERIES_PK, AlbumSeries.ALBUM_SERIES, "album_series_series_fk_fkey", new TableField[] { AlbumSeries.ALBUM_SERIES.SERIES_FK }, true);
        public static final ForeignKey<AlbumUserRecord, AlbumsRecord> ALBUM_USER__ALBUM_USER_ALBUM_FK_FKEY = Internal.createForeignKey(Keys.ALBUM_PK, AlbumUser.ALBUM_USER, "album_user_album_fk_fkey", new TableField[] { AlbumUser.ALBUM_USER.ALBUM_FK }, true);
        public static final ForeignKey<AlbumUserRecord, UsersRecord> ALBUM_USER__ALBUM_USER_USER_FK_FKEY = Internal.createForeignKey(Keys.USERS_PK, AlbumUser.ALBUM_USER, "album_user_user_fk_fkey", new TableField[] { AlbumUser.ALBUM_USER.USER_FK }, true);
        public static final ForeignKey<CapabilitiesRecord, UsersRecord> CAPABILITIES__CAPABILITIES_USER_FK_FKEY = Internal.createForeignKey(Keys.USERS_PK, Capabilities.CAPABILITIES, "capabilities_user_fk_fkey", new TableField[] { Capabilities.CAPABILITIES.USER_FK }, true);
        public static final ForeignKey<CapabilitiesRecord, AlbumsRecord> CAPABILITIES__CAPABILITIES_ALBUM_FK_FKEY = Internal.createForeignKey(Keys.ALBUM_PK, Capabilities.CAPABILITIES, "capabilities_album_fk_fkey", new TableField[] { Capabilities.CAPABILITIES.ALBUM_FK }, true);
        public static final ForeignKey<EventSeriesRecord, EventsRecord> EVENT_SERIES__EVENT_SERIES_EVENT_FK_FKEY = Internal.createForeignKey(Keys.EVENT_PK, EventSeries.EVENT_SERIES, "event_series_event_fk_fkey", new TableField[] { EventSeries.EVENT_SERIES.EVENT_FK }, true);
        public static final ForeignKey<EventSeriesRecord, SeriesRecord> EVENT_SERIES__EVENT_SERIES_SERIES_FK_FKEY = Internal.createForeignKey(Keys.SERIES_PK, EventSeries.EVENT_SERIES, "event_series_series_fk_fkey", new TableField[] { EventSeries.EVENT_SERIES.SERIES_FK }, true);
        public static final ForeignKey<EventsRecord, AlbumsRecord> EVENTS__EVENT_ALBUM_FK_FKEY = Internal.createForeignKey(Keys.ALBUM_PK, Events.EVENTS, "event_album_fk_fkey", new TableField[] { Events.EVENTS.ALBUM_FK }, true);
        public static final ForeignKey<EventsRecord, StudiesRecord> EVENTS__EVENT_STUDY_FK_FKEY = Internal.createForeignKey(Keys.STUDIES_PK, Events.EVENTS, "event_study_fk_fkey", new TableField[] { Events.EVENTS.STUDY_FK }, true);
        public static final ForeignKey<EventsRecord, UsersRecord> EVENTS__EVENT_USER_FK_FKEY = Internal.createForeignKey(Keys.USERS_PK, Events.EVENTS, "event_user_fk_fkey", new TableField[] { Events.EVENTS.USER_FK }, true);
        public static final ForeignKey<EventsRecord, CapabilitiesRecord> EVENTS__EVENT_CAPABILITY_FK_FKEY = Internal.createForeignKey(Keys.CAPABILITIES_PK, Events.EVENTS, "event_capability_fk_fkey", new TableField[] { Events.EVENTS.CAPABILITY_FK }, true);
        public static final ForeignKey<EventsRecord, UsersRecord> EVENTS__EVENT_PRIVATE_TARGET_USER_FK_FKEY = Internal.createForeignKey(Keys.USERS_PK, Events.EVENTS, "event_private_target_user_fk_fkey", new TableField[] { Events.EVENTS.PRIVATE_TARGET_USER_FK }, true);
        public static final ForeignKey<EventsRecord, UsersRecord> EVENTS__EVENT_TO_USER_FK_FKEY = Internal.createForeignKey(Keys.USERS_PK, Events.EVENTS, "event_to_user_fk_fkey", new TableField[] { Events.EVENTS.TO_USER_FK }, true);
        public static final ForeignKey<EventsRecord, ReportProvidersRecord> EVENTS__EVENT_REPORT_PROVIDER_FK_FKEY = Internal.createForeignKey(Keys.REPORT_PROVIDERS_PK, Events.EVENTS, "event_report_provider_fk_fkey", new TableField[] { Events.EVENTS.REPORT_PROVIDER_FK }, true);
        public static final ForeignKey<InstancesRecord, SeriesRecord> INSTANCES__INSTANCES_SERIES_FK_FKEY = Internal.createForeignKey(Keys.SERIES_PK, Instances.INSTANCES, "instances_series_fk_fkey", new TableField[] { Instances.INSTANCES.SERIES_FK }, true);
        public static final ForeignKey<ReportProvidersRecord, AlbumsRecord> REPORT_PROVIDERS__REPORT_PROVIDERS_ALBUM_FK_FKEY = Internal.createForeignKey(Keys.ALBUM_PK, ReportProviders.REPORT_PROVIDERS, "report_providers_album_fk_fkey", new TableField[] { ReportProviders.REPORT_PROVIDERS.ALBUM_FK }, true);
        public static final ForeignKey<SeriesRecord, StudiesRecord> SERIES__SERIES_STUDY_FK_FKEY = Internal.createForeignKey(Keys.STUDIES_PK, Series.SERIES, "series_study_fk_fkey", new TableField[] { Series.SERIES.STUDY_FK }, true);
        public static final ForeignKey<UsersRecord, AlbumsRecord> USERS__USERS_INBOX_FK_FKEY = Internal.createForeignKey(Keys.ALBUM_PK, Users.USERS, "users_inbox_fk_fkey", new TableField[] { Users.USERS.INBOX_FK }, true);
        public static final ForeignKey<WebhookAttemptsRecord, WebhookTriggersRecord> WEBHOOK_ATTEMPTS__WEBHOOK_ATTEMPTS_WEBHOOK_TRIGGERS_FK_FKEY = Internal.createForeignKey(Keys.WEBHOOK_TRIGGERS_PK, WebhookAttempts.WEBHOOK_ATTEMPTS, "webhook_attempts_webhook_triggers_fk_fkey", new TableField[] { WebhookAttempts.WEBHOOK_ATTEMPTS.WEBHOOK_TRIGGER_FK }, true);
        public static final ForeignKey<WebhookTriggerSeriesRecord, WebhookTriggersRecord> WEBHOOK_TRIGGER_SERIES__WEBHOOK_TRIGGER_SERIES_WEBHOOK_TRIGGER_FK_FKEY = Internal.createForeignKey(Keys.WEBHOOK_TRIGGERS_PK, WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES, "webhook_trigger_series_webhook_trigger_fk_fkey", new TableField[] { WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES.WEBHOOK_TRIGGER_FK }, true);
        public static final ForeignKey<WebhookTriggerSeriesRecord, SeriesRecord> WEBHOOK_TRIGGER_SERIES__WEBHOOK_TRIGGER_SERIES_SERIES_FK_FKEY = Internal.createForeignKey(Keys.SERIES_PK, WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES, "webhook_trigger_series_series_fk_fkey", new TableField[] { WebhookTriggerSeries.WEBHOOK_TRIGGER_SERIES.SERIES_FK }, true);
        public static final ForeignKey<WebhookTriggersRecord, WebhooksRecord> WEBHOOK_TRIGGERS__WEBHOOK_TRIGGERS_WEBHOOK_FK_FKEY = Internal.createForeignKey(Keys.WEBHOOKS_PK, WebhookTriggers.WEBHOOK_TRIGGERS, "webhook_triggers_webhook_fk_fkey", new TableField[] { WebhookTriggers.WEBHOOK_TRIGGERS.WEBHOOK_FK }, true);
        public static final ForeignKey<WebhookTriggersRecord, UsersRecord> WEBHOOK_TRIGGERS__WEBHOOK_TRIGGERS_USER_FK_FKEY = Internal.createForeignKey(Keys.USERS_PK, WebhookTriggers.WEBHOOK_TRIGGERS, "webhook_triggers_user_fk_fkey", new TableField[] { WebhookTriggers.WEBHOOK_TRIGGERS.USER_FK }, true);
        public static final ForeignKey<WebhooksRecord, UsersRecord> WEBHOOKS__WEBHOOK_USER_FK_FKEY = Internal.createForeignKey(Keys.USERS_PK, Webhooks.WEBHOOKS, "webhook_user_fk_fkey", new TableField[] { Webhooks.WEBHOOKS.USER_FK }, true);
        public static final ForeignKey<WebhooksRecord, AlbumsRecord> WEBHOOKS__WEBHOOK_ALBUM_FK_FKEY = Internal.createForeignKey(Keys.ALBUM_PK, Webhooks.WEBHOOKS, "webhook_album_fk_fkey", new TableField[] { Webhooks.WEBHOOKS.ALBUM_FK }, true);
    }
}

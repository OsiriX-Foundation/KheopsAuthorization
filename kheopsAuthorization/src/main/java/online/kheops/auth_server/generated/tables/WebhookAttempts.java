/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import online.kheops.auth_server.generated.Indexes;
import online.kheops.auth_server.generated.Keys;
import online.kheops.auth_server.generated.Public;
import online.kheops.auth_server.generated.tables.records.WebhookAttemptsRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WebhookAttempts extends TableImpl<WebhookAttemptsRecord> {

    private static final long serialVersionUID = 1581176458;

    /**
     * The reference instance of <code>public.webhook_attempts</code>
     */
    public static final WebhookAttempts WEBHOOK_ATTEMPTS = new WebhookAttempts();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WebhookAttemptsRecord> getRecordType() {
        return WebhookAttemptsRecord.class;
    }

    /**
     * The column <code>public.webhook_attempts.pk</code>.
     */
    public final TableField<WebhookAttemptsRecord, Long> PK = createField(DSL.name("pk"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.webhook_attempts.status</code>.
     */
    public final TableField<WebhookAttemptsRecord, Long> STATUS = createField(DSL.name("status"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.webhook_attempts.time</code>.
     */
    public final TableField<WebhookAttemptsRecord, LocalDateTime> TIME = createField(DSL.name("time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>public.webhook_attempts.webhook_trigger_fk</code>.
     */
    public final TableField<WebhookAttemptsRecord, Long> WEBHOOK_TRIGGER_FK = createField(DSL.name("webhook_trigger_fk"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.webhook_attempts.attempt</code>.
     */
    public final TableField<WebhookAttemptsRecord, Long> ATTEMPT = createField(DSL.name("attempt"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>public.webhook_attempts</code> table reference
     */
    public WebhookAttempts() {
        this(DSL.name("webhook_attempts"), null);
    }

    /**
     * Create an aliased <code>public.webhook_attempts</code> table reference
     */
    public WebhookAttempts(String alias) {
        this(DSL.name(alias), WEBHOOK_ATTEMPTS);
    }

    /**
     * Create an aliased <code>public.webhook_attempts</code> table reference
     */
    public WebhookAttempts(Name alias) {
        this(alias, WEBHOOK_ATTEMPTS);
    }

    private WebhookAttempts(Name alias, Table<WebhookAttemptsRecord> aliased) {
        this(alias, aliased, null);
    }

    private WebhookAttempts(Name alias, Table<WebhookAttemptsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> WebhookAttempts(Table<O> child, ForeignKey<O, WebhookAttemptsRecord> key) {
        super(child, key, WEBHOOK_ATTEMPTS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.WEBHOOK_ATTEMPTS_WEBHOOK_TRIGGER_FK_INDEX);
    }

    @Override
    public Identity<WebhookAttemptsRecord, Long> getIdentity() {
        return Keys.IDENTITY_WEBHOOK_ATTEMPTS;
    }

    @Override
    public UniqueKey<WebhookAttemptsRecord> getPrimaryKey() {
        return Keys.WEBHOOK_ATTEMPT_PK;
    }

    @Override
    public List<UniqueKey<WebhookAttemptsRecord>> getKeys() {
        return Arrays.<UniqueKey<WebhookAttemptsRecord>>asList(Keys.WEBHOOK_ATTEMPT_PK);
    }

    @Override
    public List<ForeignKey<WebhookAttemptsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WebhookAttemptsRecord, ?>>asList(Keys.WEBHOOK_ATTEMPTS__WEBHOOK_ATTEMPTS_WEBHOOK_TRIGGERS_FK_FKEY);
    }

    public WebhookTriggers webhookTriggers() {
        return new WebhookTriggers(this, Keys.WEBHOOK_ATTEMPTS__WEBHOOK_ATTEMPTS_WEBHOOK_TRIGGERS_FK_FKEY);
    }

    @Override
    public WebhookAttempts as(String alias) {
        return new WebhookAttempts(DSL.name(alias), this);
    }

    @Override
    public WebhookAttempts as(Name alias) {
        return new WebhookAttempts(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WebhookAttempts rename(String name) {
        return new WebhookAttempts(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WebhookAttempts rename(Name name) {
        return new WebhookAttempts(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, LocalDateTime, Long, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}

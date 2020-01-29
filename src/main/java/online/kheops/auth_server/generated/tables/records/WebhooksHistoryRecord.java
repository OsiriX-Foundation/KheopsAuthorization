/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import online.kheops.auth_server.generated.tables.WebhooksHistory;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WebhooksHistoryRecord extends UpdatableRecordImpl<WebhooksHistoryRecord> implements Record9<Long, String, Long, Timestamp, Long, Boolean, Boolean, Boolean, Long> {

    private static final long serialVersionUID = 81608553;

    /**
     * Setter for <code>public.webhooks_history.pk</code>.
     */
    public void setPk(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.webhooks_history.pk</code>.
     */
    public Long getPk() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.webhooks_history.id</code>.
     */
    public void setId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.webhooks_history.id</code>.
     */
    public String getId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.webhooks_history.status</code>.
     */
    public void setStatus(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.webhooks_history.status</code>.
     */
    public Long getStatus() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.webhooks_history.time</code>.
     */
    public void setTime(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.webhooks_history.time</code>.
     */
    public Timestamp getTime() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>public.webhooks_history.webhook_fk</code>.
     */
    public void setWebhookFk(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.webhooks_history.webhook_fk</code>.
     */
    public Long getWebhookFk() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>public.webhooks_history.is_manual_trigger</code>.
     */
    public void setIsManualTrigger(Boolean value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.webhooks_history.is_manual_trigger</code>.
     */
    public Boolean getIsManualTrigger() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>public.webhooks_history.new_series</code>.
     */
    public void setNewSeries(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.webhooks_history.new_series</code>.
     */
    public Boolean getNewSeries() {
        return (Boolean) get(6);
    }

    /**
     * Setter for <code>public.webhooks_history.new_user</code>.
     */
    public void setNewUser(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.webhooks_history.new_user</code>.
     */
    public Boolean getNewUser() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>public.webhooks_history.attempt</code>.
     */
    public void setAttempt(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.webhooks_history.attempt</code>.
     */
    public Long getAttempt() {
        return (Long) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<Long, String, Long, Timestamp, Long, Boolean, Boolean, Boolean, Long> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<Long, String, Long, Timestamp, Long, Boolean, Boolean, Boolean, Long> valuesRow() {
        return (Row9) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return WebhooksHistory.WEBHOOKS_HISTORY.PK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return WebhooksHistory.WEBHOOKS_HISTORY.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field3() {
        return WebhooksHistory.WEBHOOKS_HISTORY.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return WebhooksHistory.WEBHOOKS_HISTORY.TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field5() {
        return WebhooksHistory.WEBHOOKS_HISTORY.WEBHOOK_FK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field6() {
        return WebhooksHistory.WEBHOOKS_HISTORY.IS_MANUAL_TRIGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field7() {
        return WebhooksHistory.WEBHOOKS_HISTORY.NEW_SERIES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field8() {
        return WebhooksHistory.WEBHOOKS_HISTORY.NEW_USER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field9() {
        return WebhooksHistory.WEBHOOKS_HISTORY.ATTEMPT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getPk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component3() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component5() {
        return getWebhookFk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component6() {
        return getIsManualTrigger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component7() {
        return getNewSeries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component8() {
        return getNewUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component9() {
        return getAttempt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getPk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value3() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value5() {
        return getWebhookFk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value6() {
        return getIsManualTrigger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value7() {
        return getNewSeries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value8() {
        return getNewUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value9() {
        return getAttempt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value1(Long value) {
        setPk(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value2(String value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value3(Long value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value4(Timestamp value) {
        setTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value5(Long value) {
        setWebhookFk(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value6(Boolean value) {
        setIsManualTrigger(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value7(Boolean value) {
        setNewSeries(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value8(Boolean value) {
        setNewUser(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord value9(Long value) {
        setAttempt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhooksHistoryRecord values(Long value1, String value2, Long value3, Timestamp value4, Long value5, Boolean value6, Boolean value7, Boolean value8, Long value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WebhooksHistoryRecord
     */
    public WebhooksHistoryRecord() {
        super(WebhooksHistory.WEBHOOKS_HISTORY);
    }

    /**
     * Create a detached, initialised WebhooksHistoryRecord
     */
    public WebhooksHistoryRecord(Long pk, String id, Long status, Timestamp time, Long webhookFk, Boolean isManualTrigger, Boolean newSeries, Boolean newUser, Long attempt) {
        super(WebhooksHistory.WEBHOOKS_HISTORY);

        set(0, pk);
        set(1, id);
        set(2, status);
        set(3, time);
        set(4, webhookFk);
        set(5, isManualTrigger);
        set(6, newSeries);
        set(7, newUser);
        set(8, attempt);
    }
}
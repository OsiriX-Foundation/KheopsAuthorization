/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated.tables.records;


import online.kheops.auth_server.generated.tables.WebhookTriggers;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WebhookTriggersRecord extends UpdatableRecordImpl<WebhookTriggersRecord> implements Record8<Long, String, Long, Boolean, Boolean, Boolean, Long, Boolean> {

    private static final long serialVersionUID = 1067901923;

    /**
     * Setter for <code>public.webhook_triggers.pk</code>.
     */
    public void setPk(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.pk</code>.
     */
    public Long getPk() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.webhook_triggers.id</code>.
     */
    public void setId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.id</code>.
     */
    public String getId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.webhook_triggers.webhook_fk</code>.
     */
    public void setWebhookFk(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.webhook_fk</code>.
     */
    public Long getWebhookFk() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.webhook_triggers.is_manual_trigger</code>.
     */
    public void setIsManualTrigger(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.is_manual_trigger</code>.
     */
    public Boolean getIsManualTrigger() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>public.webhook_triggers.new_series</code>.
     */
    public void setNewSeries(Boolean value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.new_series</code>.
     */
    public Boolean getNewSeries() {
        return (Boolean) get(4);
    }

    /**
     * Setter for <code>public.webhook_triggers.new_user</code>.
     */
    public void setNewUser(Boolean value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.new_user</code>.
     */
    public Boolean getNewUser() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>public.webhook_triggers.user_fk</code>.
     */
    public void setUserFk(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.user_fk</code>.
     */
    public Long getUserFk() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>public.webhook_triggers.remove_series</code>.
     */
    public void setRemoveSeries(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.webhook_triggers.remove_series</code>.
     */
    public Boolean getRemoveSeries() {
        return (Boolean) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, String, Long, Boolean, Boolean, Boolean, Long, Boolean> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, String, Long, Boolean, Boolean, Boolean, Long, Boolean> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.PK;
    }

    @Override
    public Field<String> field2() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.ID;
    }

    @Override
    public Field<Long> field3() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.WEBHOOK_FK;
    }

    @Override
    public Field<Boolean> field4() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.IS_MANUAL_TRIGGER;
    }

    @Override
    public Field<Boolean> field5() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.NEW_SERIES;
    }

    @Override
    public Field<Boolean> field6() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.NEW_USER;
    }

    @Override
    public Field<Long> field7() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.USER_FK;
    }

    @Override
    public Field<Boolean> field8() {
        return WebhookTriggers.WEBHOOK_TRIGGERS.REMOVE_SERIES;
    }

    @Override
    public Long component1() {
        return getPk();
    }

    @Override
    public String component2() {
        return getId();
    }

    @Override
    public Long component3() {
        return getWebhookFk();
    }

    @Override
    public Boolean component4() {
        return getIsManualTrigger();
    }

    @Override
    public Boolean component5() {
        return getNewSeries();
    }

    @Override
    public Boolean component6() {
        return getNewUser();
    }

    @Override
    public Long component7() {
        return getUserFk();
    }

    @Override
    public Boolean component8() {
        return getRemoveSeries();
    }

    @Override
    public Long value1() {
        return getPk();
    }

    @Override
    public String value2() {
        return getId();
    }

    @Override
    public Long value3() {
        return getWebhookFk();
    }

    @Override
    public Boolean value4() {
        return getIsManualTrigger();
    }

    @Override
    public Boolean value5() {
        return getNewSeries();
    }

    @Override
    public Boolean value6() {
        return getNewUser();
    }

    @Override
    public Long value7() {
        return getUserFk();
    }

    @Override
    public Boolean value8() {
        return getRemoveSeries();
    }

    @Override
    public WebhookTriggersRecord value1(Long value) {
        setPk(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord value2(String value) {
        setId(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord value3(Long value) {
        setWebhookFk(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord value4(Boolean value) {
        setIsManualTrigger(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord value5(Boolean value) {
        setNewSeries(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord value6(Boolean value) {
        setNewUser(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord value7(Long value) {
        setUserFk(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord value8(Boolean value) {
        setRemoveSeries(value);
        return this;
    }

    @Override
    public WebhookTriggersRecord values(Long value1, String value2, Long value3, Boolean value4, Boolean value5, Boolean value6, Long value7, Boolean value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WebhookTriggersRecord
     */
    public WebhookTriggersRecord() {
        super(WebhookTriggers.WEBHOOK_TRIGGERS);
    }

    /**
     * Create a detached, initialised WebhookTriggersRecord
     */
    public WebhookTriggersRecord(Long pk, String id, Long webhookFk, Boolean isManualTrigger, Boolean newSeries, Boolean newUser, Long userFk, Boolean removeSeries) {
        super(WebhookTriggers.WEBHOOK_TRIGGERS);

        set(0, pk);
        set(1, id);
        set(2, webhookFk);
        set(3, isManualTrigger);
        set(4, newSeries);
        set(5, newUser);
        set(6, userFk);
        set(7, removeSeries);
    }
}

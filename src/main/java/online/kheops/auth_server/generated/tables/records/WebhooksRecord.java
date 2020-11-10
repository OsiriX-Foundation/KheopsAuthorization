/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated.tables.records;


import java.time.LocalDateTime;

import online.kheops.auth_server.generated.tables.Webhooks;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WebhooksRecord extends UpdatableRecordImpl<WebhooksRecord> implements Record13<Long, String, String, String, Boolean, LocalDateTime, Long, String, Boolean, Boolean, Long, Boolean, Boolean> {

    private static final long serialVersionUID = -331983080;

    /**
     * Setter for <code>public.webhooks.pk</code>.
     */
    public void setPk(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.webhooks.pk</code>.
     */
    public Long getPk() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.webhooks.id</code>.
     */
    public void setId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.webhooks.id</code>.
     */
    public String getId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.webhooks.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.webhooks.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.webhooks.url</code>.
     */
    public void setUrl(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.webhooks.url</code>.
     */
    public String getUrl() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.webhooks.enabled</code>.
     */
    public void setEnabled(Boolean value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.webhooks.enabled</code>.
     */
    public Boolean getEnabled() {
        return (Boolean) get(4);
    }

    /**
     * Setter for <code>public.webhooks.creation_time</code>.
     */
    public void setCreationTime(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.webhooks.creation_time</code>.
     */
    public LocalDateTime getCreationTime() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>public.webhooks.user_fk</code>.
     */
    public void setUserFk(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.webhooks.user_fk</code>.
     */
    public Long getUserFk() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>public.webhooks.secret</code>.
     */
    public void setSecret(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.webhooks.secret</code>.
     */
    public String getSecret() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.webhooks.new_series</code>.
     */
    public void setNewSeries(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.webhooks.new_series</code>.
     */
    public Boolean getNewSeries() {
        return (Boolean) get(8);
    }

    /**
     * Setter for <code>public.webhooks.new_user</code>.
     */
    public void setNewUser(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.webhooks.new_user</code>.
     */
    public Boolean getNewUser() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>public.webhooks.album_fk</code>.
     */
    public void setAlbumFk(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.webhooks.album_fk</code>.
     */
    public Long getAlbumFk() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>public.webhooks.remove_series</code>.
     */
    public void setRemoveSeries(Boolean value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.webhooks.remove_series</code>.
     */
    public Boolean getRemoveSeries() {
        return (Boolean) get(11);
    }

    /**
     * Setter for <code>public.webhooks.delete_album</code>.
     */
    public void setDeleteAlbum(Boolean value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.webhooks.delete_album</code>.
     */
    public Boolean getDeleteAlbum() {
        return (Boolean) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, String, String, String, Boolean, LocalDateTime, Long, String, Boolean, Boolean, Long, Boolean, Boolean> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<Long, String, String, String, Boolean, LocalDateTime, Long, String, Boolean, Boolean, Long, Boolean, Boolean> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Webhooks.WEBHOOKS.PK;
    }

    @Override
    public Field<String> field2() {
        return Webhooks.WEBHOOKS.ID;
    }

    @Override
    public Field<String> field3() {
        return Webhooks.WEBHOOKS.NAME;
    }

    @Override
    public Field<String> field4() {
        return Webhooks.WEBHOOKS.URL;
    }

    @Override
    public Field<Boolean> field5() {
        return Webhooks.WEBHOOKS.ENABLED;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Webhooks.WEBHOOKS.CREATION_TIME;
    }

    @Override
    public Field<Long> field7() {
        return Webhooks.WEBHOOKS.USER_FK;
    }

    @Override
    public Field<String> field8() {
        return Webhooks.WEBHOOKS.SECRET;
    }

    @Override
    public Field<Boolean> field9() {
        return Webhooks.WEBHOOKS.NEW_SERIES;
    }

    @Override
    public Field<Boolean> field10() {
        return Webhooks.WEBHOOKS.NEW_USER;
    }

    @Override
    public Field<Long> field11() {
        return Webhooks.WEBHOOKS.ALBUM_FK;
    }

    @Override
    public Field<Boolean> field12() {
        return Webhooks.WEBHOOKS.REMOVE_SERIES;
    }

    @Override
    public Field<Boolean> field13() {
        return Webhooks.WEBHOOKS.DELETE_ALBUM;
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
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getUrl();
    }

    @Override
    public Boolean component5() {
        return getEnabled();
    }

    @Override
    public LocalDateTime component6() {
        return getCreationTime();
    }

    @Override
    public Long component7() {
        return getUserFk();
    }

    @Override
    public String component8() {
        return getSecret();
    }

    @Override
    public Boolean component9() {
        return getNewSeries();
    }

    @Override
    public Boolean component10() {
        return getNewUser();
    }

    @Override
    public Long component11() {
        return getAlbumFk();
    }

    @Override
    public Boolean component12() {
        return getRemoveSeries();
    }

    @Override
    public Boolean component13() {
        return getDeleteAlbum();
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
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getUrl();
    }

    @Override
    public Boolean value5() {
        return getEnabled();
    }

    @Override
    public LocalDateTime value6() {
        return getCreationTime();
    }

    @Override
    public Long value7() {
        return getUserFk();
    }

    @Override
    public String value8() {
        return getSecret();
    }

    @Override
    public Boolean value9() {
        return getNewSeries();
    }

    @Override
    public Boolean value10() {
        return getNewUser();
    }

    @Override
    public Long value11() {
        return getAlbumFk();
    }

    @Override
    public Boolean value12() {
        return getRemoveSeries();
    }

    @Override
    public Boolean value13() {
        return getDeleteAlbum();
    }

    @Override
    public WebhooksRecord value1(Long value) {
        setPk(value);
        return this;
    }

    @Override
    public WebhooksRecord value2(String value) {
        setId(value);
        return this;
    }

    @Override
    public WebhooksRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public WebhooksRecord value4(String value) {
        setUrl(value);
        return this;
    }

    @Override
    public WebhooksRecord value5(Boolean value) {
        setEnabled(value);
        return this;
    }

    @Override
    public WebhooksRecord value6(LocalDateTime value) {
        setCreationTime(value);
        return this;
    }

    @Override
    public WebhooksRecord value7(Long value) {
        setUserFk(value);
        return this;
    }

    @Override
    public WebhooksRecord value8(String value) {
        setSecret(value);
        return this;
    }

    @Override
    public WebhooksRecord value9(Boolean value) {
        setNewSeries(value);
        return this;
    }

    @Override
    public WebhooksRecord value10(Boolean value) {
        setNewUser(value);
        return this;
    }

    @Override
    public WebhooksRecord value11(Long value) {
        setAlbumFk(value);
        return this;
    }

    @Override
    public WebhooksRecord value12(Boolean value) {
        setRemoveSeries(value);
        return this;
    }

    @Override
    public WebhooksRecord value13(Boolean value) {
        setDeleteAlbum(value);
        return this;
    }

    @Override
    public WebhooksRecord values(Long value1, String value2, String value3, String value4, Boolean value5, LocalDateTime value6, Long value7, String value8, Boolean value9, Boolean value10, Long value11, Boolean value12, Boolean value13) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WebhooksRecord
     */
    public WebhooksRecord() {
        super(Webhooks.WEBHOOKS);
    }

    /**
     * Create a detached, initialised WebhooksRecord
     */
    public WebhooksRecord(Long pk, String id, String name, String url, Boolean enabled, LocalDateTime creationTime, Long userFk, String secret, Boolean newSeries, Boolean newUser, Long albumFk, Boolean removeSeries, Boolean deleteAlbum) {
        super(Webhooks.WEBHOOKS);

        set(0, pk);
        set(1, id);
        set(2, name);
        set(3, url);
        set(4, enabled);
        set(5, creationTime);
        set(6, userFk);
        set(7, secret);
        set(8, newSeries);
        set(9, newUser);
        set(10, albumFk);
        set(11, removeSeries);
        set(12, deleteAlbum);
    }
}

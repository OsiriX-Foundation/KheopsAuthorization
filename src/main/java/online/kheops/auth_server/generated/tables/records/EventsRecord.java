/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated.tables.records;


import java.time.LocalDateTime;

import online.kheops.auth_server.generated.tables.Events;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EventsRecord extends UpdatableRecordImpl<EventsRecord> implements Record13<Long, String, Long, Long, LocalDateTime, Long, Long, Long, String, String, Long, Long, Long> {

    private static final long serialVersionUID = -396518233;

    /**
     * Setter for <code>public.events.pk</code>.
     */
    public void setPk(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.events.pk</code>.
     */
    public Long getPk() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.events.event_type</code>.
     */
    public void setEventType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.events.event_type</code>.
     */
    public String getEventType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.events.album_fk</code>.
     */
    public void setAlbumFk(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.events.album_fk</code>.
     */
    public Long getAlbumFk() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.events.study_fk</code>.
     */
    public void setStudyFk(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.events.study_fk</code>.
     */
    public Long getStudyFk() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>public.events.event_time</code>.
     */
    public void setEventTime(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.events.event_time</code>.
     */
    public LocalDateTime getEventTime() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>public.events.user_fk</code>.
     */
    public void setUserFk(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.events.user_fk</code>.
     */
    public Long getUserFk() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>public.events.capability_fk</code>.
     */
    public void setCapabilityFk(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.events.capability_fk</code>.
     */
    public Long getCapabilityFk() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>public.events.private_target_user_fk</code>.
     */
    public void setPrivateTargetUserFk(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.events.private_target_user_fk</code>.
     */
    public Long getPrivateTargetUserFk() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>public.events.comment</code>.
     */
    public void setComment(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.events.comment</code>.
     */
    public String getComment() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.events.mutation_type</code>.
     */
    public void setMutationType(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.events.mutation_type</code>.
     */
    public String getMutationType() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.events.to_user_fk</code>.
     */
    public void setToUserFk(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.events.to_user_fk</code>.
     */
    public Long getToUserFk() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>public.events.series_fk</code>.
     */
    public void setSeriesFk(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.events.series_fk</code>.
     */
    public Long getSeriesFk() {
        return (Long) get(11);
    }

    /**
     * Setter for <code>public.events.report_provider_fk</code>.
     */
    public void setReportProviderFk(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.events.report_provider_fk</code>.
     */
    public Long getReportProviderFk() {
        return (Long) get(12);
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
    public Row13<Long, String, Long, Long, LocalDateTime, Long, Long, Long, String, String, Long, Long, Long> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<Long, String, Long, Long, LocalDateTime, Long, Long, Long, String, String, Long, Long, Long> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Events.EVENTS.PK;
    }

    @Override
    public Field<String> field2() {
        return Events.EVENTS.EVENT_TYPE;
    }

    @Override
    public Field<Long> field3() {
        return Events.EVENTS.ALBUM_FK;
    }

    @Override
    public Field<Long> field4() {
        return Events.EVENTS.STUDY_FK;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return Events.EVENTS.EVENT_TIME;
    }

    @Override
    public Field<Long> field6() {
        return Events.EVENTS.USER_FK;
    }

    @Override
    public Field<Long> field7() {
        return Events.EVENTS.CAPABILITY_FK;
    }

    @Override
    public Field<Long> field8() {
        return Events.EVENTS.PRIVATE_TARGET_USER_FK;
    }

    @Override
    public Field<String> field9() {
        return Events.EVENTS.COMMENT;
    }

    @Override
    public Field<String> field10() {
        return Events.EVENTS.MUTATION_TYPE;
    }

    @Override
    public Field<Long> field11() {
        return Events.EVENTS.TO_USER_FK;
    }

    @Override
    public Field<Long> field12() {
        return Events.EVENTS.SERIES_FK;
    }

    @Override
    public Field<Long> field13() {
        return Events.EVENTS.REPORT_PROVIDER_FK;
    }

    @Override
    public Long component1() {
        return getPk();
    }

    @Override
    public String component2() {
        return getEventType();
    }

    @Override
    public Long component3() {
        return getAlbumFk();
    }

    @Override
    public Long component4() {
        return getStudyFk();
    }

    @Override
    public LocalDateTime component5() {
        return getEventTime();
    }

    @Override
    public Long component6() {
        return getUserFk();
    }

    @Override
    public Long component7() {
        return getCapabilityFk();
    }

    @Override
    public Long component8() {
        return getPrivateTargetUserFk();
    }

    @Override
    public String component9() {
        return getComment();
    }

    @Override
    public String component10() {
        return getMutationType();
    }

    @Override
    public Long component11() {
        return getToUserFk();
    }

    @Override
    public Long component12() {
        return getSeriesFk();
    }

    @Override
    public Long component13() {
        return getReportProviderFk();
    }

    @Override
    public Long value1() {
        return getPk();
    }

    @Override
    public String value2() {
        return getEventType();
    }

    @Override
    public Long value3() {
        return getAlbumFk();
    }

    @Override
    public Long value4() {
        return getStudyFk();
    }

    @Override
    public LocalDateTime value5() {
        return getEventTime();
    }

    @Override
    public Long value6() {
        return getUserFk();
    }

    @Override
    public Long value7() {
        return getCapabilityFk();
    }

    @Override
    public Long value8() {
        return getPrivateTargetUserFk();
    }

    @Override
    public String value9() {
        return getComment();
    }

    @Override
    public String value10() {
        return getMutationType();
    }

    @Override
    public Long value11() {
        return getToUserFk();
    }

    @Override
    public Long value12() {
        return getSeriesFk();
    }

    @Override
    public Long value13() {
        return getReportProviderFk();
    }

    @Override
    public EventsRecord value1(Long value) {
        setPk(value);
        return this;
    }

    @Override
    public EventsRecord value2(String value) {
        setEventType(value);
        return this;
    }

    @Override
    public EventsRecord value3(Long value) {
        setAlbumFk(value);
        return this;
    }

    @Override
    public EventsRecord value4(Long value) {
        setStudyFk(value);
        return this;
    }

    @Override
    public EventsRecord value5(LocalDateTime value) {
        setEventTime(value);
        return this;
    }

    @Override
    public EventsRecord value6(Long value) {
        setUserFk(value);
        return this;
    }

    @Override
    public EventsRecord value7(Long value) {
        setCapabilityFk(value);
        return this;
    }

    @Override
    public EventsRecord value8(Long value) {
        setPrivateTargetUserFk(value);
        return this;
    }

    @Override
    public EventsRecord value9(String value) {
        setComment(value);
        return this;
    }

    @Override
    public EventsRecord value10(String value) {
        setMutationType(value);
        return this;
    }

    @Override
    public EventsRecord value11(Long value) {
        setToUserFk(value);
        return this;
    }

    @Override
    public EventsRecord value12(Long value) {
        setSeriesFk(value);
        return this;
    }

    @Override
    public EventsRecord value13(Long value) {
        setReportProviderFk(value);
        return this;
    }

    @Override
    public EventsRecord values(Long value1, String value2, Long value3, Long value4, LocalDateTime value5, Long value6, Long value7, Long value8, String value9, String value10, Long value11, Long value12, Long value13) {
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
     * Create a detached EventsRecord
     */
    public EventsRecord() {
        super(Events.EVENTS);
    }

    /**
     * Create a detached, initialised EventsRecord
     */
    public EventsRecord(Long pk, String eventType, Long albumFk, Long studyFk, LocalDateTime eventTime, Long userFk, Long capabilityFk, Long privateTargetUserFk, String comment, String mutationType, Long toUserFk, Long seriesFk, Long reportProviderFk) {
        super(Events.EVENTS);

        set(0, pk);
        set(1, eventType);
        set(2, albumFk);
        set(3, studyFk);
        set(4, eventTime);
        set(5, userFk);
        set(6, capabilityFk);
        set(7, privateTargetUserFk);
        set(8, comment);
        set(9, mutationType);
        set(10, toUserFk);
        set(11, seriesFk);
        set(12, reportProviderFk);
    }
}

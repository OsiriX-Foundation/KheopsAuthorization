/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated.tables;


import java.util.Arrays;
import java.util.List;

import online.kheops.auth_server.generated.Indexes;
import online.kheops.auth_server.generated.Keys;
import online.kheops.auth_server.generated.Public;
import online.kheops.auth_server.generated.tables.records.EventSeriesRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
public class EventSeries extends TableImpl<EventSeriesRecord> {

    private static final long serialVersionUID = 134462258;

    /**
     * The reference instance of <code>public.event_series</code>
     */
    public static final EventSeries EVENT_SERIES = new EventSeries();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EventSeriesRecord> getRecordType() {
        return EventSeriesRecord.class;
    }

    /**
     * The column <code>public.event_series.pk</code>.
     */
    public final TableField<EventSeriesRecord, Long> PK = createField(DSL.name("pk"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.event_series.event_fk</code>.
     */
    public final TableField<EventSeriesRecord, Long> EVENT_FK = createField(DSL.name("event_fk"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.event_series.series_fk</code>.
     */
    public final TableField<EventSeriesRecord, Long> SERIES_FK = createField(DSL.name("series_fk"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>public.event_series</code> table reference
     */
    public EventSeries() {
        this(DSL.name("event_series"), null);
    }

    /**
     * Create an aliased <code>public.event_series</code> table reference
     */
    public EventSeries(String alias) {
        this(DSL.name(alias), EVENT_SERIES);
    }

    /**
     * Create an aliased <code>public.event_series</code> table reference
     */
    public EventSeries(Name alias) {
        this(alias, EVENT_SERIES);
    }

    private EventSeries(Name alias, Table<EventSeriesRecord> aliased) {
        this(alias, aliased, null);
    }

    private EventSeries(Name alias, Table<EventSeriesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> EventSeries(Table<O> child, ForeignKey<O, EventSeriesRecord> key) {
        super(child, key, EVENT_SERIES);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.EVENT_SERIES_EVENT_FK_INDEX);
    }

    @Override
    public Identity<EventSeriesRecord, Long> getIdentity() {
        return Keys.IDENTITY_EVENT_SERIES;
    }

    @Override
    public UniqueKey<EventSeriesRecord> getPrimaryKey() {
        return Keys.EVENT_SERIES_PK;
    }

    @Override
    public List<UniqueKey<EventSeriesRecord>> getKeys() {
        return Arrays.<UniqueKey<EventSeriesRecord>>asList(Keys.EVENT_SERIES_PK, Keys.EVENT_SERIES_UNIQUE);
    }

    @Override
    public List<ForeignKey<EventSeriesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<EventSeriesRecord, ?>>asList(Keys.EVENT_SERIES__EVENT_SERIES_EVENT_FK_FKEY, Keys.EVENT_SERIES__EVENT_SERIES_SERIES_FK_FKEY);
    }

    public Events events() {
        return new Events(this, Keys.EVENT_SERIES__EVENT_SERIES_EVENT_FK_FKEY);
    }

    public Series series() {
        return new Series(this, Keys.EVENT_SERIES__EVENT_SERIES_SERIES_FK_FKEY);
    }

    @Override
    public EventSeries as(String alias) {
        return new EventSeries(DSL.name(alias), this);
    }

    @Override
    public EventSeries as(Name alias) {
        return new EventSeries(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EventSeries rename(String name) {
        return new EventSeries(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EventSeries rename(Name name) {
        return new EventSeries(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}

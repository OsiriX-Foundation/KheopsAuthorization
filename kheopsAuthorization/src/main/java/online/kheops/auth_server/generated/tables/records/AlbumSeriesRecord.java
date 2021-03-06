/*
 * This file is generated by jOOQ.
 */
package online.kheops.auth_server.generated.tables.records;


import online.kheops.auth_server.generated.tables.AlbumSeries;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AlbumSeriesRecord extends UpdatableRecordImpl<AlbumSeriesRecord> implements Record4<Long, Long, Long, Boolean> {

    private static final long serialVersionUID = 22347716;

    /**
     * Setter for <code>public.album_series.pk</code>.
     */
    public void setPk(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.album_series.pk</code>.
     */
    public Long getPk() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.album_series.album_fk</code>.
     */
    public void setAlbumFk(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.album_series.album_fk</code>.
     */
    public Long getAlbumFk() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.album_series.series_fk</code>.
     */
    public void setSeriesFk(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.album_series.series_fk</code>.
     */
    public Long getSeriesFk() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.album_series.favorite</code>.
     */
    public void setFavorite(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.album_series.favorite</code>.
     */
    public Boolean getFavorite() {
        return (Boolean) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, Long, Boolean> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, Long, Long, Boolean> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return AlbumSeries.ALBUM_SERIES.PK;
    }

    @Override
    public Field<Long> field2() {
        return AlbumSeries.ALBUM_SERIES.ALBUM_FK;
    }

    @Override
    public Field<Long> field3() {
        return AlbumSeries.ALBUM_SERIES.SERIES_FK;
    }

    @Override
    public Field<Boolean> field4() {
        return AlbumSeries.ALBUM_SERIES.FAVORITE;
    }

    @Override
    public Long component1() {
        return getPk();
    }

    @Override
    public Long component2() {
        return getAlbumFk();
    }

    @Override
    public Long component3() {
        return getSeriesFk();
    }

    @Override
    public Boolean component4() {
        return getFavorite();
    }

    @Override
    public Long value1() {
        return getPk();
    }

    @Override
    public Long value2() {
        return getAlbumFk();
    }

    @Override
    public Long value3() {
        return getSeriesFk();
    }

    @Override
    public Boolean value4() {
        return getFavorite();
    }

    @Override
    public AlbumSeriesRecord value1(Long value) {
        setPk(value);
        return this;
    }

    @Override
    public AlbumSeriesRecord value2(Long value) {
        setAlbumFk(value);
        return this;
    }

    @Override
    public AlbumSeriesRecord value3(Long value) {
        setSeriesFk(value);
        return this;
    }

    @Override
    public AlbumSeriesRecord value4(Boolean value) {
        setFavorite(value);
        return this;
    }

    @Override
    public AlbumSeriesRecord values(Long value1, Long value2, Long value3, Boolean value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AlbumSeriesRecord
     */
    public AlbumSeriesRecord() {
        super(AlbumSeries.ALBUM_SERIES);
    }

    /**
     * Create a detached, initialised AlbumSeriesRecord
     */
    public AlbumSeriesRecord(Long pk, Long albumFk, Long seriesFk, Boolean favorite) {
        super(AlbumSeries.ALBUM_SERIES);

        set(0, pk);
        set(1, albumFk);
        set(2, seriesFk);
        set(3, favorite);
    }
}

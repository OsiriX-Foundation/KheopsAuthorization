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
import online.kheops.auth_server.generated.tables.records.StudiesRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row15;
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
public class Studies extends TableImpl<StudiesRecord> {

    private static final long serialVersionUID = -1552734792;

    /**
     * The reference instance of <code>public.studies</code>
     */
    public static final Studies STUDIES = new Studies();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StudiesRecord> getRecordType() {
        return StudiesRecord.class;
    }

    /**
     * The column <code>public.studies.pk</code>.
     */
    public final TableField<StudiesRecord, Long> PK = createField(DSL.name("pk"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.studies.created_time</code>.
     */
    public final TableField<StudiesRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("created_time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>public.studies.updated_time</code>.
     */
    public final TableField<StudiesRecord, LocalDateTime> UPDATED_TIME = createField(DSL.name("updated_time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>public.studies.study_uid</code>.
     */
    public final TableField<StudiesRecord, String> STUDY_UID = createField(DSL.name("study_uid"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>public.studies.study_date</code>.
     */
    public final TableField<StudiesRecord, String> STUDY_DATE = createField(DSL.name("study_date"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.studies.study_time</code>.
     */
    public final TableField<StudiesRecord, String> STUDY_TIME = createField(DSL.name("study_time"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.studies.study_description</code>.
     */
    public final TableField<StudiesRecord, String> STUDY_DESCRIPTION = createField(DSL.name("study_description"), org.jooq.impl.SQLDataType.VARCHAR(155), this, "");

    /**
     * The column <code>public.studies.timezone_offset_from_utc</code>.
     */
    public final TableField<StudiesRecord, String> TIMEZONE_OFFSET_FROM_UTC = createField(DSL.name("timezone_offset_from_utc"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.studies.accession_number</code>.
     */
    public final TableField<StudiesRecord, String> ACCESSION_NUMBER = createField(DSL.name("accession_number"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.studies.referring_physician_name</code>.
     */
    public final TableField<StudiesRecord, String> REFERRING_PHYSICIAN_NAME = createField(DSL.name("referring_physician_name"), org.jooq.impl.SQLDataType.VARCHAR(4095), this, "");

    /**
     * The column <code>public.studies.patient_name</code>.
     */
    public final TableField<StudiesRecord, String> PATIENT_NAME = createField(DSL.name("patient_name"), org.jooq.impl.SQLDataType.VARCHAR(4095), this, "");

    /**
     * The column <code>public.studies.patient_id</code>.
     */
    public final TableField<StudiesRecord, String> PATIENT_ID = createField(DSL.name("patient_id"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.studies.patient_birth_date</code>.
     */
    public final TableField<StudiesRecord, String> PATIENT_BIRTH_DATE = createField(DSL.name("patient_birth_date"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.studies.patient_sex</code>.
     */
    public final TableField<StudiesRecord, String> PATIENT_SEX = createField(DSL.name("patient_sex"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.studies.study_id</code>.
     */
    public final TableField<StudiesRecord, String> STUDY_ID = createField(DSL.name("study_id"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * Create a <code>public.studies</code> table reference
     */
    public Studies() {
        this(DSL.name("studies"), null);
    }

    /**
     * Create an aliased <code>public.studies</code> table reference
     */
    public Studies(String alias) {
        this(DSL.name(alias), STUDIES);
    }

    /**
     * Create an aliased <code>public.studies</code> table reference
     */
    public Studies(Name alias) {
        this(alias, STUDIES);
    }

    private Studies(Name alias, Table<StudiesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Studies(Name alias, Table<StudiesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Studies(Table<O> child, ForeignKey<O, StudiesRecord> key) {
        super(child, key, STUDIES);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ACCESSION_NUMBER_INDEX, Indexes.PATIENT_ID_INDEX, Indexes.STUDY_DATE_INDEX, Indexes.STUDY_ID_INDEX, Indexes.STUDY_TIME_INDEX);
    }

    @Override
    public Identity<StudiesRecord, Long> getIdentity() {
        return Keys.IDENTITY_STUDIES;
    }

    @Override
    public UniqueKey<StudiesRecord> getPrimaryKey() {
        return Keys.STUDIES_PK;
    }

    @Override
    public List<UniqueKey<StudiesRecord>> getKeys() {
        return Arrays.<UniqueKey<StudiesRecord>>asList(Keys.STUDIES_PK, Keys.STUDY_UID_UNIQUE);
    }

    @Override
    public Studies as(String alias) {
        return new Studies(DSL.name(alias), this);
    }

    @Override
    public Studies as(Name alias) {
        return new Studies(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Studies rename(String name) {
        return new Studies(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Studies rename(Name name) {
        return new Studies(name, null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, LocalDateTime, LocalDateTime, String, String, String, String, String, String, String, String, String, String, String, String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }
}

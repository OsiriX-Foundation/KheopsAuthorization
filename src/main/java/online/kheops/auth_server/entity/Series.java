package online.kheops.auth_server.entity;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import static online.kheops.auth_server.series.Series.safeAttributeSetString;

@SuppressWarnings({"WeakerAccess", "unused"})

@NamedQueries({
        @NamedQuery(name = "Series.findAllByStudyUIDFromInbox",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE a = u.inbox AND u=:callingUser AND s.study.studyInstanceUID = :StudyInstanceUID"),
        @NamedQuery(name = "Series.findAllByStudyUIDFromAlbum",
                query = "SELECT s FROM Album a JOIN a.albumSeries alS JOIN alS.series s WHERE :album = a AND s.study.studyInstanceUID = :StudyInstanceUID"),
        @NamedQuery(name = "Series.findAllByStudyUIDFromInboxAndAlbum",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE u=:callingUser AND s.study.studyInstanceUID = :StudyInstanceUID"),
        @NamedQuery(name = "Series.findByStudyUIDFromInbox",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE a = u.inbox AND u=:callingUser AND s.study.studyInstanceUID = :StudyInstanceUID AND s.seriesInstanceUID = :SeriesInstanceUID"),
        @NamedQuery(name = "Series.findByStudyUIDFromAlbum",
                query = "SELECT s FROM Album a JOIN a.albumSeries alS JOIN alS.series s WHERE :album = a AND s.study.studyInstanceUID = :StudyInstanceUID AND s.seriesInstanceUID = :SeriesInstanceUID"),
        @NamedQuery(name = "Series.findBySeriesUIDAndStudyUIDAndUser",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE u=:callingUser AND s.study.studyInstanceUID = :StudyInstanceUID AND s.seriesInstanceUID = :SeriesInstanceUID"),
        @NamedQuery(name = "Series.findBySeriesUIDAndStudyUIDAndUserWithSharePermission",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE (a = u.inbox or au.admin = true or a.userPermission.sendSeries = true)AND u=:callingUser AND s.study.studyInstanceUID = :StudyInstanceUID AND s.seriesInstanceUID = :SeriesInstanceUID"),
        @NamedQuery(name = "Series.findBySeriesAndUserWithSharePermission",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE u=:callingUser AND s = :series AND (au.admin = true or a.userPermission.sendSeries = true)"),
        @NamedQuery(name = "Series.findBySeriesUIDAndStudyUID",
                query = "SELECT s FROM Series s JOIN s.study st WHERE s.seriesInstanceUID = :SeriesInstanceUID AND st.studyInstanceUID = :StudyInstanceUID"),
        @NamedQuery(name = "Series.findBySeriesUID",
                query = "SELECT s FROM Series s WHERE s.seriesInstanceUID = :SeriesInstanceUID"),
        @NamedQuery(name = "Series.findBySeriesFromInbox",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE u=:callingUser AND s = :series AND a = u.inbox"),
        @NamedQuery(name = "Series.isOrphan",
                query = "SELECT s FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE s = :series"),
        @NamedQuery(name = "Series.findAllUIDByStudyUIDFromAlbum",
                query = "SELECT new online.kheops.auth_server.series.SeriesUIDFavoritePair(s.seriesInstanceUID, alS.favorite) FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE s.study.studyInstanceUID = :StudyInstanceUID AND u.inbox <> a AND :user = u AND a = :album"),
        @NamedQuery(name = "Series.findAllUIDByStudyUIDFromInbox",
                query = "SELECT new online.kheops.auth_server.series.SeriesUIDFavoritePair(s.seriesInstanceUID, alS.favorite) FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE s.study.studyInstanceUID = :StudyInstanceUID AND u.inbox = a AND :user = u"),
        @NamedQuery(name = "Series.findAllUIDByStudyUIDFromInboxAndAlbum",
                query = "SELECT new online.kheops.auth_server.series.SeriesUIDFavoritePair(s.seriesInstanceUID) FROM User u JOIN u.albumUser au JOIN au.album a JOIN a.albumSeries alS JOIN alS.series s WHERE s.study.studyInstanceUID = :StudyInstanceUID AND :user = u")
})

@Entity
@Table(name = "series")
public class Series {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "pk")
    private long pk;

    @Basic(optional = false)
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @Basic(optional = false)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Basic(optional = false)
    @Column(name = "series_uid", updatable = false)
    private String seriesInstanceUID;

    @Column(name = "modality")
    private String modality;

    @Column(name = "timezone_offset_from_utc")
    private String timezoneOffsetFromUTC;

    @Column(name = "series_description")
    private String seriesDescription;

    @Column(name = "body_part_examined")
    private String bodyPartExamined;

    @Column(name = "series_number")
    private int seriesNumber;

    @Column(name = "number_of_series_related_instances")
    private int numberOfSeriesRelatedInstances;

    @Basic(optional = false)
    @Column(name = "populated")
    private boolean populated = false;

    @ManyToOne
    @JoinColumn(name = "study_fk", insertable = true, updatable=false)
    private Study study;

    @OneToMany(mappedBy = "series")
    private Set<AlbumSeries> albumsSeries = new HashSet<>();

    @OneToMany(mappedBy = "series")
    private Set<Instances> instances = new HashSet<>();

    public Series() {}

    public Series(String newSeriesInstanceUID) {
        seriesInstanceUID = newSeriesInstanceUID;
    }

    @PrePersist
    public void onPrePersist() {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        createdTime = now;
        updatedTime = now;
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedTime = LocalDateTime.now(ZoneOffset.UTC);
    }

    public Attributes getAttributes() {
        if (!isPopulated()) {
            throw new IllegalStateException();
        }

        Attributes attributes = new Attributes();

        safeAttributeSetString(attributes, Tag.Modality, VR.CS, getModality());
        safeAttributeSetString(attributes, Tag.TimezoneOffsetFromUTC, VR.SH, getTimezoneOffsetFromUTC());
        safeAttributeSetString(attributes, Tag.SeriesDescription, VR.LO, getSeriesDescription());
        safeAttributeSetString(attributes, Tag.SeriesInstanceUID, VR.UI, getSeriesInstanceUID());
        safeAttributeSetString(attributes, Tag.BodyPartExamined, VR.CS, getBodyPartExamined());

        attributes.setInt(Tag.SeriesNumber, VR.IS, getSeriesNumber());
        attributes.setInt(Tag.NumberOfSeriesRelatedInstances, VR.IS, getNumberOfSeriesRelatedInstances());

        return attributes;
    }

    // doesn't set populated, but the caller probably will want to set populated after calling this method
    public void mergeAttributes(Attributes attributes) {
        setModality(attributes.getString(Tag.Modality, getModality()));
        setBodyPartExamined(attributes.getString(Tag.BodyPartExamined, getBodyPartExamined()));
        setSeriesDescription(attributes.getString(Tag.SeriesDescription, getSeriesDescription()));
        setTimezoneOffsetFromUTC(attributes.getString(Tag.TimezoneOffsetFromUTC, getTimezoneOffsetFromUTC()));
        setSeriesNumber(attributes.getInt(Tag.SeriesNumber, getSeriesNumber()));
        setNumberOfSeriesRelatedInstances(attributes.getInt(Tag.NumberOfSeriesRelatedInstances, getNumberOfSeriesRelatedInstances()));
    }

    public long getPk() {
        return pk;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public boolean isPopulated() {
        return populated;
    }

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    public String getTimezoneOffsetFromUTC() {
        return timezoneOffsetFromUTC;
    }

    public void setTimezoneOffsetFromUTC(String timezoneOffsetFromUTC) {
        this.timezoneOffsetFromUTC = timezoneOffsetFromUTC;
    }

    public String getSeriesDescription() {
        return seriesDescription;
    }

    public void setSeriesDescription(String seriesDescription) {
        this.seriesDescription = seriesDescription;
    }

    public int getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(int seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public int getNumberOfSeriesRelatedInstances() {
        return numberOfSeriesRelatedInstances;
    }

    public void setNumberOfSeriesRelatedInstances(int numberOfSeriesRelatedInstances) {
        this.numberOfSeriesRelatedInstances = numberOfSeriesRelatedInstances;
    }

    public void addAlbumSeries(AlbumSeries albumSeries) { albumsSeries.add(albumSeries); }

    public void removeAlbumSeries(AlbumSeries albumSeries) { albumsSeries.remove(albumSeries); }

    public String getBodyPartExamined() { return bodyPartExamined; }

    public void setBodyPartExamined(String bodyPartExamined) { this.bodyPartExamined = bodyPartExamined; }

    public Set<Instances> getInstances() { return instances; }

    public void setInstances(Instances instance) { instances.add(instance); }
}

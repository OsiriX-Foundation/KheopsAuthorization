package online.kheops.auth_server.entity;

import online.kheops.auth_server.user.UsersPermission;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused"})

@NamedQueries({
        @NamedQuery(name = "Albums.findById",
        query = "SELECT a FROM Album a WHERE :albumId = a.id"),
        @NamedQuery(name = "Albums.findWithEnabledNewSeriesWebhooks",
        query = "SELECT distinct a FROM Album a JOIN a.albumSeries alS JOIN alS.series s JOIN s.study st JOIN a.webhooks w WHERE w.newSeries = true AND w.enabled = true AND st.studyInstanceUID = :studyInstanceUID"),

})

@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "pk")
    private long pk;

    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @Column(name = "id")
    private String id;

    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @Basic(optional = false)
    @Column(name = "last_event_time", updatable = true)
    private LocalDateTime lastEventTime;

    @Embedded private UserPermission userPermission;

    @OneToMany(mappedBy = "album")
    private Set<AlbumSeries> albumSeries = new HashSet<>();

    @OneToMany(mappedBy = "album")
    private Set<AlbumUser> albumUser = new HashSet<>();

    @OneToMany(mappedBy = "album")
    private Set<Webhook> webhooks = new HashSet<>();

    @OneToMany(mappedBy = "album")
    private Set<Event> events = new HashSet<>();

    @OneToOne(mappedBy = "inbox")
    private User inboxUser;

    @OneToMany(mappedBy = "album")
    private Set<Capability> capabilities = new HashSet<>();

    @OneToMany(mappedBy = "album")
    private Set<ReportProvider> reportProviders = new HashSet<>();

    @PrePersist
    public void onPrePersist() {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        createdTime = now;
        lastEventTime = now;
    }

    public Album() {}

    public Album(String name, String description, UsersPermission usersPermission, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.userPermission = new UserPermission(usersPermission);
    }

    public long getPk() { return pk; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName( String name ) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription( String description ) { this.description = description; }

    public LocalDateTime getCreatedTime() { return createdTime; }

    public LocalDateTime getLastEventTime() { return lastEventTime; }

    public UserPermission getUserPermission() { return userPermission; }

    public void setUserPermission(UserPermission userPermission) { this.userPermission = userPermission; }

    public void updateLastEventTime() { this.lastEventTime = LocalDateTime.now(ZoneOffset.UTC); }

    public boolean containsSeries(Series series, EntityManager em) {
        try {
            em.createNamedQuery("AlbumSeries.findByAlbumAndSeries", AlbumSeries.class)
                    .setParameter("series", series)
                    .setParameter("album", this)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    public void addSeries(AlbumSeries albumSeries) { this.albumSeries.add(albumSeries); }

    public void removeSeries(Series series, EntityManager em) {
        AlbumSeries localAlbumSeries = em.createNamedQuery("AlbumSeries.findByAlbumAndSeries", AlbumSeries.class)
                .setParameter("series", series)
                .setParameter("album", this)
                .getSingleResult();
        series.removeAlbumSeries(localAlbumSeries);
        this.albumSeries.remove(localAlbumSeries);
        em.remove(localAlbumSeries);
    }

    public Set<AlbumSeries> getAlbumSeries() { return albumSeries; }

    public Set<AlbumUser> getAlbumUser() { return albumUser; }

    public void addAlbumUser(AlbumUser albumUser) { this.albumUser.add(albumUser); }

    public User getInboxUser() { return inboxUser; }

    public void setInboxUser(User inboxUser) { this.inboxUser = inboxUser; }

    public Set<Event> getEvents() { return events; }

    public void setEvents(Set<Event> events) { this.events = events; }

    public void addEvents(Event event) { this.events.add(event); }

    public void addCapability(Capability capability) { this.capabilities.add(capability); }

    public Set<Capability> getCapabilities() { return capabilities; }

    public void addReportProvider(ReportProvider reportProvider) { this.reportProviders.add(reportProvider); }

    public Set<ReportProvider> getReportProviders() {return reportProviders; }

    public void addWebhook(Webhook webhook) { this.webhooks.add(webhook); }

    public Set<Webhook> getWebhooks() { return webhooks; }

    @Override
    public String toString() {
        return "[Album_id:"+id+"]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return pk == album.pk &&
                id.equals(album.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, id);
    }
}

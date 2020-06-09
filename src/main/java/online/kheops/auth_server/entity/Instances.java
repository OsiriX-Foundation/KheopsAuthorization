package online.kheops.auth_server.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "instances")
public class Instances {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "pk")
    private long pk;

    @Basic(optional = false)
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @Basic(optional = false)
    @Column(name = "instance_uid", updatable = false)
    private String instanceUID;

    @ManyToOne
    @JoinColumn(name = "series_fk", insertable = true, updatable=false)
    private Series series;

    @PrePersist
    public void onPrePersist() {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        createdTime = now;
    }

    public Instances(String instanceUID) { this.instanceUID = instanceUID; }

    public long getPk() { return pk; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public String getInstanceUID() { return instanceUID; }
    public Series getSeries() { return series; }
}

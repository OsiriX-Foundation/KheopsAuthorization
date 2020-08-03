package online.kheops.auth_server.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@NamedQueries({
        @NamedQuery(name = "Instances.findByInstancesUID",
                query = "SELECT i FROM Instances i WHERE i.instanceUID = :InstanceUID")
})

@Entity
@Table(name = "instances")
public class Instances {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "pk")
    private long pk;

    @Basic(optional = false)
    @Column(name = "creation_time", updatable = false)
    private LocalDateTime createdTime;

    @Basic(optional = false)
    @Column(name = "instance_uid", updatable = false, unique = true)
    private String instanceUID;

    @ManyToOne
    @JoinColumn(name = "series_fk", insertable = true, updatable=false)
    private Series series;

    @PrePersist
    public void onPrePersist() {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        createdTime = now;
    }

    public Instances() {}

    public Instances(String instanceUID, Series series) {
        this.series = series;
        this.instanceUID = instanceUID;
        series.addInstances(this);
    }

    public long getPk() { return pk; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public String getInstanceUID() { return instanceUID; }
    public Series getSeries() { return series; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instances instances = (Instances) o;
        return instanceUID.equals(instances.instanceUID);
    }

    @Override
    public int hashCode() {
        return instanceUID.hashCode();
    }

    @Override
    public String toString() { return instanceUID; }
}

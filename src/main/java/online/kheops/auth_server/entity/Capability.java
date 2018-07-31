package online.kheops.auth_server.entity;

import online.kheops.auth_server.Capabilities;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@SuppressWarnings("unused")
@Entity
@Table(name = "capabilities")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Capability.findCapabilityBySecret",
                query = "SELECT c from capabilities c where secret = :token")
})
public class Capability {
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

    @Column(name = "expiration_time")
    private LocalDateTime expiration;

    @Column(name = "revoked_time")
    private LocalDateTime revokedTime;

    @Basic(optional = false)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @Column(name = "secret", updatable = false)
    private String secret;

    @ManyToOne
    @JoinColumn(name = "user_fk", insertable=false, updatable=false)
    private User user;

    @PrePersist
    public void onPrePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        createdTime = now;
        updatedTime = now;
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedTime = LocalDateTime.now(ZoneOffset.UTC);
    }

    public Capability() {
        this.secret = Capabilities.newCapabilityToken();
    }

    // returns null if the token does not exist
    public static Capability findCapabilityByCapabilityToken(String token, EntityManager em) {
        try {
            Query capabilityTokenQuery = em.createNamedQuery("Capability.findCapabilityBySecret");
            capabilityTokenQuery.setParameter("token", token);
            return ((Capability) capabilityTokenQuery.getSingleResult());
        } catch (NoResultException ignored) {/*empty*/}
        return null;
    }


    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public boolean isRevoked() {
        return revokedTime != null;
    }

    public void setRevoked(boolean revoked) {
        if (!revoked && this.revokedTime != null) {
            throw new IllegalStateException("Can't unrevoke a revoked capability");
        } else if (revoked && this.revokedTime == null) {
            this.revokedTime = LocalDateTime.now(ZoneOffset.UTC);
        }
    }

    public LocalDateTime getRevokedTime() {
        return revokedTime;
    }

    public void setRevokedTime(LocalDateTime revokedTime) {
        if (this.revokedTime != null) {
            throw new IllegalStateException("Can't update the revokedTime on an already revoked capability");
        }
        this.revokedTime = revokedTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getSecret() {
        return secret;
    }
}

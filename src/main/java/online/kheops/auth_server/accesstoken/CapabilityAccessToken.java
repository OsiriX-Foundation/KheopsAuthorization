package online.kheops.auth_server.accesstoken;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.capability.Capabilities;
import online.kheops.auth_server.capability.CapabilityNotFoundException;
import online.kheops.auth_server.capability.CapabilityNotValidException;
import online.kheops.auth_server.capability.CapabilityToken;
import online.kheops.auth_server.entity.Capability;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Objects;
import java.util.Optional;

final class CapabilityAccessToken implements AccessToken {
    private final String sub;
    private static final Builder BUILDER = new Builder();
    private Capability capability;

    static final class Builder {
        CapabilityAccessToken build(String capabilityToken) throws BadAccessTokenException {
            if (!CapabilityToken.isValidFormat(capabilityToken)) {
                throw new BadAccessTokenException("Bad capability token format");
            }

            final EntityManager em = EntityManagerListener.createEntityManager();
            final EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                final Capability capability = Capabilities.getCapability(capabilityToken, em);

                capability.isValid();

                final String sub = capability.getUser().getKeycloakId();

                capability.setLastUsed();

                tx.commit();

                return new CapabilityAccessToken(capability, sub);
            } catch (CapabilityNotFoundException e) {
                throw new BadAccessTokenException("Unknown capability token");
            } catch (CapabilityNotValidException e) {
                throw new BadAccessTokenException(e.getMessage());
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
                em.close();
            }
        }
    }

    static Builder getBuilder() {
        return BUILDER;
    }

    private CapabilityAccessToken(Capability capability, String sub) {
        this.capability = Objects.requireNonNull(capability);
        this.sub = Objects.requireNonNull(sub);
    }

    @Override
    public String getSub() {
        return sub;
    }

    @Override
    public boolean hasCapabilityAccess() {
        return false;
    }

    @Override
    public Optional<Capability> getCapability() {
        return Optional.of(capability);
    }

    @Override
    public TokenType getTokenType() { return TokenType.CAPABILITY_TOKEN; }
}
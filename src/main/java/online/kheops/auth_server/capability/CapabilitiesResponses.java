package online.kheops.auth_server.capability;

import online.kheops.auth_server.entity.Capability;

import javax.xml.bind.annotation.XmlElement;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class CapabilitiesResponses {

    private CapabilitiesResponses() { throw new IllegalStateException("Utility class"); }

    public static class CapabilityResponse {
        @XmlElement(name = "id")
        long id;

        @XmlElement(name = "secret")
        String secret;
        @XmlElement(name = "title")
        String title;

        @XmlElement(name = "issued_at_time")
        String issuedAt;
        @XmlElement(name = "not_before_time")
        String notBeforeTime;
        @XmlElement(name = "expiration_time")
        String expirationTime;
        @XmlElement(name = "revoke_time")
        String revokeTime;

        @XmlElement(name = "revoked")
        boolean revoked;

        @XmlElement(name = "read_permission")
        Boolean readPermission;
        @XmlElement(name = "write_permission")
        Boolean writePermission;
        @XmlElement(name = "download_permission")
        Boolean downloadPermission;
        @XmlElement(name = "appropriate_permission")
        Boolean appropriatePermission;

        @XmlElement(name = "scope_type")
        String scopeType;
        @XmlElement(name = "scope_album")
        Long albumId;
        @XmlElement(name = "scope_series")
        String series;
        @XmlElement(name = "scope_study")
        String study;
    }

    public static CapabilityResponse capabilityToCapabilitiesResponses(Capability capability) {

        CapabilityResponse capabilityResponse = new CapabilityResponse();

        capabilityResponse.id = capability.getPk();

        capabilityResponse.secret = capability.getSecret();//TODO MUST BE REMOVE USE FOR DEBUG ONLY !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        capabilityResponse.title = capability.getTitle();
        capabilityResponse.expirationTime = ZonedDateTime.of(capability.getExpirationTime(), ZoneOffset.UTC).toString();
        capabilityResponse.revoked = capability.isRevoked();
        if (capability.isRevoked()) {
            capabilityResponse.revokeTime = ZonedDateTime.of(capability.getRevokedTime(), ZoneOffset.UTC).toString();
        }
        capabilityResponse.issuedAt = ZonedDateTime.of(capability.getIssuedAtTime(), ZoneOffset.UTC).toString();
        if (capability.isActive()) {
            capabilityResponse.notBeforeTime = ZonedDateTime.of(capability.getNotBeforeTime(), ZoneOffset.UTC).toString();
        }

        capabilityResponse = ScopeType.valueOf(capability.getScopeType().toUpperCase()).setCapabilityResponse(capabilityResponse, capability);

        return capabilityResponse;
    }
}

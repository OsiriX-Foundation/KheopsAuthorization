package online.kheops.auth_server.assertion;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import online.kheops.auth_server.entity.User;
import online.kheops.auth_server.principal.*;

import javax.servlet.ServletContext;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

public class ReportProviderAssertion implements Assertion {
    private final String sub;
    private final List<String> studyUIDs;
    private final String clientId;

    static class Builder {
        private static final String HOST_ROOT_PARAMETER = "online.kheops.root.uri";

        private final ServletContext servletContext;

        private Builder(ServletContext servletContext) {
            this.servletContext = Objects.requireNonNull(servletContext);
        }

        ReportProviderAssertion build(String assertionToken) throws BadAssertionException {
            Objects.requireNonNull(assertionToken);

            final Algorithm algorithm;
            try {
                algorithm = Algorithm.HMAC256(authorizationSecret());
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("authorizationSecret is not a valid HMAC256 secret", e);
            }
            final DecodedJWT jwt;
            try {
                jwt = JWT.require(algorithm)
                        .withIssuer(getIssuerHost())
                        .withAudience(getIssuerHost())
                        .withClaim("type", "report_generator")
                        .build()
                        .verify(assertionToken);
            } catch (JWTVerificationException e) {
                throw new BadAssertionException("Assertion verification failed.", e);
            }

            if (jwt.getSubject() == null) {
                throw new BadAssertionException("Missing sub claim in token.");
            }

            final Claim clientIdClaim = jwt.getClaim("client_id");
            if (clientIdClaim.isNull() || clientIdClaim.asString() == null) {
                throw new BadAssertionException("Missing client_id claim in token");
            }

            final Claim studyUIDsClaim = jwt.getClaim("study_uids");
            try {
                if (studyUIDsClaim.isNull() || studyUIDsClaim.asList(String.class) == null) {
                    throw new BadAssertionException("Missing study_uids claim in token");
                }
                return new ReportProviderAssertion(jwt.getSubject(), studyUIDsClaim.asList(String.class), clientIdClaim.asString());
            } catch (JWTDecodeException e) {
                throw new BadAssertionException("unable to decode study_uids");
            }
        }

        private String authorizationSecret() {
            return servletContext.getInitParameter("online.kheops.auth.hmacsecret");
        }

        private String getIssuerHost() {
            return servletContext.getInitParameter(HOST_ROOT_PARAMETER);
        }

    }

    static ReportProviderAssertion.Builder getBuilder(ServletContext servletContext) {
        return new ReportProviderAssertion.Builder(servletContext);
    }

    private ReportProviderAssertion(String sub, List<String> studyUIDs, String ClientId) {
        this.sub = Objects.requireNonNull(sub);
        this.studyUIDs = Objects.requireNonNull(studyUIDs);
        this.clientId = Objects.requireNonNull(ClientId);
    }

    @Override
    public String getSub() {
        return sub;
    }

    @Override
    public boolean hasCapabilityAccess() {
        return true;
    }

    @Override
    public TokenType getTokenType() { return TokenType.REPORT_PROVIDER_TOKEN; }

    @Override
    public KheopsPrincipalInterface newPrincipal(ServletContext servletContext, User user) {

        return new ReportProviderPrincipal(user, studyUIDs, clientId);
    }
}
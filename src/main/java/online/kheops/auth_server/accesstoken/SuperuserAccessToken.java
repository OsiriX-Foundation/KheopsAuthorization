package online.kheops.auth_server.accesstoken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.ServletContext;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

final class SuperuserAccessToken implements AccessToken {
    private final String sub;

    static class Builder implements AccessTokenBuilder {
        private final ServletContext servletContext;

        Builder(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public SuperuserAccessToken build(String assertionToken) throws AccessTokenVerificationException {
            Objects.requireNonNull(assertionToken);

            final Algorithm algorithm;
            try {
                algorithm = Algorithm.HMAC256(getSuperuserSecret());
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("superuserSecret is not a valid HMAC256 secret", e);
            }
            final DecodedJWT jwt;
            try {
                jwt = JWT.require(algorithm)
                        .withIssuer("authorization.kheops.online")
                        .build()
                        .verify(assertionToken);
            } catch (JWTVerificationException e) {
                throw new AccessTokenVerificationException("AccessToken verification failed.", e);
            }

            if (jwt.getSubject() == null) {
                throw new AccessTokenVerificationException("Missing sub claim in token.");
            }

            return new SuperuserAccessToken(jwt.getSubject());
        }

        private String getSuperuserSecret() {
            return servletContext.getInitParameter("online.kheops.superuser.hmacsecret");
        }
    }

    private SuperuserAccessToken(String sub) {
        this.sub = Objects.requireNonNull(sub);
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
    public TokenType getTokenType() { return TokenType.SUPER_USER_TOKEN; }
}
package online.kheops.auth_server.accesstoken;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import online.kheops.auth_server.entity.User;
import online.kheops.auth_server.keycloak.KeycloakContextListener;
import online.kheops.auth_server.principal.KheopsPrincipal;
import online.kheops.auth_server.principal.UserPrincipal;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Optional;

final class KeycloakAccessToken implements AccessToken {

    private static final Client CLIENT = ClientBuilder.newClient();

    private final String subject;
    private final String actingParty;
    private final String token;

    private static final String JWKS_CACHE_ALIAS = "jwksCache";
    private static final String PUBLIC_KEY_CACHE_ALIAS = "publicKeyCache";
    private static CacheManager jwksCacheManager = getJwksCacheManager();
    private static Cache<String, URL> jwksURLCache = jwksCacheManager.getCache(JWKS_CACHE_ALIAS, String.class, URL.class);
    private static Cache<String, RSAPublicKey> publicKeyCache = jwksCacheManager.getCache(PUBLIC_KEY_CACHE_ALIAS, String.class, RSAPublicKey.class);

    private static CacheManager getJwksCacheManager() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(JWKS_CACHE_ALIAS, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, URL.class, ResourcePoolsBuilder.heap(10))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10))))
                .withCache(PUBLIC_KEY_CACHE_ALIAS, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, RSAPublicKey.class, ResourcePoolsBuilder.heap(10))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10))))
                .build();
        cacheManager.init();
        return cacheManager;
    }

    private static class ConfigurationEntity {
        @XmlElement(name="jwks_uri")
        String jwksURI;
    }

    static final class Builder implements AccessTokenBuilder {
        private final String configurationUrl = KeycloakContextListener.getKeycloakOIDCConfigurationString();

        private final ServletContext servletContext;

        Builder(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public KeycloakAccessToken build(String assertionToken) throws AccessTokenVerificationException {
            URL jwksURL = getJwksURL(configurationUrl);

            final RSAKeyProvider keyProvider = new RSAKeyProvider() {
                @Override
                public RSAPublicKey getPublicKeyById(String kid) {
                    return getRSAPublicKey(jwksURL, kid);
                }
                // implemented to get rid of warnings
                @Override public RSAPrivateKey getPrivateKey() {return null;}
                @Override public String getPrivateKeyId() {return null;}
            };

            final DecodedJWT jwt;
            try {
                jwt = JWT.require(Algorithm.RSA256(keyProvider))
                        .acceptLeeway(120)
                        //.withIssuer(getIssuer())
                        .acceptLeeway(60)
                        .build()
                        .verify(assertionToken);
            } catch (JWTVerificationException e) {
                throw new AccessTokenVerificationException("Verification of the token failed, configuration URL:" + configurationUrl, e);
            }

            if (jwt.getSubject() == null) {
                throw new AccessTokenVerificationException("No subject present in the token, configuration URL:" + configurationUrl);
            }

            try {
                if (!AccessTokenUtils.StringContainsScope(jwt.getClaim("scope").asString(), "kheops")) {
                    throw new AccessTokenVerificationException("Token not valid for kheops");
                }
            } catch (NullPointerException | IllegalArgumentException e) {
                throw new AccessTokenVerificationException("Missing scope claim");
            }

            final String actingParty;
            Claim actClaim = jwt.getClaim("act");
            if (!actClaim.isNull()) {
                try {
                    actingParty = (String) actClaim.asMap().get("sub");
                } catch (ClassCastException | JWTDecodeException e) {
                    throw new AccessTokenVerificationException("Unable to read the acting party", e);
                }
                if (actingParty == null) {
                    throw new AccessTokenVerificationException("Has acting party, but without a subject");
                }
            } else {
                actingParty = null;
            }

            return new KeycloakAccessToken(jwt.getSubject(), actingParty, assertionToken);
        }

        private static synchronized URL getJwksURL(String configurationUrl) {
            URL jwksURL = jwksURLCache.get(configurationUrl);

            if (jwksURL == null) {
                final ConfigurationEntity configurationEntity;
                try {
                    configurationEntity = CLIENT.target(configurationUrl).request(MediaType.APPLICATION_JSON).get(ConfigurationEntity.class);
                } catch (RuntimeException e) {
                    throw new DownloadKeyException("Unable to download the jwks_uri, configuration URL:" + configurationUrl, e);
                }

                try {
                    jwksURL = new URL(configurationEntity.jwksURI);
                } catch (MalformedURLException e) {
                    throw new DownloadKeyException("jwks_uri is not a valid URI, configuration URL:\" + configurationUrl", e);
                }
                jwksURLCache.put(configurationUrl, jwksURL);
            }
            return jwksURL;
        }

        private static synchronized RSAPublicKey getRSAPublicKey(URL jwksURL, String keyId) {
            RSAPublicKey publicKey = publicKeyCache.get(jwksURL.toString() + "_" + keyId);

            if (publicKey == null) {
                try {
                    publicKey = (RSAPublicKey) new UrlJwkProvider(jwksURL).get(keyId).getPublicKey();
                } catch (JwkException e) {
                    throw new DownloadKeyException("Can't download the public RSA key, jwks URL:" + jwksURL, e);
                }
                publicKeyCache.put(jwksURL.toString() + "_" + keyId, publicKey);
            }

            return publicKey;
        }

        private String getIssuer() {
            return servletContext.getInitParameter("online.kheops.keycloak.uri") + "/auth/realms/" +
                    servletContext.getInitParameter("online.kheops.keycloak.realms");
        }
    }

    private KeycloakAccessToken(String subject, String actingParty, String token) {
        this.subject = subject;
        this.actingParty = actingParty;
        this.token = token;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.KEYCLOAK_TOKEN;
    }

    @Override
    public Optional<String> getScope() {
        return Optional.of("user read write downloadbutton send");
    }

    @Override
    public Optional<String> getActingParty() {
        return Optional.ofNullable(actingParty);
    }

    @Override
    public KheopsPrincipal newPrincipal(ServletContext servletContext, User user) {
        return new UserPrincipal(user, actingParty, token);
    }
}

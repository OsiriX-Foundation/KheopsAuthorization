package online.kheops.auth_server.accesstoken;

import javax.servlet.ServletContext;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AccessTokenVerifier {

    private static final List<Class<?>> accessTokenBuilderClasses =
            Arrays.asList(ReportProviderAccessToken.Builder.class,
                          CapabilityAccessToken.CapabilityAccessTokenBuilder.class,
                          ViewerAccessTokenBuilder.class,
                          OidcAccessToken.Builder.class);

    private static final Class<PepAccessToken.Builder> pepAccessTokenClass = PepAccessToken.Builder.class;

    private AccessTokenVerifier() {}

    public static AccessToken authenticateAccessToken(ServletContext servletContext, String accessToken)
        throws AccessTokenVerificationException {
        return authenticateAccessTokens(accessTokenBuilderClasses, servletContext, accessToken, true);
    }

    public static AccessToken authenticateAccessToken(ServletContext servletContext, String accessToken, boolean verifySignature)
        throws AccessTokenVerificationException {
        return authenticateAccessTokens(accessTokenBuilderClasses, servletContext, accessToken, verifySignature);
    }

    public static AccessToken authenticateIntrospectableAccessToken(ServletContext servletContext, String accessToken)
            throws AccessTokenVerificationException {

        List<Class<?>> introspectableAccessTokenBuilderClasses = new ArrayList<>(accessTokenBuilderClasses);
        introspectableAccessTokenBuilderClasses.add(pepAccessTokenClass);

        return authenticateAccessTokens(introspectableAccessTokenBuilderClasses, servletContext, accessToken, true);
    }

    private static AccessToken authenticateAccessTokens(List<Class<?>> accessTokenBuilderClasses, ServletContext servletContext, String accessToken, boolean verifySignature)
            throws AccessTokenVerificationException {

        List<AccessTokenVerificationException> exceptionList = new ArrayList<>(6);

        for (Class<?> builderClass: accessTokenBuilderClasses) {
            final AccessTokenBuilder accessTokenBuilder;
            Constructor<?> servletContextConstructor;
            try {
                servletContextConstructor = builderClass.getDeclaredConstructor((ServletContext.class));
            } catch (NoSuchMethodException e) {
                servletContextConstructor = null;
            }

            try {
                if (servletContextConstructor != null) {
                    accessTokenBuilder = (AccessTokenBuilder) servletContextConstructor.newInstance(servletContext);
                } else {
                    accessTokenBuilder = (AccessTokenBuilder) builderClass.getDeclaredConstructor().newInstance();
                }
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new IllegalStateException(e);
            }

            try {
                return accessTokenBuilder.build(accessToken, verifySignature);
            } catch (AccessTokenVerificationException e) {
                exceptionList.add(e);
            }
        }

        final String message = "Unable to verify access token because: " +
                exceptionList.stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.joining(", "));
        final AccessTokenVerificationException accessTokenVerificationException = new AccessTokenVerificationException(message);
        exceptionList.forEach(accessTokenVerificationException::addSuppressed);

        throw accessTokenVerificationException;
    }
}

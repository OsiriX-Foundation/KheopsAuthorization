package online.kheops.auth_server.keycloak;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class KeycloakContextListener implements ServletContextListener {

    private static ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        servletContext = null;
    }

    private static String getKeycloakUri(){ return servletContext.getInitParameter("online.kheops.keycloak.uri"); }

    public static String getKeycloakUser(){ return servletContext.getInitParameter("online.kheops.keycloak.user"); }

    public static String getKeycloakPassword(){ return servletContext.getInitParameter("online.kheops.keycloak.password"); }

    public static String getKeycloakClientId(){ return servletContext.getInitParameter("online.kheops.keycloak.clientid"); }

    public static String getKeycloakClientSecret(){ return servletContext.getInitParameter("online.kheops.keycloak.clientsecret"); }

    private static String getKeycloakRealms(){ return servletContext.getInitParameter("online.kheops.keycloak.realms"); }

    public static URI getKeycloakWellKnownURI(){ return UriBuilder.fromUri(getKeycloakUri() + "/auth/realms/" + getKeycloakRealms() + "/.well-known/openid-configuration").build(); }

    public static String getKeycloakWellKnownString(){ return getKeycloakUri() + "/auth/realms/" + getKeycloakRealms() + "/.well-known/openid-configuration"; }

    public static URI getKeycloakAdminURI(){ return UriBuilder.fromUri(getKeycloakUri() + "/auth/admin/realms/" + getKeycloakRealms()).build(); }

    public static String getKeycloakIssuer() { return getKeycloakUri() + "/auth/realms/" + getKeycloakRealms();}

}
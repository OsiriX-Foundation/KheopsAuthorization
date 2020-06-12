package online.kheops.auth_server.ContextListener;

import online.kheops.auth_server.PepAccessTokenBuilder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class PEPAccessTokenSecretContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        PepAccessTokenBuilder.setSecret(sce.getServletContext().getInitParameter("online.kheops.auth.hmacsecret"));
    }
}

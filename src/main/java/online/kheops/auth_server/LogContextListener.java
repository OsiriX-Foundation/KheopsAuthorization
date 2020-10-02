package online.kheops.auth_server;

import online.kheops.auth_server.util.KheopsLevel;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.bridge.SLF4JBridgeHandler;

//import org.slf4j.LoggerFactory;

public class LogContextListener implements ServletContextListener {

    private static final java.util.logging.Logger LOG = Logger.getLogger(LogContextListener.class.getName());

    //private static final org.slf4j.Logger LOGGER = (org.slf4j.Logger) LoggerFactory.getLogger(LogContextListener.class);
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LogContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {


        java.util.logging.LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();

        LOG.log(Level.SEVERE, "TEST TEST TEST");
        LOG.log(Level.WARNING, "TEST TEST TEST");
        LOG.log(Level.INFO, "TEST TEST TEST");
        LOG.log(Level.CONFIG, "TEST TEST TEST");
        LOG.log(Level.FINE, "TEST TEST TEST");
        LOG.log(Level.FINER, "TEST TEST TEST");
        LOG.log(Level.FINEST, "TEST TEST TEST");
        LOG.log(Level.ALL, "TEST TEST TEST");
        LOG.log(KheopsLevel.KHEOPS, "TEST TEST TEST");
        /*LOGGER.warn("TATA TATAT");
        LOGGER.warn("TATA TATAT");
        LOGGER.debug("TATA TATAT");
        LOGGER.info("TATA TATAT");
        LOGGER.error("TATA TATAT");*/
        logger.info("TUTU");
        logger.error("TUTU");
        logger.debug("TUTU");



        //debug
        //System.getProperties().setProperty("org.jooq.no-logo", "true");
        try (InputStream propFile = getClass().getClassLoader().getResourceAsStream("prop.properties")) {
            Properties p = new Properties(System.getProperties());
            p.load(propFile);
            System.setProperties(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
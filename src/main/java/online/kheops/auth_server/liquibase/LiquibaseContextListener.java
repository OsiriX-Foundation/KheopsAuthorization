package online.kheops.auth_server.liquibase;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import online.kheops.auth_server.EntityManagerListener;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;

public class LiquibaseContextListener implements ServletContextListener {

    private static ServletContext servletContext;
    private static final String CHANGE_LOG_FILE = "kheopsChangeLog-master.xml";
    private static final java.util.logging.Logger LOG = Logger.getLogger(LiquibaseContextListener.class.getName());
    private static final String DB_VERSION = "v3.1";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        servletContext = sce.getServletContext();

        LOG.log(Level.INFO, "Start initializing the DB with Liquibase. Database version : " + DB_VERSION);

        final Properties properties = new Properties();
        properties.setProperty(Environment.USER, getJDBCUser());
        properties.setProperty(Environment.PASS, getJDBCPassword());
        properties.setProperty(Environment.URL, getJDBCUrl());
        properties.setProperty(Environment.DRIVER, "org.postgresql.Driver");
        properties.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty(Environment.SHOW_SQL, "false");

        final Configuration cfg = new Configuration();
        cfg.setProperties(properties);

            try (Connection con = EntityManagerListener.getConnection()){
                JdbcConnection jdbcCon = new JdbcConnection(con);

                // Initialize Liquibase and run the update
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcCon);
                Liquibase liquibase = new Liquibase(CHANGE_LOG_FILE, new ClassLoaderResourceAccessor(), database);
                final String version = DB_VERSION;

                if (liquibase.tagExists(version)) {
                    liquibase.rollback(version, "");
                    liquibase.update(version, "");
                } else {
                    liquibase.update(version, "");
                }
                liquibase.validate();
                jdbcCon.close();
//                con.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Unable to use liquibase", e);
                exit(1);
            }
        LOG.log(Level.INFO, "Liquibase : database version : " + DB_VERSION + " SUCCESSFUL");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        servletContext = null;
    }

    private static void verifyState() {
        if (servletContext == null) {
            throw new IllegalStateException("Getting parameters before the context is initialized");
        }
    }

    private static String getJDBCUrl() {
        verifyState();
        return servletContext.getInitParameter("online.kheops.jdbc.url");
    }
    private static String getJDBCPassword() {
        verifyState();
        return servletContext.getInitParameter("online.kheops.jdbc.password");
    }
    private static String getJDBCUser() {
        verifyState();
        return servletContext.getInitParameter("online.kheops.jdbc.user");
    }

}

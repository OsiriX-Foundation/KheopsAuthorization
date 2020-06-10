package online.kheops.auth_server.instances;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.PepAccessTokenBuilder;
import online.kheops.auth_server.PepAccessTokenBuilderImpl;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.token.TokenProvenance;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.System.exit;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static online.kheops.auth_server.util.Consts.INCLUDE_FIELD;

public class InstancesContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(InstancesContextListener.class.getName());

    private static class EmptyTokenProvenance implements TokenProvenance {
        @Override public Optional<String> getAuthorizedParty() { return Optional.empty(); }
        @Override public Optional<String> getActingParty() { return Optional.empty(); }
        @Override public Optional<String> getCapabilityTokenId() { return Optional.empty(); }
    }

    private static final Client CLIENT = newClient();
    private static UriBuilder uriBuilder = null;

    public static void setDicomWebURI(URI dicomWebURI) {
        uriBuilder = UriBuilder.fromUri(Objects.requireNonNull(dicomWebURI)).path("studies/{StudyInstanceUID}/series/{SeriesInstanceUID}/instances");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        //Parcourir toutes les séries
            //obtenir un PEPtoken
            //Demander la liste des instances pour chaque séries
                //pour chaque instances Ajouter une ligne dans la DB


        //Vérification que chaque séries à au moins une instances


        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            List<Series> seriesList;
            TypedQuery<Series> q = em.createQuery("SELECT s FROM Series s ORDER BY s.pk", Series.class);
            seriesList = q.getResultList();

            for (Series series: seriesList) {
                LOG.info(series.getSeriesInstanceUID() + "----" + series.getStudy().getStudyInstanceUID());
                String pepToken = PepAccessTokenBuilder.newBuilder(new EmptyTokenProvenance())
                        .withSeriesUID(series.getSeriesInstanceUID())
                        .withStudyUID(series.getStudy().getStudyInstanceUID())
                        .withSubject(this.getClass().getName()).build();

                LOG.info("PEPtoken: " + pepToken);

                final URI instancesUri = uriBuilder.build(series.getSeriesInstanceUID(), series.getStudy().getStudyInstanceUID());
                List<Attributes> toto = CLIENT.target(instancesUri).request().accept("application/dicom+json").header("Authorization", "Bearer "+pepToken).get(new GenericType<List<Attributes>>() {});

                //PEP token
                //ask PACS
                //for (instance:) {}
                    //new instances
            }

            tx.commit();

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }
}

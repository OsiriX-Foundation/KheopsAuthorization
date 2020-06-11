package online.kheops.auth_server.instances;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.PepAccessTokenBuilder;
import online.kheops.auth_server.entity.Instances;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.marshaller.JSONAttributesListMarshaller;
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

import static javax.ws.rs.client.ClientBuilder.newClient;

public class InstancesContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(InstancesContextListener.class.getName());
    private static final int LIMIT = 100;

    private static class EmptyTokenProvenance implements TokenProvenance {
        @Override public Optional<String> getAuthorizedParty() { return Optional.empty(); }
        @Override public Optional<String> getActingParty() { return Optional.empty(); }
        @Override public Optional<String> getCapabilityTokenId() { return Optional.empty(); }
    }

    private static final Client CLIENT = newClient().register(JSONAttributesListMarshaller.class);
    private static UriBuilder uriBuilder = null;

    public static void setDicomWebURI(URI dicomWebURI) {
        uriBuilder = UriBuilder.fromUri(Objects.requireNonNull(dicomWebURI)).path("studies/{StudyInstanceUID}/series/{SeriesInstanceUID}/instances");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        Integer numberOfSeriesOk = 0;
        Integer numberOfInstancesCreated = 0;
        Integer numberOfSeriesFail = 0;
        Long totalNumberOfSeries;

        try {
            tx.begin();

            final long countInstances = em.createQuery("SELECT COUNT(i) FROM Instances i", Long.class).getSingleResult();
            if (countInstances != 0) {
                LOG.info("InstancesContextListener is already filled");
                return;
            }

            totalNumberOfSeries = em.createQuery("SELECT COUNT(s) FROM Series s", Long.class).getSingleResult();

            List<Series> seriesList;
            int iteration = 0;
            do {
                TypedQuery<Series> q = em.createQuery("SELECT s FROM Series s ORDER BY s.pk", Series.class).setMaxResults(LIMIT).setMaxResults(LIMIT * iteration);
                seriesList = q.getResultList();

                for (Series series : seriesList) {
                    String pepToken = PepAccessTokenBuilder.newBuilder(new EmptyTokenProvenance())
                            .withSeriesUID(series.getSeriesInstanceUID())
                            .withStudyUID(series.getStudy().getStudyInstanceUID())
                            .withSubject(this.getClass().getName()).build();

                    final URI instancesUri = uriBuilder.build(series.getStudy().getStudyInstanceUID(), series.getSeriesInstanceUID());
                    List<Attributes> instancesList = CLIENT.target(instancesUri).request().accept("application/dicom+json").header("Authorization", "Bearer " + pepToken).get(new GenericType<List<Attributes>>() {
                    });
                    if (instancesList == null || instancesList.isEmpty()) {
                        LOG.info("Series : " + series.getSeriesInstanceUID() + "contain 0 instances");
                        numberOfSeriesFail++;
                    } else {
                        Instances instances;
                        for (Attributes attributes : instancesList) {
                            instances = new Instances(attributes.getString(Tag.SOPInstanceUID), series);
                            em.persist(instances);
                            numberOfInstancesCreated++;
                        }
                        numberOfSeriesOk++;
                        em.flush();
                    }
                }
                iteration++;
            } while (seriesList.isEmpty());

            tx.commit();

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
        LOG.info("End of InstancesContextListener :" +
                " totalNumberOfSeries:" + totalNumberOfSeries +
                " numberOfSeriesOk:" + numberOfSeriesOk  +
                " numberOfSeriesFail:" + numberOfSeriesFail +
                " numberOfInstancesCreated:" + numberOfInstancesCreated);
    }
}

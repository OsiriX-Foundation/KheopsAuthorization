package online.kheops.auth_server.report_provider;


import online.kheops.auth_server.entity.ReportProvider;
import javax.persistence.EntityManager;
import java.util.List;

import online.kheops.auth_server.util.ErrorResponse;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import static online.kheops.auth_server.util.ErrorResponse.Message.REPORT_PROVIDER_NOT_FOUND;

public class ReportProviderQueries {

    private ReportProviderQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static ReportProvider getReportProviderWithClientId(String clientId, EntityManager em) {

        return em.createNamedQuery("ReportProvider.findByClientId", ReportProvider.class)
                .setParameter("clientId", clientId)
                .getSingleResult();
    }

    public static List<ReportProvider> getReportProvidersWithAlbumId(String albumId, Integer limit, Integer offset, EntityManager em) {
        return em.createNamedQuery("ReportProvider.findAllByAlbumId", ReportProvider.class)
                .setParameter("albumId", albumId)
                .setMaxResults(limit)
                .setFirstResult(offset)
                .getResultList();
    }

    public static long countReportProviderWithAlbumId(String albumId, EntityManager em) {
        return em.createNamedQuery("ReportProvider.countAllByAlbumId", Long.class)
                .setParameter("albumId", albumId)
                .getSingleResult();
    }

}

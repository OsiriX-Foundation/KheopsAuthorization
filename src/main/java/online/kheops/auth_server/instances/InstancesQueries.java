package online.kheops.auth_server.instances;

import online.kheops.auth_server.entity.Instances;
import online.kheops.auth_server.util.ErrorResponse;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static online.kheops.auth_server.util.ErrorResponse.Message.INSTANCES_NOT_FOUND;

public class InstancesQueries {

    public static Instances findInstancesByInstanceUID(String instanceUID, EntityManager em)
            throws InstancesNotFoundException {

        try {
            TypedQuery<Instances> query = em.createNamedQuery("Instances.findByInstancesUID", Instances.class);
            query.setParameter("InstanceUID", instanceUID);
            return query.getSingleResult();
        } catch (NoResultException e) {
            final ErrorResponse errorResponse = new ErrorResponse.ErrorResponseBuilder()
                    .message(INSTANCES_NOT_FOUND)
                    .detail("The instances does not exist")
                    .build();
            throw new InstancesNotFoundException(errorResponse);
        }
    }
}

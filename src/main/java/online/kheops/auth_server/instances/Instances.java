package online.kheops.auth_server.instances;

import javax.persistence.EntityManager;

import static online.kheops.auth_server.instances.InstancesQueries.findInstancesByInstanceUID;

public class Instances {
    public static boolean instancesExist(String instanceUID, EntityManager em) {
        try {
            getInstances(instanceUID, em);
        } catch (InstancesNotFoundException e) {
            return false;
        }
        return true;
    }

    public static online.kheops.auth_server.entity.Instances getInstances(String instanceUID, EntityManager em)
        throws InstancesNotFoundException {
        return findInstancesByInstanceUID(instanceUID, em);
    }
}

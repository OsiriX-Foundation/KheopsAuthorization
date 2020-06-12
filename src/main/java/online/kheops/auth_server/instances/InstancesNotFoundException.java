package online.kheops.auth_server.instances;

import online.kheops.auth_server.util.ErrorResponse;
import online.kheops.auth_server.util.KheopsException;

public class InstancesNotFoundException extends Exception implements KheopsException {

    private final ErrorResponse errorResponse;

    public InstancesNotFoundException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() { return errorResponse; }
}

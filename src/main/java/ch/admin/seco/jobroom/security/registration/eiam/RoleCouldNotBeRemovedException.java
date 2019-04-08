package ch.admin.seco.jobroom.security.registration.eiam;

import ch.adnovum.nevisidm.ws.services.v1.BusinessException;

class RoleCouldNotBeRemovedException extends EiamClientRuntimeException {

    RoleCouldNotBeRemovedException(String userExtId, String roleName, BusinessException businessException) {
        super("The roleName " + roleName + " could not be removed to the user with userExtId " + userExtId
            + " with the error message: " + businessException.getMessage()
            + " and the reason: " + businessException.getReason());
    }
}

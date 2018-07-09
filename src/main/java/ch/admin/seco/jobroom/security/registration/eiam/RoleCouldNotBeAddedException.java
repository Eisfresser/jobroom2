package ch.admin.seco.jobroom.security.registration.eiam;

import ch.adnovum.nevisidm.ws.services.v1.BusinessException;

class RoleCouldNotBeAddedException extends EiamClientRuntimeException {

    RoleCouldNotBeAddedException(String userExtId, String roleName, BusinessException businessException) {
        super("The role " + roleName + " could not be added to the user with extId " + userExtId
            + " with the error message: " + businessException.getMessage()
            + " and the reason: " + businessException.getReason());
    }
}

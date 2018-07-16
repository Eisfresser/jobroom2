package ch.admin.seco.jobroom.security.registration.eiam;


import ch.adnovum.nevisidm.ws.services.v1.User;

public interface EiamClient {

    String APPLICATION_NAME = "ALV-jobroom";

    User getUserByExtId(String userExtId) throws UserNotFoundException;

    User getUserByEmail(String email) throws UserNotFoundException;

    void addRoleToUser(String userExtId, String profileExtId, String roleName);

    void removeRoleFromUser(String userExtId, String profileExtId, String role);
}

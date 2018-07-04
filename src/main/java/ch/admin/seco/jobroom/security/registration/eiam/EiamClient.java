package ch.admin.seco.jobroom.security.registration.eiam;


import ch.adnovum.nevisidm.ws.services.v1.User;

public interface EiamClient {

    User getUserByExtId(String userExtId) throws UserNotFoundException, ExtIdNotUniqueException;

    void addRoleToUser(String userExtId, String profileExtId, String roleName) throws RoleCouldNotBeAddedException;

    void removeRoleFromUser(String userExtId, String profileExtId, String role) throws RoleCouldNotBeRemovedException;
}

package ch.admin.seco.jobroom.security.registration.eiam;

import java.util.List;

import ch.adnovum.nevisidm.ws.services.v1.AddAuthorizationToProfile;
import ch.adnovum.nevisidm.ws.services.v1.AddAuthorizationToProfileRequest;
import ch.adnovum.nevisidm.ws.services.v1.Authorization;
import ch.adnovum.nevisidm.ws.services.v1.BusinessException;
import ch.adnovum.nevisidm.ws.services.v1.GetUsersByExtId;
import ch.adnovum.nevisidm.ws.services.v1.GetUsersByExtIdResponse;
import ch.adnovum.nevisidm.ws.services.v1.ObjectFactory;
import ch.adnovum.nevisidm.ws.services.v1.Profile;
import ch.adnovum.nevisidm.ws.services.v1.QueryUsers;
import ch.adnovum.nevisidm.ws.services.v1.QueryUsersResponse;
import ch.adnovum.nevisidm.ws.services.v1.RemoveAuthorizationFromProfile;
import ch.adnovum.nevisidm.ws.services.v1.RemoveAuthorizationFromProfileRequest;
import ch.adnovum.nevisidm.ws.services.v1.Role;
import ch.adnovum.nevisidm.ws.services.v1.User;
import ch.adnovum.nevisidm.ws.services.v1.UserGetByExtId;
import ch.adnovum.nevisidm.ws.services.v1.UserQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;

import ch.admin.seco.jobroom.security.registration.eiam.EiamClientRuntimeException.Identification;

class DefaultEiamClient implements EiamClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEiamClient.class);

    private static final int DEFAULT_DETAIL_LEVEL = 1;

    private final WebServiceTemplate webServiceTemplate;

    private final String clientName;

    DefaultEiamClient(WebServiceTemplate webServiceTemplate, String clientName) {
        Assert.notNull(webServiceTemplate, "a web service template is needed");
        Assert.notNull(clientName, "a clientName must be provided");
        this.webServiceTemplate = webServiceTemplate;
        this.clientName = clientName;
    }

    @Override
    public User getUserByExtId(String userExtId) throws UserNotFoundException {
        ObjectFactory factory = new ObjectFactory();
        GetUsersByExtId getUsersByExtId = factory.createGetUsersByExtId();
        UserGetByExtId usersGetByExtId = factory.createUserGetByExtId();
        usersGetByExtId.setClientName(clientName);
        usersGetByExtId.setDetail(DEFAULT_DETAIL_LEVEL);
        usersGetByExtId.getExtIds().add(userExtId);
        getUsersByExtId.setGet(usersGetByExtId);

        LOGGER.debug("Eiam client sending [extId={}]", userExtId);

        GetUsersByExtIdResponse result = (GetUsersByExtIdResponse) webServiceTemplate.marshalSendAndReceive(getUsersByExtId);
        List<User> users = result.getReturns();

        LOGGER.debug("Eiam client received user = {}", printUsers(users));

        if (users.isEmpty()) {
            throw new UserNotFoundException(Identification.EXT_ID, userExtId);
        } else if (users.size() > 1) {
            throw new MultipleEiamUsersFound(Identification.EXT_ID, userExtId);
        }
        return users.get(0);
    }

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        ObjectFactory factory = new ObjectFactory();
        UserQuery userQuery = factory.createUserQuery();
        User user = factory.createUser();
        user.setEmail(email);
        userQuery.setUser(user);
        userQuery.setDetail(DEFAULT_DETAIL_LEVEL);
        QueryUsers queryUsers = factory.createQueryUsers();
        QueryUsersResponse result = (QueryUsersResponse) webServiceTemplate.marshalSendAndReceive(queryUsers);
        List<User> users = result.getReturns();
        LOGGER.debug("Eiam client received user = {}", printUsers(users));
        if (users.isEmpty()) {
            throw new UserNotFoundException(Identification.EMAIL, email);
        } else if (users.size() > 1) {
            throw new MultipleEiamUsersFound(Identification.EMAIL, email);
        }
        return users.get(0);
    }

    @Override
    public void addRoleToUser(String userExtId, String profileExtId, String roleName) {
        ObjectFactory factory = new ObjectFactory();

        AddAuthorizationToProfileRequest addAuthorizationToProfileRequest = factory.createAddAuthorizationToProfileRequest();
        addAuthorizationToProfileRequest.setClientName(clientName);
        addAuthorizationToProfileRequest.setDetail(DEFAULT_DETAIL_LEVEL);
        addAuthorizationToProfileRequest.setAuthorization(prepareAuthorization(roleName, factory));
        addAuthorizationToProfileRequest.setProfile(prepareProfile(userExtId, profileExtId, factory));

        AddAuthorizationToProfile addAuthorizationToProfile = factory.createAddAuthorizationToProfile();
        addAuthorizationToProfile.setRequest(addAuthorizationToProfileRequest);
        Object result = webServiceTemplate.marshalSendAndReceive(addAuthorizationToProfile);
        if (result instanceof BusinessException) {
            throw new RoleCouldNotBeAddedException(userExtId, roleName, (BusinessException) result);
        }
    }

    @Override
    public void removeRoleFromUser(String userExtId, String profileExtId, String roleName) {
        ObjectFactory factory = new ObjectFactory();

        RemoveAuthorizationFromProfileRequest removeAuthorizationFromProfileRequest = factory.createRemoveAuthorizationFromProfileRequest();
        removeAuthorizationFromProfileRequest.setClientName(clientName);
        removeAuthorizationFromProfileRequest.setDetail(DEFAULT_DETAIL_LEVEL);
        removeAuthorizationFromProfileRequest.setAuthorization(prepareAuthorization(roleName, factory));
        removeAuthorizationFromProfileRequest.setProfile(prepareProfile(userExtId, profileExtId, factory));

        RemoveAuthorizationFromProfile removeAuthorizationFromProfile = factory.createRemoveAuthorizationFromProfile();
        removeAuthorizationFromProfile.setRequest(removeAuthorizationFromProfileRequest);
        Object result = webServiceTemplate.marshalSendAndReceive(removeAuthorizationFromProfile);
        if (result instanceof BusinessException) {
            throw new RoleCouldNotBeRemovedException(userExtId, roleName, (BusinessException) result);
        }
    }

    private Authorization prepareAuthorization(String role, ObjectFactory factory) {
        Authorization authorization = factory.createAuthorization();
        authorization.setRole(prepareRole(role, factory));
        return authorization;
    }

    private Role prepareRole(String role, ObjectFactory factory) {
        Role roleToBeSet = factory.createRole();
        roleToBeSet.setName(role);
        roleToBeSet.setApplicationName(APPLICATION_NAME);
        return roleToBeSet;
    }

    private Profile prepareProfile(String extId, String profileExtId, ObjectFactory factory) {
        Profile profile = factory.createProfile();
        profile.setDefaultProfile(true);
        profile.setUserExtId(extId);
        profile.setExtId(profileExtId);
        return profile;
    }

    private String printUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(user.getFirstName())
                .append(" ")
                .append(user.getName())
                .append(" - extId=")
                .append(user.getExtId())
                .append(" - roles: ")
                .append(printAuthorizations(user.getProfiles().get(0).getAuthorizations()))
                .append("\n");
        }
        if (sb.length() > 0) {
            return sb.toString().substring(0, sb.length() - 1);
        } else {
            return "EMPTY RESPONSE";
        }
    }

    private String printAuthorizations(List<Authorization> authorizations) {
        StringBuilder sb = new StringBuilder();
        for (Authorization authorization : authorizations) {
            sb.append(authorization.getRole().getName())
                .append(", ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }

}

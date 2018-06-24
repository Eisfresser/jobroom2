package ch.admin.seco.jobroom.security.registration.eiam;

import java.util.List;

import ch.adnovum.nevisidm.ws.services.v1.AddAuthorizationToProfile;
import ch.adnovum.nevisidm.ws.services.v1.AddAuthorizationToProfileRequest;
import ch.adnovum.nevisidm.ws.services.v1.Authorization;
import ch.adnovum.nevisidm.ws.services.v1.BusinessException;
import ch.adnovum.nevisidm.ws.services.v1.GetUsersByExtId;
import ch.adnovum.nevisidm.ws.services.v1.GetUsersByExtIdResponse;
import ch.adnovum.nevisidm.ws.services.v1.ObjectFactory;
import ch.adnovum.nevisidm.ws.services.v1.Role;
import ch.adnovum.nevisidm.ws.services.v1.User;
import ch.adnovum.nevisidm.ws.services.v1.UserGetByExtId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;

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
    public User getUserByExtId(String userExtId) throws UserNotFoundException, ExtIdNotUniqueException {
        ObjectFactory factory = new ObjectFactory();
        GetUsersByExtId getUsersByExtId = factory.createGetUsersByExtId();
        UserGetByExtId usersGetByExtId = factory.createUserGetByExtId();
        usersGetByExtId.setClientName(clientName);
        usersGetByExtId.setDetail(DEFAULT_DETAIL_LEVEL);
        usersGetByExtId.getExtIds().add(userExtId);
        getUsersByExtId.setGet(usersGetByExtId);

        LOGGER.debug("Eiam client sending [extId={}]", userExtId);

        GetUsersByExtIdResponse result = (GetUsersByExtIdResponse) webServiceTemplate.marshalSendAndReceive(getUsersByExtId); //, ACTION_ADDING_CALLBACK);
        List<User> users = result.getReturns();

        LOGGER.debug("Eiam client received user = {}", printUsers(users));

        if (users.isEmpty()) {
            throw new UserNotFoundException();
        } else if (users.size() > 1) {
            throw new ExtIdNotUniqueException();
        }
        return users.get(0);
    }

    @Override
    public void addRoleToUser(String userExtId, String profileExtId, String roleName, String applicationName) throws RoleCouldNotBeAddedException {
        ObjectFactory factory = new ObjectFactory();
        AddAuthorizationToProfile addAuthorizationToProfile = factory.createAddAuthorizationToProfile();
        AddAuthorizationToProfileRequest addAuthorizationToProfileRequest = factory.createAddAuthorizationToProfileRequest();
        addAuthorizationToProfileRequest.setClientName(clientName);
        addAuthorizationToProfileRequest.setDetail(DEFAULT_DETAIL_LEVEL);
        Authorization authorization = factory.createAuthorization();
        Role roleToBeSet = factory.createRole();
        roleToBeSet.setName(roleName);
        roleToBeSet.setApplicationName(applicationName);
        authorization.setRole(roleToBeSet);
        addAuthorizationToProfileRequest.setAuthorization(authorization);
        ch.adnovum.nevisidm.ws.services.v1.Profile profile = factory.createProfile();
        profile.setDefaultProfile(true);
        profile.setUserExtId(userExtId);
        profile.setExtId(profileExtId);
        addAuthorizationToProfileRequest.setProfile(profile);
        addAuthorizationToProfile.setRequest(addAuthorizationToProfileRequest);
        Object result = webServiceTemplate.marshalSendAndReceive(addAuthorizationToProfile);
        if (result instanceof BusinessException) {
            throw new RoleCouldNotBeAddedException("The role " + roleName + " could not be added to the user with extId " + userExtId
                + " with the error message: " + ((BusinessException) result).getMessage()
                + " and the reason: " + ((BusinessException) result).getReason());
        }
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

package ch.admin.seco.jobroom.security.registration.eiam;

import java.util.Optional;

import ch.adnovum.nevisidm.ws.services.v1.Profile;
import ch.adnovum.nevisidm.ws.services.v1.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EiamAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EiamAdminService.class);

    private final EiamClient eiamClient;

    @Autowired
    public EiamAdminService(EiamClient eiamClient) {
        this.eiamClient = eiamClient;
    }


    public void addRole(String userExtId, String profileExtId, EiamClientRole eiamClientRole) throws RoleCouldNotBeAddedException {
        this.eiamClient.addRoleToUser(userExtId, profileExtId, eiamClientRole.getName());
    }

    public void removeRole(String extId, EiamClientRole eiamClientRole) throws UserNotFoundException, ExtIdNotUniqueException, RoleCouldNotBeRemovedException {
        this.removeRoleFromUser(extId, eiamClientRole.getName());
    }

    private void removeRoleFromUser(String extId, String role) throws UserNotFoundException, ExtIdNotUniqueException, RoleCouldNotBeRemovedException {
        User user = this.eiamClient.getUserByExtId(extId);
        String profileExtId = extractProfileExtId(user)
            .map(Profile::getExtId)
            .orElseThrow(() -> new IllegalStateException("Not Profile for User having ext-id: " + extId));
        this.eiamClient.removeRoleFromUser(extId, profileExtId, role);
    }

    private Optional<Profile> extractProfileExtId(User user) {
        Optional<Profile> firstProfile = user.getProfiles().stream()
            .peek(profile -> LOGGER.debug("Received Profile: Name: {} ExtId: {}", profile.getName(), profile.getExtId()))
            .filter(profile -> StringUtils.isNotEmpty(profile.getExtId()))
            .findFirst();
        if (!firstProfile.isPresent()) {
            throw new IllegalStateException("Could not find a Profile for User with extid: " + user.getExtId());
        }
        return firstProfile;
    }

}

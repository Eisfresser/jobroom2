package ch.admin.seco.jobroom.security.registration.eiam;

import java.util.Set;
import java.util.stream.Collectors;

import ch.adnovum.nevisidm.ws.services.v1.Profile;
import ch.adnovum.nevisidm.ws.services.v1.Role;
import ch.adnovum.nevisidm.ws.services.v1.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class EiamAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EiamAdminService.class);

    private final EiamClient eiamClient;

    EiamAdminService(EiamClient eiamClient) {
        this.eiamClient = eiamClient;
    }

    public void addRole(String userExtId, String profileExtId, EiamClientRole eiamClientRole) {
        this.eiamClient.addRoleToUser(userExtId, profileExtId, eiamClientRole.getName());
    }

    public void removeRole(String extId, EiamClientRole eiamClientRole) throws UserNotFoundException {
        User user = this.eiamClient.getUserByExtId(extId);
        if (hasRole(user, eiamClientRole)) {
            this.removeRoleFromUser(extId, eiamClientRole.getName());
        } else {
            LOGGER.warn("User with extId: {} doesn't have the requested role: {} to be removed", eiamClientRole);
        }
    }

    public Set<String> getRoles(String userExternalId) throws UserNotFoundException {
        User user = this.eiamClient.getUserByExtId(userExternalId);
        return user.getProfiles().stream()
            .flatMap(profile -> profile.getRoles().stream())
            .map(Role::getName)
            .collect(Collectors.toSet());
    }

    private void removeRoleFromUser(String extId, String role) throws UserNotFoundException {
        User user = this.eiamClient.getUserByExtId(extId);
        String profileExtId = extractProfileExtId(user);
        this.eiamClient.removeRoleFromUser(extId, profileExtId, role);
    }

    private String extractProfileExtId(User user) {
        return user.getProfiles().stream()
            .peek(profile -> LOGGER.debug("Received Profile: Name: {} ExtId: {}", profile.getName(), profile.getExtId()))
            .filter(this::hasAnyJobRoomRole)
            .peek(profile -> LOGGER.debug("Filtering Profile: Name: {} ExtId: {}", profile.getName(), profile.getExtId()))
            .findFirst()
            .map(Profile::getExtId)
            .orElseThrow(() -> new IllegalStateException("No Profiles found for User having ext-id: " + user.getExtId()));
    }

    private boolean hasAnyJobRoomRole(Profile profile) {
        return profile.getRoles().stream()
            .anyMatch(role -> role.getApplicationName().equalsIgnoreCase(EiamClient.APPLICATION_NAME));
    }

    private boolean hasRole(User user, EiamClientRole eiamClientRole) {
        return user.getProfiles().stream().anyMatch(profile -> hasRole(profile, eiamClientRole));
    }

    private boolean hasRole(Profile profile, EiamClientRole eiamClientRole) {
        return profile.getRoles().stream()
            .anyMatch(role -> matchesRoleName(role, eiamClientRole) && matchesApplicationName(role));
    }

    private boolean matchesApplicationName(Role role) {
        return role.getApplicationName().equalsIgnoreCase(EiamClient.APPLICATION_NAME);
    }

    private boolean matchesRoleName(Role role, EiamClientRole eiamClientRole) {
        return role.getName().equalsIgnoreCase(eiamClientRole.getName());
    }

}

package ch.admin.seco.jobroom.security.registration.eiam;

import ch.adnovum.nevisidm.ws.services.v1.Profile;
import ch.adnovum.nevisidm.ws.services.v1.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EiamClientMock implements EiamClient {

    private final Logger log = LoggerFactory.getLogger(EiamClientMock.class);

    @Override
    public User getUserByExtId(String userExtId) {
        log.debug("getUserByExtId of eIAM webservice mock was called with extId={}", userExtId);
        User user = new User();
        user.setFirstName("Hans");
        user.setName("Muster");
        user.setTelephone("+41 31 1234567");
        user.getProfiles().add(prepareProfile());
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return this.getUserByExtId(email);
    }

    private Profile prepareProfile() {
        Profile profile = new Profile();
        profile.setExtId("DUMMY-PROFILE-EXT-ID");
        return profile;
    }

    @Override
    public void addRoleToUser(String userExtId, String profileExtId, String role) {
        log.debug("addRoleToUser of eIAM webservice mock was called with userExtId={} and role={}", userExtId, role);
    }

    @Override
    public void removeRoleFromUser(String userExtId, String profileExtId, String role) {
        log.debug("removeRoleFromUser of eIAM webservice mock was called with userExtId={} and role={}", userExtId, role);
    }

}

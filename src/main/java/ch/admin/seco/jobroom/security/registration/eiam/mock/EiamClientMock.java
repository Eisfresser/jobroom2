package ch.admin.seco.jobroom.security.registration.eiam.mock;

import ch.adnovum.nevisidm.ws.services.v1.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.admin.seco.jobroom.security.registration.eiam.EiamClient;

public class EiamClientMock implements EiamClient {

    private final Logger log = LoggerFactory.getLogger(EiamClientMock.class);

    @Override
    public User getUserByExtId(String userExtId) {
        log.debug("getUserByExtId of eIAM webservice mock was called with extId={}", userExtId);
        User user = new User();
        user.setFirstName("Hans");
        user.setName("Muster");
        user.setTelephone("+41 31 1234567");
        return user;
    }

    @Override
    public void addRoleToUser(String userExtId, String profileExtId, String role, String applicationName) {
        log.debug("addRoleToUser of eIAM webservice mock was called with userExtId={} and role={}", userExtId, role);
    }

    /*

    private String getLanguage(String username) {
        return username.substring(username.lastIndexOf("-") + 1);
    }

    private String getFirstName(String username) {
        String lang = getLanguage(username);
        switch (lang) {
            case "de":
                return "Hans";
            case "fr":
                return "Jean";
            case "it":
                return "Giovani";
            case "en":
                return "John";
            default:
                return "not-found";
        }
    }
    */
}

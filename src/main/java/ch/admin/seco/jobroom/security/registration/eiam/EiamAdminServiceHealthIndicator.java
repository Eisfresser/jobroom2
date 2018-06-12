package ch.admin.seco.jobroom.security.registration.eiam;

import ch.adnovum.nevisidm.ws.services.v1.User;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import ch.admin.seco.jobroom.security.registration.eiam.exceptions.ExtIdNotUniqueException;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.UserNotFoundException;

public class EiamAdminServiceHealthIndicator extends AbstractHealthIndicator {

    private final EiamClient eiamClient;

    private final String extId;

    public EiamAdminServiceHealthIndicator(EiamClient eiamClient, String extId) {
        this.eiamClient = eiamClient;
        this.extId = extId;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            User userByExtId = eiamClient.getUserByExtId(extId);
            builder.withDetail("UserExternalId", userByExtId.getExtId());
            builder.withDetail("UserFirstname", userByExtId.getFirstName());
            builder.withDetail("UserLastName", userByExtId.getName());
        } catch (UserNotFoundException e) {
            builder.withDetail("User", "Not found");
        } catch (ExtIdNotUniqueException e) {
            builder.withDetail("User", "More than one found");
        }
        builder.up();
    }
}

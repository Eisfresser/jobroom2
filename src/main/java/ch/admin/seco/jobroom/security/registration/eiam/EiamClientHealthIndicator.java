package ch.admin.seco.jobroom.security.registration.eiam;

import ch.adnovum.nevisidm.ws.services.v1.User;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

class EiamClientHealthIndicator extends AbstractHealthIndicator {

    private final EiamClient eiamClient;

    private final String extId;

    EiamClientHealthIndicator(EiamClient eiamClient, String extId) {
        this.eiamClient = eiamClient;
        this.extId = extId;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            User userByExtId = eiamClient.getUserByExtId(extId);
            builder.withDetail("user-extId", userByExtId.getExtId());
            builder.withDetail("user-firstName", userByExtId.getFirstName());
            builder.withDetail("user-lastName", userByExtId.getName());
        } catch (UserNotFoundException e) {
            builder.withDetail("user", "No User found with extId: " + this.extId);
        }
        builder.up();
    }
}

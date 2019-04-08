package ch.admin.seco.jobroom.security.registration.uid;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

class UidHealthIndicator extends AbstractHealthIndicator {

    private final UidClient uidClient;

    private final Long monitoringUId;

    UidHealthIndicator(UidClient uidClient, Long monitoringUId) {
        this.uidClient = uidClient;
        this.monitoringUId = monitoringUId;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            FirmData firmData = uidClient.getCompanyByUid(monitoringUId);
            builder.up()
                .withDetail("company-name", firmData.getName())
                .withDetail("company-uid", firmData.getUid());
        } catch (UidCompanyNotFoundException e) {
            builder.up().withDetail("company", "no company found with uid:" + this.monitoringUId);
        } catch (UidClientRuntimeException e) {
            builder.down().withException(e);
        }
    }
}

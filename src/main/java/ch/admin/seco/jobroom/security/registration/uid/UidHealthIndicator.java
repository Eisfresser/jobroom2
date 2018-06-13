package ch.admin.seco.jobroom.security.registration.uid;

import ch.admin.seco.jobroom.security.registration.uid.dto.FirmData;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.CompanyNotFoundException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidClientException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidNotUniqueException;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

public class UidHealthIndicator extends AbstractHealthIndicator {

    private final UidClient uidClient;

    private final Long monitoringUId;

    public UidHealthIndicator(UidClient uidClient, Long monitoringUId) {
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
        } catch (CompanyNotFoundException e) {
            builder.up().withDetail("company", "no company found with uid:" + this.monitoringUId);
        } catch (UidNotUniqueException e) {
            builder.up().withDetail("company", "more than one company found having uid: " + this.monitoringUId);
        } catch (UidClientException e) {
            builder.down().withException(e);
        }
    }
}

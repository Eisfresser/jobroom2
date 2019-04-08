package ch.admin.seco.jobroom.domain.fixture;

import static ch.admin.seco.jobroom.domain.BlacklistedAgent.Builder;
import static ch.admin.seco.jobroom.domain.fixture.UserPrincipalFixture.testPrincipal;

import ch.admin.seco.jobroom.domain.BlacklistedAgentId;
import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.security.UserPrincipal;

public class BlacklistedAgentFixture {

    public static Builder testBlacklistedAgentEmpty() {
        return new Builder();
    }

    public static Builder testBlacklistedAgent() {
        return testBlacklistedAgentEmpty()
            .setId(new BlacklistedAgentId())
            .setCity("city")
            .setCreatedBy(testPrincipal())
            .setExternalId("")
            .setName("name")
            .setStreet("street")
            .setZipCode("8001");
    }
}


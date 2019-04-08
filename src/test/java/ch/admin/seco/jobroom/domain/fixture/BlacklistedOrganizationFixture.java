package ch.admin.seco.jobroom.domain.fixture;

import static ch.admin.seco.jobroom.domain.BlacklistedAgent.Builder;

import ch.admin.seco.jobroom.domain.BlacklistedAgentId;
import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.security.UserPrincipal;

public class BlacklistedOrganizationFixture {

    public static Builder testBlacklistedOrganizationEmpty() {
        return new Builder();
    }

    public static Builder testBlacklistedOrganization() {
        return testBlacklistedOrganizationEmpty()
            .setId(new BlacklistedAgentId())
            .setCity("city")
            .setCreatedBy(testPrincipal())
            .setExternalId("")
            .setName("name")
            .setStreet("street")
            .setZipCode("8001");
    }

    private static UserPrincipal testPrincipal() {
        return new UserPrincipal(
            new UserInfoId("usserInfoId"),
            "firstName",
            "lastName",
            "email@emial.ch",
            "userExtId",
            "ch"
        );
    }
}


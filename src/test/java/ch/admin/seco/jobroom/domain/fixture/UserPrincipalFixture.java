package ch.admin.seco.jobroom.domain.fixture;

import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.security.UserPrincipal;

public class UserPrincipalFixture {

    public static UserPrincipal testPrincipal() {
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

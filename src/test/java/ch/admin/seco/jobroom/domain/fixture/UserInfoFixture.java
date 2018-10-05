package ch.admin.seco.jobroom.domain.fixture;

import static ch.admin.seco.jobroom.config.Constants.DEFAULT_LANGUAGE;

import ch.admin.seco.jobroom.domain.UserInfo;

public class UserInfoFixture {

    public static UserInfo testUserInfo() {
        return new UserInfo("Hans", "Muster", "john.doe@example.com", "extid", DEFAULT_LANGUAGE);
    }
}

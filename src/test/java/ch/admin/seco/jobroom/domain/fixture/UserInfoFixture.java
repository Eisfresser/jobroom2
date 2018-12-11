package ch.admin.seco.jobroom.domain.fixture;

import static ch.admin.seco.jobroom.config.Constants.DEFAULT_LANGUAGE;

import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.UserInfo;

public class UserInfoFixture {

    public static UserInfo testUserInfo() {
        return new UserInfo("Hans", "Muster", "john.doe@example.com", "extid", DEFAULT_LANGUAGE);
    }

    public static UserInfo testCompanyUserInfo() {
        UserInfo userInfo = new UserInfo("TEST", "TEST", "test@example.com", "1234", "de");
        Company company = new Company("ACME AG", "CHE-123.456.789");
        userInfo.requestAccessAsEmployer(company);
        return userInfo;
    }
}

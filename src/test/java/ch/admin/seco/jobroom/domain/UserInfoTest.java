package ch.admin.seco.jobroom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;

public class UserInfoTest {


    @Test
    public void testUnregister() {
        // given
        UserInfo userInfo = prepareCompanyUser();

        // when
        userInfo.unregister();

        //then
        assertThat(userInfo.getRegistrationStatus()).isEqualTo(RegistrationStatus.UNREGISTERED);
        assertThat(userInfo.getCompany()).isNull();
    }

    @Test
    public void testUnregisterTwice() {
        // given
        UserInfo userInfo = prepareCompanyUser();

        // when
        userInfo.unregister();
        userInfo.unregister();

        //then
        assertThat(userInfo.getRegistrationStatus()).isEqualTo(RegistrationStatus.UNREGISTERED);
        assertThat(userInfo.getCompany()).isNull();
    }

    private UserInfo prepareCompanyUser() {
        UserInfo userInfo = new UserInfo("TEST", "TEST", "test@example.com", "1234", "de");
        Company company = new Company("ACME AG", "CHE-123.456.789");
        userInfo.registerExistingAgent(company);
        return userInfo;
    }

}

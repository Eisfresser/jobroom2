package ch.admin.seco.jobroom.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.service.CompanyContactTemplateNotFoundException;

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

    @Test
    public void testAddMultipleCompanyContactTemplates() {
        // given
        UserInfo userInfo = prepareCompanyUser();
        Company company1 = new Company("ACME AG", "CHE-123.456.789");
        Company company2 = new Company("ACME AG", "CHE-123.456.789");

        userInfo.addCompany(company1);
        userInfo.addCompany(company2);

        // when
        userInfo.addCompanyContactTemplate(CompanyContactTemplate.builder()
            .setCompanyId(company1.getId()).build());

        userInfo.addCompanyContactTemplate(CompanyContactTemplate.builder()
            .setCompanyId(company2.getId()).build());

        //then
        assertThat(userInfo.getCompanyContactTemplates()).hasSize(2);
    }

    @Test
    public void testAddCompanyContactTemplate() {
        // given
        UserInfo userInfo = prepareCompanyUser();
        Company company = userInfo.getCompany();

        // when
        userInfo.addCompanyContactTemplate(CompanyContactTemplate.builder()
            .setCompanyId(company.getId()).build());

        //then
        assertThat(userInfo.getCompanyContactTemplates()).hasSize(1);
    }

    @Test
    public void testUpdateCompanyContactTemplate() throws CompanyContactTemplateNotFoundException {
        // given
        UserInfo userInfo = prepareCompanyUser();
        Company company = userInfo.getCompany();

        // when
        userInfo.addCompanyContactTemplate(CompanyContactTemplate.builder()
            .setCompanyId(company.getId())
            .setCompanyName("Test1").build());

        userInfo.addCompanyContactTemplate(CompanyContactTemplate.builder()
            .setCompanyId(company.getId())
            .setCompanyName("Test2").build());

        //then
        assertThat(userInfo.getCompanyContactTemplate(company.getId()).getCompanyName()).isEqualToIgnoringCase("Test2");
        assertThat(userInfo.getCompanyContactTemplates()).hasSize(1);
    }

    @Test
    public void testRemoveCompanyContactTemplate() {
        // given
        UserInfo userInfo = prepareCompanyUser();
        Company company = userInfo.getCompany();

        // when
        userInfo.addCompanyContactTemplate(CompanyContactTemplate.builder()
            .setCompanyId(company.getId()).build());
        assertThat(userInfo.getCompanyContactTemplates()).hasSize(1);

        //then
        userInfo.removeContactTemplate(company.getId());
        assertThat(userInfo.getCompanyContactTemplates()).hasSize(0);
    }

    private UserInfo prepareCompanyUser() {
        UserInfo userInfo = new UserInfo("TEST", "TEST", "test@example.com", "1234", "de");
        Company company = new Company("ACME AG", "CHE-123.456.789");
        userInfo.requestAccessAsEmployer(company);
        return userInfo;
    }


}

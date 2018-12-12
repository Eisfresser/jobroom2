package ch.admin.seco.jobroom.domain;

import static ch.admin.seco.jobroom.domain.fixture.UserInfoFixture.testCompanyUserInfo;
import static ch.admin.seco.jobroom.domain.fixture.UserInfoFixture.testUserInfo;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.Test;

import org.springframework.util.ReflectionUtils;

import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.service.CompanyContactTemplateNotFoundException;

public class UserInfoTest {


    @Test
    public void testUnregister() {
        // given
        UserInfo userInfo = testCompanyUserInfo();

        // when
        userInfo.unregister();

        //then
        assertThat(userInfo.getRegistrationStatus()).isEqualTo(RegistrationStatus.UNREGISTERED);
        assertThat(userInfo.getCompany()).isNull();
    }

    @Test
    public void testUnregisterTwice() {
        // given
        UserInfo userInfo = testCompanyUserInfo();

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
        UserInfo userInfo = testCompanyUserInfo();
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
        UserInfo userInfo = testCompanyUserInfo();
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
        UserInfo userInfo = testCompanyUserInfo();
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
        UserInfo userInfo = testCompanyUserInfo();
        Company company = userInfo.getCompany();

        // when
        userInfo.addCompanyContactTemplate(CompanyContactTemplate.builder()
            .setCompanyId(company.getId()).build());
        assertThat(userInfo.getCompanyContactTemplates()).hasSize(1);

        //then
        userInfo.removeContactTemplate(company.getId());
        assertThat(userInfo.getCompanyContactTemplates()).hasSize(0);
    }

    @Test
    public void testIsLatestLegalTermsAccepted_with_not_finished_registration() {
        // given
        UserInfo userInfo = testUserInfo();

        // then
        assertThat(userInfo.isLatestLegalTermsAccepted(LocalDate.now().minusDays(1))).isFalse();
        assertThat(userInfo.isLatestLegalTermsAccepted(LocalDate.now().plusDays(1))).isFalse();
    }

    @Test
    public void testIsLatestLegalTermsAccepted_with_accepted_terms() {
        // given
        UserInfo userInfo = testUserInfo();

        // when
        userInfo.acceptLegalTerms();

        // then
        assertThat(userInfo.isLatestLegalTermsAccepted(LocalDate.now().minusDays(1))).isTrue();
        assertThat(userInfo.isLatestLegalTermsAccepted(LocalDate.now())).isTrue();
        assertThat(userInfo.isLatestLegalTermsAccepted(LocalDate.now().plusDays(1))).isFalse();
    }

}

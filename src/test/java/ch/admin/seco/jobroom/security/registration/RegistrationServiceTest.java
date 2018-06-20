package ch.admin.seco.jobroom.security.registration;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.Organization;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.CompanyRepository;
import ch.admin.seco.jobroom.repository.OrganizationRepository;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.repository.UserRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;
import ch.admin.seco.jobroom.security.registration.stes.StesService;
import ch.admin.seco.jobroom.security.registration.stes.StesVerificationResult;
import ch.admin.seco.jobroom.security.registration.uid.UidClient;
import ch.admin.seco.jobroom.security.registration.uid.dto.AddressData;
import ch.admin.seco.jobroom.security.registration.uid.dto.FirmData;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.CompanyNotFoundException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidClientException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidNotUniqueException;
import ch.admin.seco.jobroom.security.saml.utils.IamService;
import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceTest {

    private static final String VALID_ACCESS_CODE = "4FQ2ABBP";
    private static final String INVALID_ACCESS_CODE = "ABC";
    private static final String EXT_ID = "CH1234";
    private static final String PROFILE_EXT_ID = "123456";
    private static final String USER_ROLE_USER = "ROLE_USER";
    private static final String USER_ROLE_REGISTERED = "ROLE_REGISTERED";
    private static final long VALID_UID = 1234567;
    private static final String VALID_UID_PREFIX = "CHE";
    private static final String VALID_AVG_ID = "12345";
    private static final long VALID_PERS_NO = 1234;
    private static final int BIRTHDATE_YEAR = 1999;
    private static final int BIRTHDATE_MONTH = 9;
    private static final int BIRTHDATE_DAY = 19;
    private static final String VALID_PASSWORD = "p@ssw0rd";
    private static final String VALID_LOGIN = "hamu@mail.ch";
    private static final String INVALID_PASSWORD = "wrongPassword";

    private static final String VALID_FULL_UID = "CHE-123.456.789";

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private UserInfoRepository mockUserInfoRepository;

    @Mock
    private CompanyRepository mockCompanyRepository;

    @Mock
    private MailService mockMailService;

    @Mock
    private OrganizationRepository mockOrganizationRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private UidClient mockUidClient;

    @Mock
    private IamService mockIamService;

    @Mock
    private StesService mockStesService;

    @Mock
    private CurrentUserService currentUserService;

    @Before
    public void setup() {
        registrationService.setAccessCodeMailRecipient("servicedesk@jobroom.ch");
        setupSecurityContextMockWithRegistrationStatus();
        UserInfo userInfo = this.getDummyUser();
        when(this.mockUserInfoRepository.findById(any())).thenReturn(Optional.of(userInfo));
    }

    @Test
    public void insertNewJobseeker() throws RoleCouldNotBeAddedException, InvalidPersonenNumberException, StesServiceException {
        StesVerificationResult stesVerificationResult = new StesVerificationResult();
        stesVerificationResult.setVerified(true);
        when(mockStesService.verifyStesRegistrationData(any())).thenReturn(stesVerificationResult);

        this.registrationService.registerAsJobSeeker(LocalDate.of(BIRTHDATE_YEAR, BIRTHDATE_MONTH, BIRTHDATE_DAY), VALID_PERS_NO);

        verify(mockIamService).addJobSeekerRoleToUser(anyString(), anyString());
        verify(currentUserService).addRoleToSession(eq(AuthoritiesConstants.ROLE_JOBSEEKER_CLIENT));
    }

    @Test
    public void insertNewEmployerwithNewCompany() throws UidClientException, CompanyNotFoundException, UidNotUniqueException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockUidClient.getCompanyByUid(anyLong())).thenReturn(getDummyFirm());          // found company in UID register
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.empty());   // company not found in database -> new company
        when(mockCompanyRepository.save(any())).thenReturn(getDummyCompany());              // save company

        this.registrationService.requestAccessAsEmployer(VALID_UID);

        verify(mockUserInfoRepository).findById(any());
        verify(mockUidClient).getCompanyByUid(anyLong());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository).save(any());
        verify(mockMailService).sendAccessCodeLetterMail(anyString(), any());
    }

    @Test
    public void getCompanyByUid() throws UidClientException, CompanyNotFoundException, UidNotUniqueException {
        when(mockUidClient.getCompanyByUid(anyLong())).thenReturn(getDummyFirm());

        FirmData companyByUid = this.registrationService.getCompanyByUid(VALID_UID);

        verify(mockUidClient).getCompanyByUid(anyLong());
        assertTrue(companyByUid.getName().equals("mimacom ag"));
    }

    @Test
    public void insertNewEmployerwithExistingCompany() throws UidClientException, CompanyNotFoundException, UidNotUniqueException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockUidClient.getCompanyByUid(anyLong())).thenReturn(getDummyFirm());          // found company in UID register
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.of(getDummyCompany()));   // company found in database

        this.registrationService.requestAccessAsEmployer(VALID_UID);

        verify(mockUserInfoRepository).findById(any());
        verify(mockUidClient).getCompanyByUid(anyLong());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository, never()).save(any());
        verify(mockMailService).sendAccessCodeLetterMail(anyString(), any());
    }

    @Test
    public void insertNewAgent() throws CompanyNotFoundException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockOrganizationRepository.findByExternalId(anyString())).thenReturn(getDummyOrganization());  // found company in AVG list
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.empty());   // company not found in database -> new company
        when(mockCompanyRepository.save(any())).thenReturn(getDummyCompany());              // save company

        this.registrationService.requestAccessAsAgent(VALID_AVG_ID);

        verify(mockUserInfoRepository).findById(any());
        verify(mockOrganizationRepository).findByExternalId(anyString());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository).save(any());
        verify(mockMailService).sendAccessCodeLetterMail(anyString(), any());
    }

    @Test
    public void insertNewAgentWithExistingCompany() throws CompanyNotFoundException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockOrganizationRepository.findByExternalId(anyString())).thenReturn(getDummyOrganization());  // found company in AVG list
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.of(getDummyCompany()));   // company found in database

        this.registrationService.requestAccessAsAgent(VALID_AVG_ID);

        verify(mockUserInfoRepository).findById(any());
        verify(mockOrganizationRepository).findByExternalId(anyString());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository, never()).save(any());
        verify(mockMailService).sendAccessCodeLetterMail(anyString(), any());
    }

    @Test
    public void registerEmployer() throws RoleCouldNotBeAddedException, InvalidAccessCodeException {

        setupSecurityContextMockWithRegistrationStatus();  // overwrite with one that has registration status set
        UserInfo userInfo = this.getDummyUser();
        userInfo.requestAccessAsEmployer(getDummyCompany());
        when(this.mockUserInfoRepository.findById(any())).thenReturn(Optional.of(userInfo));


        RegistrationResultDTO registrationResultDTO = this.registrationService.registerAsEmployerOrAgent(userInfo.getAccessCode());

        verify(mockIamService).addCompanyRoleToUser(anyString(), anyString());
        assertTrue(registrationResultDTO.isSuccess());
        assertTrue(registrationResultDTO.getType().equals(Constants.TYPE_EMPLOYER));
        verify(currentUserService).addRoleToSession(AuthoritiesConstants.ROLE_COMPANY);
    }

    @Test
    public void registerExistingAgent() throws RoleCouldNotBeAddedException, InvalidOldLoginException {
        when(mockUserRepository.findOneWithAuthoritiesByLogin(anyString())).thenReturn(getOldDummyUser());

        when(mockCompanyRepository.save(any())).thenReturn(getDummyCompany());

        registrationService.registerExistingAgent(VALID_LOGIN, VALID_PASSWORD);

        verify(mockIamService).addAgentRoleToUser(anyString(), anyString());
        verify(currentUserService).addRoleToSession(AuthoritiesConstants.ROLE_PRIVATE_EMPLOYMENT_AGENT);
    }


    @Test(expected = InvalidOldLoginException.class)
    public void validateOldLoginWrongPassword() throws RoleCouldNotBeAddedException, InvalidOldLoginException {
        when(mockUserRepository.findOneWithAuthoritiesByLogin(anyString())).thenReturn(Optional.empty());

        when(mockUserRepository.findOneWithAuthoritiesByLogin(anyString())).thenReturn(getOldDummyUser());

        this.registrationService.registerExistingAgent(VALID_LOGIN, INVALID_PASSWORD);

    }

    private void setupSecurityContextMockWithRegistrationStatus() {
        Collection<GrantedAuthority> authorities = new HashSet<>(2);
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority(USER_ROLE_USER);
        authorities.add(userAuthority);
        SimpleGrantedAuthority registeredAuthority = new SimpleGrantedAuthority(USER_ROLE_REGISTERED);
        authorities.add(registeredAuthority);

        UserPrincipal userPrincipal = Mockito.mock(UserPrincipal.class);
        when(userPrincipal.getUserExtId()).thenReturn(EXT_ID);
        when(userPrincipal.getUserDefaultProfileExtId()).thenReturn(PROFILE_EXT_ID);
        when(this.currentUserService.getPrincipal()).thenReturn(userPrincipal);
    }

    public UserInfo getDummyUser() {
        UserInfo user = new UserInfo("Hans", "Muster", "hans-muster@example.com", "EXT-ID", "de");
        user.setAccessCode(VALID_ACCESS_CODE);
        return user;
    }

    private Optional<User> getOldDummyUser() {
        User user = new User();
        user.setFirstName("Hans");
        user.setLastName("Muster");
        user.setLogin(VALID_LOGIN);
        MD5PasswordEncoder md5PasswordEncoder = new MD5PasswordEncoder();
        user.setPassword(md5PasswordEncoder.encode(VALID_PASSWORD));
        user.setOrganization(getDummyOrganization().get());
        return Optional.of(user);
    }

    private Company getDummyCompany() {
        return new Company("ACME AG", VALID_FULL_UID);
    }

    private Optional<Organization> getDummyOrganization() {
        Organization organization = new Organization();
        organization.setName("ACME AG");
        return Optional.of(organization);
    }

    private FirmData getDummyFirm() {
        FirmData firm = new FirmData();
        firm.setName("mimacom ag");
        firm.setAdditionalName("Software Development");
        firm.setUid(115635627);
        firm.setChId("CH03630474042");
        firm.setUidPrefix("CHE");
        firm.setUidPublic(true);
        firm.setCommercialRegisterEntryDate(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        firm.setActive(true);
        firm.setMwst("CHE-115.635.627");
        firm.setVatEntryStatus("ACTIVE");
        firm.setVatLiquidationDate(null);
        AddressData address = new AddressData();
        address.setStreet("Galgenfeldweg");
        address.setBuildingNum("16");
        address.setZip("3006");
        address.setCity("Bern");
        address.setCanton("BE");
        address.setCommunityNumber("351");
        address.setCountry("CH");
        firm.setAddress(address);
        return firm;
    }

    private RegisterJobseekerVM getDummyInputData() {
        RegisterJobseekerVM inputData = new RegisterJobseekerVM();
        inputData.setBirthdateYear(BIRTHDATE_YEAR);
        inputData.setBirthdateMonth(BIRTHDATE_MONTH);
        inputData.setBirthdateDay(BIRTHDATE_DAY);
        inputData.setPersonNumber(VALID_PERS_NO);
        return inputData;
    }

}

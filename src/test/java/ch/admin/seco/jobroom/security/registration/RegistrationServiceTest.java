package ch.admin.seco.jobroom.security.registration;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import feign.FeignException;
import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.Organization;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.repository.CompanyRepository;
import ch.admin.seco.jobroom.repository.OrganizationRepository;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.repository.UserRepository;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
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
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;

@RunWith(SpringRunner.class)
public class RegistrationServiceTest {

    private static final String VALID_ACCESS_CODE = "4FQ2ABBP";
    private static final String INVALID_ACCESS_CODE = "ABC";
    private static final String EXT_ID = "CH1234";
    private static final String PROFILE_EXT_ID = "123456";
    private static final String USER_ROLE_USER = "ROLE_USER";
    private static final String USER_ROLE_REGISTERED = "ROLE_REGISTERED";
    private static final int VALID_UID = 1234567;
    private static final String VALID_UID_PREFIX = "CHE";
    private static final String VALID_AVG_ID = "12345";
    private static final long VALID_PERS_NO = 1234;
    private static final int BIRTHDATE_YEAR = 1999;
    private static final int BIRTHDATE_MONTH = 9;
    private static final int BIRTHDATE_DAY = 19;
    private static final String VALID_PASSWORD = "p@ssw0rd";
    private static final String VALID_LOGIN = "hamu@mail.ch";
    private static final String INVALID_PASSWORD = "wrongPassword";

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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        registrationService.setAccessCodeMailRecipient("servicedesk@jobroom.ch");

        setupSecurityContextMock();
    }

    @Test
    public void insertNewJobseeker() throws UserAlreadyExistsException, RoleCouldNotBeAddedException, NoValidPrincipalException {
        // mockUidClient does nothing (no exception means it is ok -> new user)
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());

        this.registrationService.insertNewJobseeker();

        verify(mockIamService).addJobSeekerRoleToUser(anyString(), anyString());
        verify(mockUserInfoRepository).save(any());
    }

    @Test (expected = UserAlreadyExistsException.class)
    public void insertExisingJobseekerFails() throws UserAlreadyExistsException, RoleCouldNotBeAddedException, NoValidPrincipalException {
        when(mockUserInfoRepository.findOneByUserExternalId(anyString())).thenReturn(Optional.of(getDummyUser()));    // existing user

        this.registrationService.insertNewJobseeker();

        verify(mockIamService, never()).addJobSeekerRoleToUser(anyString(), anyString());
        verify(mockUserInfoRepository, never()).save(any());
    }

    @Test
    public void insertNewEmployerwithNewCompany() throws UidClientException, NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, UidNotUniqueException, PersistenceException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockUidClient.getCompanyByUid(anyLong())).thenReturn(getDummyFirm());          // found company in UID register
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.empty());   // company not found in database -> new company
        when(mockCompanyRepository.save(any())).thenReturn(getDummyCompany());              // save company
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());                // save user

        this.registrationService.insertNewEmployer(VALID_UID);

        verify(mockUserInfoRepository).findOneByUserExternalId(anyString());
        verify(mockUidClient).getCompanyByUid(anyLong());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository).save(any());
        verify(mockUserInfoRepository).save(any());
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
    public void insertNewEmployerwithExistingCompany() throws UidClientException, NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, UidNotUniqueException, PersistenceException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockUidClient.getCompanyByUid(anyLong())).thenReturn(getDummyFirm());          // found company in UID register
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.of(getDummyCompany()));   // company found in database
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());                // save user

        this.registrationService.insertNewEmployer(VALID_UID);

        verify(mockUserInfoRepository).findOneByUserExternalId(anyString());
        verify(mockUidClient).getCompanyByUid(anyLong());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository, never()).save(any());
        verify(mockUserInfoRepository).save(any());
        verify(mockMailService).sendAccessCodeLetterMail(anyString(), any());
    }

    @Test (expected = UserAlreadyExistsException.class)
    public void insertExistingEmployerFails() throws UidClientException, NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, UidNotUniqueException, PersistenceException {
        when(mockUserInfoRepository.findOneByUserExternalId(anyString())).thenReturn(Optional.of(getDummyUser()));    // existing user

        this.registrationService.insertNewEmployer(VALID_UID);

        verify(mockUserInfoRepository).findOneByUserExternalId(anyString());
        verify(mockUidClient, never()).getCompanyByUid(anyLong());
    }

    @Test
    public void insertNewAgent() throws UserAlreadyExistsException, NoValidPrincipalException, CompanyNotFoundException, PersistenceException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockOrganizationRepository.findByExternalId(anyString())).thenReturn(getDummyOrganization());  // found company in AVG list
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.empty());   // company not found in database -> new company
        when(mockCompanyRepository.save(any())).thenReturn(getDummyCompany());              // save company
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());                // save user

        this.registrationService.insertNewAgent(VALID_AVG_ID);

        verify(mockUserInfoRepository).findOneByUserExternalId(anyString());
        verify(mockOrganizationRepository).findByExternalId(anyString());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository).save(any());
        verify(mockUserInfoRepository).save(any());
        verify(mockMailService).sendAccessCodeLetterMail(anyString(), any());
    }

    @Test
    public void insertNewAgentWithExistingCompany() throws NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, PersistenceException {
        // userInfoRepository does nothing (no exception means it is ok -> new user)
        when(mockOrganizationRepository.findByExternalId(anyString())).thenReturn(getDummyOrganization());  // found company in AVG list
        when(mockCompanyRepository.findByExternalId(any())).thenReturn(Optional.of(getDummyCompany()));   // company found in database
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());                // save user

        this.registrationService.insertNewAgent(VALID_AVG_ID);

        verify(mockUserInfoRepository).findOneByUserExternalId(anyString());
        verify(mockOrganizationRepository).findByExternalId(anyString());
        verify(mockCompanyRepository).findByExternalId(any());
        verify(mockCompanyRepository, never()).save(any());
        verify(mockUserInfoRepository).save(any());
        verify(mockMailService).sendAccessCodeLetterMail(anyString(), any());
    }

    @Test (expected = UserAlreadyExistsException.class)
    public void insertExistingAgentFails() throws NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, PersistenceException {
        when(mockUserInfoRepository.findOneByUserExternalId(anyString())).thenReturn(Optional.of(getDummyUser()));    // existing user

        this.registrationService.insertNewAgent(VALID_AVG_ID);

        verify(mockUserInfoRepository).findOneByUserExternalId(anyString());
        verify(mockOrganizationRepository, never()).findByExternalId(anyString());
    }

    @Test
    public void validatePersonNumber() throws StesServiceException {
        StesVerificationResult stesVerificationResult = new StesVerificationResult();
        stesVerificationResult.setVerified(true);
        when(mockStesService.verifyStesRegistrationData(any())).thenReturn(stesVerificationResult);

        boolean result = this.registrationService.validatePersonNumber(getDummyInputData());

        verify(mockStesService).verifyStesRegistrationData(any());
        assertTrue(result);
    }

    @Test
    public void validatePersonNumberFails() throws StesServiceException {
        StesVerificationResult stesVerificationResult = new StesVerificationResult();
        stesVerificationResult.setVerified(false);
        when(mockStesService.verifyStesRegistrationData(any())).thenReturn(stesVerificationResult);

        boolean result = this.registrationService.validatePersonNumber(getDummyInputData());

        verify(mockStesService).verifyStesRegistrationData(any());
        assertTrue(!result);
    }

    @Test (expected = StesServiceException.class)
    public void validatePersonNumberServiceFailed() throws StesServiceException {
        when(mockStesService.verifyStesRegistrationData(any())).thenThrow(FeignException.errorStatus("verifyStesRegistrationData", Response.builder().status(500).headers(new HashMap<>()).build()));

        this.registrationService.validatePersonNumber(getDummyInputData());

        verify(mockStesService).verifyStesRegistrationData(any());
    }

    @Test
    public void registerEmployer() throws RoleCouldNotBeAddedException, NoValidPrincipalException {
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());
        setupSecurityContextMockWithRegistrationStatus(RegistrationStatus.VALIDATION_EMP);  // overwrite with one that has registration status set

        RegistrationResultDTO registrationResultDTO = this.registrationService.registerEmployerOrAgent();

        verify(mockIamService).addCompanyRoleToUser(anyString(), anyString());
        verify(mockUserInfoRepository).save(any());
        assertTrue(registrationResultDTO.isSuccess());
        assertTrue(registrationResultDTO.getType().equals(Constants.TYPE_EMPLOYER));
    }

    @Test
    public void registerAgent() throws RoleCouldNotBeAddedException, NoValidPrincipalException {
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());

        this.registrationService.registerExistingAgent();

        verify(mockIamService).addAgentRoleToUser(anyString(), anyString());
        verify(mockUserInfoRepository).save(any());
    }

    @Test
    public void validateOldLogin() {
        when(mockUserRepository.findOneWithAuthoritiesByLogin(anyString())).thenReturn(getOldDummyUser());

        boolean result = this.registrationService.validateOldLogin(VALID_LOGIN, VALID_PASSWORD);

        verify(mockUserRepository).findOneWithAuthoritiesByLogin(anyString());
        assertTrue(result);
    }

    @Test
    public void validateOldLoginUserNotExist() {
        when(mockUserRepository.findOneWithAuthoritiesByLogin(anyString())).thenReturn(Optional.empty());

        boolean result = this.registrationService.validateOldLogin(VALID_LOGIN, VALID_PASSWORD);

        verify(mockUserRepository).findOneWithAuthoritiesByLogin(anyString());
        assertTrue(!result);
    }
    @Test
    public void validateOldLoginWrongPassword() {
        when(mockUserRepository.findOneWithAuthoritiesByLogin(anyString())).thenReturn(getOldDummyUser());

        boolean result = this.registrationService.validateOldLogin(VALID_LOGIN, INVALID_PASSWORD);

        verify(mockUserRepository).findOneWithAuthoritiesByLogin(anyString());
        assertTrue(!result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void validateOldLoginNoUsername() {
        this.registrationService.validateOldLogin(null, INVALID_PASSWORD);

        verify(mockUserRepository, never()).findOneWithAuthoritiesByLogin(anyString());
    }

    @Test (expected = IllegalArgumentException.class)
    public void validateOldLoginNoPassword() {
        this.registrationService.validateOldLogin(VALID_LOGIN, null);

        verify(mockUserRepository, never()).findOneWithAuthoritiesByLogin(anyString());
    }

    @Test
    public void updateRegistrationStatus() {
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());
        UserInfo dummyUser = getDummyUser();
        dummyUser.setId(UUID.randomUUID());

        this.registrationService.updateRegistrationStatus(dummyUser, RegistrationStatus.VALIDATION_EMP);

        verify(mockUserInfoRepository).save(any());
    }

    @Test (expected = IllegalArgumentException.class)
    public void updateRegistrationStatusNewUser() {
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());

        this.registrationService.updateRegistrationStatus(getDummyUser(), RegistrationStatus.VALIDATION_EMP);

        verify(mockUserInfoRepository, never()).save(any());
    }

    @Test (expected = IllegalArgumentException.class)
    public void updateRegistrationStatusNotAllowedTransition() {
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());
        UserInfo dummyUser = getDummyUser();
        dummyUser.setId(UUID.randomUUID());
        dummyUser.setRegistrationStatus(RegistrationStatus.REGISTERED);

        this.registrationService.updateRegistrationStatus(dummyUser, RegistrationStatus.VALIDATION_EMP);

        verify(mockUserInfoRepository, never()).save(any());
    }

    @Test
    public void setCompanyForUser() throws CompanySelectionFailedException {
        when(mockCompanyRepository.findByExternalId(anyString())).thenReturn(Optional.of(getDummyCompany()));
        when(mockCompanyRepository.save(any())).thenReturn(getDummyCompany());              // save company
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());                // save user

        this.registrationService.setCompanyForUser(getDummyUser(), getDummyFirm());

        verify(mockCompanyRepository).findByExternalId(anyString());
        verify(mockCompanyRepository).save(any());
        verify(mockUserInfoRepository).save(any());
    }

    @Test
    public void setNewCompanyForUser() throws CompanySelectionFailedException, UidClientException, UidNotUniqueException, CompanyNotFoundException {
        when(mockCompanyRepository.findByExternalId(anyString())).thenReturn(Optional.empty()); // company not found -> new company
        when(mockUidClient.getCompanyByUid(anyLong())).thenReturn(getDummyFirm());              // found company in UID register
        when(mockCompanyRepository.save(any())).thenReturn(getDummyCompany());                  // save company
        when(mockUserInfoRepository.save(any())).thenReturn(getDummyUser());                    // save user

        this.registrationService.setCompanyForUser(getDummyUser(), getDummyFirm());

        verify(mockCompanyRepository).findByExternalId(anyString());
        verify(mockUidClient).getCompanyByUid(anyLong());
        verify(mockCompanyRepository).save(any());
        verify(mockUserInfoRepository).save(any());
    }

    @Test (expected = CompanySelectionFailedException.class)
    public void setCompanyForUserNotFoundInUidRegister() throws CompanySelectionFailedException, UidClientException, UidNotUniqueException, CompanyNotFoundException {
        when(mockCompanyRepository.findByExternalId(anyString())).thenReturn(Optional.empty()); // company not found -> new company
        when(mockUidClient.getCompanyByUid(anyLong())).thenThrow(CompanyNotFoundException.class);  // no company found in UID register

        this.registrationService.setCompanyForUser(getDummyUser(), getDummyFirm());

        verify(mockCompanyRepository).findByExternalId(anyString());
        verify(mockUidClient).getCompanyByUid(anyLong());
        verify(mockCompanyRepository, never()).save(any());
        verify(mockUserInfoRepository, never()).save(any());
    }

    @Test
    public void createAccessCode() {
        String accessCode = this.registrationService.createAccessCode();

        assertTrue(StringUtils.hasText(accessCode));
        assertTrue(accessCode.length() == 8);
        assertTrue(accessCode.matches("[A-Z2-7]{8}"));
    }

    @Test
    public void validateAccessCode() throws NoValidPrincipalException {
        boolean result = this.registrationService.validateAccessCode(VALID_ACCESS_CODE);

        assertTrue(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void validateAccessCodeNull() throws NoValidPrincipalException {
        this.registrationService.validateAccessCode(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void validateAccessCodeWrongLength() throws NoValidPrincipalException {
        this.registrationService.validateAccessCode(INVALID_ACCESS_CODE);
    }

    private void setupSecurityContextMockWithRegistrationStatus(RegistrationStatus registrationStatus) {
        UserInfo user = Mockito.mock(UserInfo.class);
        when(user.getUserExternalId()).thenReturn(EXT_ID);
        when(user.getAccessCode()).thenReturn(VALID_ACCESS_CODE);

        if (registrationStatus != null) {
            when(user.getRegistrationStatus()).thenReturn(registrationStatus);
        }

        Collection<GrantedAuthority> authorities = new HashSet<>(2);
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority(USER_ROLE_USER);
        authorities.add(userAuthority);
        SimpleGrantedAuthority registeredAuthority = new SimpleGrantedAuthority(USER_ROLE_REGISTERED);
        authorities.add(registeredAuthority);

        EiamUserPrincipal eiamUserPrincipal = Mockito.mock(EiamUserPrincipal.class);
        when(eiamUserPrincipal.getUser()).thenReturn(user);
        when(eiamUserPrincipal.getUserDefaultProfileExtId()).thenReturn(PROFILE_EXT_ID);
        Mockito.doReturn(authorities).when(eiamUserPrincipal).getAuthorities();

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(eiamUserPrincipal);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void setupSecurityContextMock() {
        setupSecurityContextMockWithRegistrationStatus(null);
    }

    public UserInfo getDummyUser() {
        UserInfo user = new UserInfo();
        user.setFirstName("Hans");
        user.setLastName("Muster");
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
        return Optional.of(user);
    }

    private Company getDummyCompany() {
        String uid = registrationService.getFullUid(VALID_UID_PREFIX, VALID_UID);
        return new Company("ACME AG", uid);
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

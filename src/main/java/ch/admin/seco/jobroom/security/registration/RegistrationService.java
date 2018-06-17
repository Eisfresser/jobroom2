package ch.admin.seco.jobroom.security.registration;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.Organization;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.enumeration.CompanySource;
import ch.admin.seco.jobroom.domain.enumeration.CompanyType;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.repository.CompanyRepository;
import ch.admin.seco.jobroom.repository.OrganizationRepository;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.repository.UserRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;
import ch.admin.seco.jobroom.security.registration.stes.StesService;
import ch.admin.seco.jobroom.security.registration.stes.StesVerificationRequest;
import ch.admin.seco.jobroom.security.registration.stes.StesVerificationResult;
import ch.admin.seco.jobroom.security.registration.uid.UidClient;
import ch.admin.seco.jobroom.security.registration.uid.dto.FirmData;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.CompanyNotFoundException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidClientException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidNotUniqueException;
import ch.admin.seco.jobroom.security.saml.utils.IamService;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Service
@ConfigurationProperties(prefix = "security")
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private UserInfoRepository userInfoRepository;

    private CompanyRepository companyRepository;

    private OrganizationRepository organizationRepository;

    private UserRepository userRepository;

    private MailService mailService;

    private UidClient uidClient;

    private IamService iamService;

    private StesService stesService;

    private String accessCodeMailRecipient;

    private final Environment env;

    // access code size
    private static final int RANDOM_NUMBER_LENGTH = 5;
    private static final int ACCESS_CODE_LENGTH = 8;    // note that this number is related to the RANDOM_NUMBER_LENGTH

    @Autowired
    public RegistrationService(UserInfoRepository userInfoRepository, CompanyRepository companyRepository, OrganizationRepository organizationRepository, UserRepository userRepository, MailService mailService, UidClient uidClient, IamService iamService, StesService stesService, Environment env) {
        this.userInfoRepository = userInfoRepository;
        this.companyRepository = companyRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.uidClient = uidClient;
        this.iamService = iamService;
        this.stesService = stesService;
        this.env = env;
    }

    @Transactional
    public void insertNewJobseeker() throws UserAlreadyExistsException, NoValidPrincipalException, RoleCouldNotBeAddedException {
        EiamUserPrincipal principal = getPrincipalFromSession();
        UserInfo userInfo = principal.getUser();
        ensureUserIsNew(userInfo);
        addJobseekerRoleToEiam(principal);
        addJobseekerRoleToSession(principal);
        userInfo.setRegistrationStatus(RegistrationStatus.REGISTERED);
        this.userInfoRepository.save(userInfo);
    }

    @Transactional
    public void insertNewEmployer(long uid) throws NoValidPrincipalException, UserAlreadyExistsException, UidClientException, UidNotUniqueException, CompanyNotFoundException, PersistenceException {
        EiamUserPrincipal principal = getPrincipalFromSession();
        UserInfo user = principal.getUser();
        ensureUserIsNew(user);
        FirmData firm = this.uidClient.getCompanyByUid(uid);
        try {
            Company company = storeCompany(firm);
            user.addCompany(company);
            user.setAccessCode(createAccessCode());
            user.setRegistrationStatus(RegistrationStatus.VALIDATION_EMP);
            this.userInfoRepository.save(user);
        } catch (TransactionSystemException e) {
            log.error("The new employee with the userId=" + user.getId() + " could not be stored in the database.", e);
            throw new PersistenceException();
        }
        sendMailForServiceDesk(user);
    }

    @Transactional
    public void insertNewAgent(String avgId) throws NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, PersistenceException {
        EiamUserPrincipal principal = getPrincipalFromSession();
        UserInfo user = principal.getUser();
        ensureUserIsNew(user);
        Optional<Organization> avgCompany = this.organizationRepository.findByExternalId(avgId);
        if (!avgCompany.isPresent()) {
            //throw new CompanyNotFoundException();
            //TODO remove following lines; it's just for dev while we have an empty Organization table
            Organization organization = new Organization();
            organization.setId(UUID.randomUUID());
            organization.setExternalId("123456");
            organization.setName("Jobroom-Test AG");
            organization.setStreet("Marktgasse 111");
            organization.setZipCode("3011");
            organization.setCity("Bern");
            organization.setEmail("info@jobroom-test.ch");
            organization.setPhone("+41 31 123 45 67");
            organization.setActive(true);
            organization.setType(CompanyType.AVG);
            avgCompany = Optional.of(organization);
        }
        try {
            Company company = storeCompany(avgCompany.get());
            user.addCompany(company);
            user.setAccessCode(createAccessCode());
            user.setRegistrationStatus(RegistrationStatus.VALIDATION_PAV);
            this.userInfoRepository.save(user);
        } catch (TransactionSystemException e) {
            log.error("The new agent with the userId=" + user.getId() + " could not be stored in the database.", e);
            throw new PersistenceException();
        }
        sendMailForServiceDesk(user);
    }


    public boolean validatePersonNumber(RegisterJobseekerVM jobseekerDetails) throws StesServiceException {
        try {
            LocalDate birthdate = LocalDate.of(jobseekerDetails.getBirthdateYear(), jobseekerDetails.getBirthdateMonth(), jobseekerDetails.getBirthdateDay());
            StesVerificationRequest jobseekerRequestData = new StesVerificationRequest(jobseekerDetails.getPersonNumber(), birthdate);
            StesVerificationResult stesVerificationResult = this.stesService.verifyStesRegistrationData(jobseekerRequestData);
            return stesVerificationResult.isVerified();
        } catch (Exception e) {
            throw new StesServiceException(e);
        }
    }

    @Transactional
    public RegistrationResultDTO registerEmployerOrAgent() throws NoValidPrincipalException, RoleCouldNotBeAddedException {
        RegistrationResultDTO result = new RegistrationResultDTO(false, Constants.TYPE_UNKOWN);
        EiamUserPrincipal principal = getPrincipalFromSession();
        UserInfo userInfo = principal.getUser();
        RegistrationStatus registrationStatus = userInfo.getRegistrationStatus();
        if (registrationStatus.equals(RegistrationStatus.VALIDATION_EMP)) {
            result.setEmployerType();
            addCompanyRoleToEiam(principal);
            addCompanyRoleToSession(principal);
        } else if (registrationStatus.equals(RegistrationStatus.VALIDATION_PAV)) {
            result.setAgentType();
            addAgentRoleToEiam(principal);
            addAgentRoleToSession(principal);
        } else {
            throw new RoleCouldNotBeAddedException("User with id=" + userInfo.getId() + " tried to register as employer/agent, but has a wrong registration status: " + registrationStatus);
        }
        userInfo.setRegistrationStatus(RegistrationStatus.REGISTERED);
        this.userInfoRepository.save(userInfo);
        result.setSuccess(true);
        return result;
    }

    @Transactional
    public void registerExistingAgent() throws NoValidPrincipalException, RoleCouldNotBeAddedException {
        EiamUserPrincipal principal = getPrincipalFromSession();
        addAgentRoleToEiam(principal);
        addAgentRoleToSession(principal);
        this.userInfoRepository.save(principal.getUser());
    }

    public FirmData getCompanyByUid(long uid) throws UidClientException, UidNotUniqueException, CompanyNotFoundException {
        return this.uidClient.getCompanyByUid(uid);
    }

    public boolean validateOldLogin(String username, String password) {
        Assert.notNull(username, "A username must be provided.");
        Assert.notNull(password, "A password must be provided.");
        Optional<User> oldUser = this.userRepository.findOneWithAuthoritiesByLogin(username);
        if (!oldUser.isPresent()) {
            return false;
        }
        MD5PasswordEncoder md5PasswordEncoder = new MD5PasswordEncoder();
        return md5PasswordEncoder.matches(password, oldUser.get().getPassword());
    }

    private void ensureUserIsNew(UserInfo user) throws UserAlreadyExistsException {
        Optional<UserInfo> dbUser = userInfoRepository.findOneByUserExternalId(user.getUserExternalId());
        if (dbUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
    }

    /**
     * Makes sure, that the company exists in the database and return the company object.
     * If the company exists alread, it is just returned, otherwise it is inserted into
     * the database and then returned.
     *
     * @param firm UID register firm data
     * @return either the inserted or the already existing company from the database (Company table)
     */
    private Company storeCompany(FirmData firm) {
        Optional<Company> companyInDb = this.companyRepository.findByExternalId(getFullUid(firm.getUidPrefix(), firm.getUid()));
        if (companyInDb.isPresent()) {
            return companyInDb.get();
        } else {
            Company company = toCompany(firm);
            return this.companyRepository.save(company);
        }
    }

    /**
     * Makes sure, that the company exists in the database and return the company object.
     * If the company exists alread, it is just returned, otherwise it is inserted into
     * the database and then returned.
     *
     * @param organization AVG organisation read from the Organization database table
     * @return either the inserted or the already existing company from the database (Company table)
     */
    private Company storeCompany(Organization organization) {
        Optional<Company> companyInDb = this.companyRepository.findByExternalId(organization.getExternalId());
        if (companyInDb.isPresent()) {
            return companyInDb.get();
        } else {
            Company company = toCompany(organization);
            return this.companyRepository.save(company);
        }
    }

    /**
     * Adding the role to the eIAM stores the role permanently for the user.
     *
     * @param principal session principal contains the necessary information to store the role in eIAM
     * @throws RoleCouldNotBeAddedException something went wrong during the eIAM webservice call (user should probably try later again)
     */
    private void addJobseekerRoleToEiam(EiamUserPrincipal principal) throws RoleCouldNotBeAddedException {
        this.iamService.addJobSeekerRoleToUser(principal.getUser().getUserExternalId(), principal.getUserDefaultProfileExtId());
    }

    private void addCompanyRoleToEiam(EiamUserPrincipal principal) throws RoleCouldNotBeAddedException {
        this.iamService.addCompanyRoleToUser(principal.getUser().getUserExternalId(), principal.getUserDefaultProfileExtId());
    }

    private void addAgentRoleToEiam(EiamUserPrincipal principal) throws RoleCouldNotBeAddedException {
        this.iamService.addAgentRoleToUser(principal.getUser().getUserExternalId(), principal.getUserDefaultProfileExtId());
    }

    @Transactional
    void updateRegistrationStatus(UserInfo userInfo, RegistrationStatus status) {
        if (userInfo.getId() == null) {
            throw new IllegalArgumentException("The registration status can only be changed on an existing user.");
        }
        if (allowedTransition(userInfo.getRegistrationStatus(), status)) {
            userInfo.setRegistrationStatus(status);
            this.userInfoRepository.save(userInfo);
        } else {
            log.error("The transition of the registration status from {} to {} is not allowed", userInfo.getRegistrationStatus().name(), status.name());
            throw new IllegalArgumentException("Not allowed registration status transition");
        }
    }

    /**
     * Adding the role to the session saves the user another re-login after the
     * registration process is completed.
     *
     * @param principal session principal to which the role is added
     */
    private void addJobseekerRoleToSession(EiamUserPrincipal principal) {
        addRoleToSession(principal, AuthoritiesConstants.ROLE_JOBSEEKER_CLIENT);
    }

    private void addCompanyRoleToSession(EiamUserPrincipal principal) {
        addRoleToSession(principal, AuthoritiesConstants.ROLE_PRIVATE_EMPLOYMENT_AGENT);
    }

    private void addAgentRoleToSession(EiamUserPrincipal principal) {
        addRoleToSession(principal, AuthoritiesConstants.ROLE_COMPANY);
    }

    private void addRoleToSession(EiamUserPrincipal principal, String role) {
        SimpleGrantedAuthority newGrantedAuthority = new SimpleGrantedAuthority(role);
        principal.addAuthority(newGrantedAuthority);
        // because the authorities collection in authentication is immutable, we have to make a new one
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        authorities.add(newGrantedAuthority);
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials(), authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    private EiamUserPrincipal getPrincipalFromSession() throws NoValidPrincipalException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new NoValidPrincipalException("No principal was found in the user's session. The user is probably not authenticated.");
        }
        if (!(authentication.getPrincipal() instanceof EiamUserPrincipal)) {
            // Workaround for local development
            if (noEiamProfileActive() && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                return toEiamUserPrincipal(userDetails);
            }
            throw new NoValidPrincipalException("The principal found in the user's session is not of type EiamUserPrincipal.");
        }
        EiamUserPrincipal principal = ((EiamUserPrincipal) authentication.getPrincipal());
        if (principal.getUser() == null || StringUtils.isEmpty(principal.getUser().getUserExternalId()) || StringUtils.isEmpty(principal.getUserDefaultProfileExtId())) {
            throw new NoValidPrincipalException("The principal found in the user's session does not contain the necessary information.");
        }
        return principal;
    }

    @Transactional
    void setCompanyForUser(UserInfo user, FirmData firmData) throws CompanySelectionFailedException {
        String uid = getFullUid(firmData.getUidPrefix(), firmData.getUid());
        Optional<Company> existingCompany = this.companyRepository.findByExternalId(uid);
        Company usersCompany;
        if (existingCompany.isPresent()) {
            usersCompany = existingCompany.get();
        } else {
            try {
                FirmData foundFirm = this.uidClient.getCompanyByUid(firmData.getUid());
                usersCompany = toCompany(foundFirm);
            } catch (CompanyNotFoundException | UidNotUniqueException | UidClientException e) {
                log.error("An exception occurred while the user with extId={} tried to select the company with uid={}", e);
                throw new CompanySelectionFailedException();
            }
        }
        this.companyRepository.save(usersCompany);
        user.addCompany(usersCompany);
        this.userInfoRepository.save(user);
    }

    private Company toCompany(FirmData firmData) {
        String uid = getFullUid(firmData.getUidPrefix(), firmData.getUid());
        Company company = new Company(firmData.getName(), uid);
        company.setStreet(getStreetRepresentation(firmData));
        company.setZipCode(firmData.getAddress().getZip());
        company.setCity(firmData.getAddress().getCity());
        company.setSource(CompanySource.UID);
        return company;
    }

    private Company toCompany(Organization organization) {
        Company company = new Company(organization.getName(), organization.getExternalId());
        company.setStreet(organization.getStreet());
        company.setZipCode(organization.getZipCode());
        company.setCity(organization.getCity());
        company.setSource(CompanySource.AVG);
        return company;
    }

    private String getStreetRepresentation(FirmData firmData) {
        return firmData.getAddress().getStreet() + " " + firmData.getAddress().getStreetAddOn() + " " + firmData.getAddress().getBuildingNum();
    }

    private void sendMailForServiceDesk(UserInfo userInfo) {
        Assert.notNull(userInfo.getAccessCode(), "The user's accesscode must be set in order to send access code mail.");
        mailService.sendAccessCodeLetterMail(accessCodeMailRecipient, userInfo);
    }

    private boolean allowedTransition(RegistrationStatus oldStatus, RegistrationStatus newStatus) {
        if (oldStatus == null) {
            return newStatus.equals(RegistrationStatus.VALIDATION_EMP) || newStatus.equals(RegistrationStatus.VALIDATION_PAV);
        } else {
            return (oldStatus.equals(RegistrationStatus.UNREGISTERED) && newStatus.equals(RegistrationStatus.VALIDATION_EMP)
                || oldStatus.equals(RegistrationStatus.UNREGISTERED) && newStatus.equals(RegistrationStatus.VALIDATION_PAV)
                || oldStatus.equals(RegistrationStatus.VALIDATION_EMP) && newStatus.equals(RegistrationStatus.REGISTERED)
                || oldStatus.equals(RegistrationStatus.VALIDATION_PAV) && newStatus.equals(RegistrationStatus.REGISTERED));
        }
    }

    String getFullUid(String uidPrefix, int uid) {
        return uidPrefix + "-" + String.valueOf(uid);
    }

    String createAccessCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[RANDOM_NUMBER_LENGTH];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public boolean validateAccessCode(String accessCode) throws NoValidPrincipalException {
        Assert.notNull(accessCode, "An access code must be provided.");
        Assert.isTrue(accessCode.length() == ACCESS_CODE_LENGTH, "The access code has an invalid length.");
        EiamUserPrincipal principal = getPrincipalFromSession();
        UserInfo user = principal.getUser();
        String storedAccessCode = user.getAccessCode();
        if (StringUtils.isEmpty(storedAccessCode)) {
            throw new IllegalArgumentException("User with extId=" + user.getUserExternalId() + " has no access code stored in the database");
        }
        return accessCode.equals(storedAccessCode);
    }

    public void setAccessCodeMailRecipient(String accessCodeMailRecipient) {
        this.accessCodeMailRecipient = accessCodeMailRecipient;
    }

    private EiamUserPrincipal toEiamUserPrincipal(org.springframework.security.core.userdetails.User userDetails) {
        Optional<User> userFromDb = this.userRepository.findOneWithAuthoritiesByLogin(userDetails.getUsername());
        EiamUserPrincipal principal = new EiamUserPrincipal();
        UserInfo userInfo = new UserInfo();
        if (userFromDb.isPresent()) {
            User user = userFromDb.get();
            userInfo.setUserExternalId(RandomStringUtils.randomAlphanumeric(10));   // allows to register the user over and over
            userInfo.setFirstName(user.getFirstName());
            userInfo.setLastName(user.getFirstName());
            userInfo.setId(user.getId());
            userInfo.setLangKey(user.getLangKey());
            userInfo.setPhone(user.getPhone());
            userInfo.setEmail(user.getEmail());
        }
        userInfo.setRegistrationStatus(RegistrationStatus.REGISTERED);
        userInfo.setAccessCode("1234ABCD");
        principal.setUser(userInfo);
        principal.setAuthorities(new ArrayList<>(userDetails.getAuthorities()));
        principal.setNeedsRegistration(false);
        principal.setUserDefaultProfileExtId("999");
        return principal;
    }

    private boolean noEiamProfileActive() {
        String[] activeProfiles = env.getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch(profile -> (profile.equals("no-eiam")));
    }
}

package ch.admin.seco.jobroom.security.registration;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.*;
import ch.admin.seco.jobroom.domain.enumeration.CompanySource;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.repository.CompanyRepository;
import ch.admin.seco.jobroom.repository.OrganizationRepository;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.repository.UserRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.UserPrincipal;
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
import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

import static ch.admin.seco.jobroom.domain.UserInfo_.registrationStatus;

@Service
@ConfigurationProperties(prefix = "security")
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final UserInfoRepository userInfoRepository;

    private final CompanyRepository companyRepository;

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    private final MailService mailService;

    private final UidClient uidClient;

    private final IamService iamService;

    private final StesService stesService;

    private final CurrentUserService currentUserService;

    private String accessCodeMailRecipient;

    // access code size
    private static final int ACCESS_CODE_LENGTH = 8;

    @Autowired
    public RegistrationService(UserInfoRepository userInfoRepository, CompanyRepository companyRepository, OrganizationRepository organizationRepository, UserRepository userRepository, MailService mailService, UidClient uidClient, IamService iamService, StesService stesService, CurrentUserService currentUserService) {
        this.userInfoRepository = userInfoRepository;
        this.companyRepository = companyRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.uidClient = uidClient;
        this.iamService = iamService;
        this.stesService = stesService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public void registerAsJobSeeker(LocalDate birthdate, Long personNumber) throws InvalidPersonenNumberException, RoleCouldNotBeAddedException, StesServiceException {
        if (!this.validatePersonNumber(birthdate, personNumber)) {
            throw new InvalidPersonenNumberException(personNumber, birthdate);
        }
        UserPrincipal principal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(principal.getId());
        addJobseekerRoleToEiam(principal);
        addJobseekerRoleToSession();
        userInfo.closeRegistration();
    }

    @Transactional
    public void requestAccessAsEmployer(Long uid) throws UidClientException, UidNotUniqueException, CompanyNotFoundException {
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        FirmData firmData = this.uidClient.getCompanyByUid(uid);
        Company company = storeCompany(firmData);
        userInfo.requestAccessAsEmployer(company);
        sendMailForServiceDesk(userInfo);
    }

    @Transactional
    public void requestAccessAsAgent(String avgId) throws CompanyNotFoundException {
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        Optional<Organization> avgCompany = this.organizationRepository.findByExternalId(avgId);
        if (!avgCompany.isPresent()) {
            throw new CompanyNotFoundException();
        }
        Company company = storeCompany(avgCompany.get());
        userInfo.requestAccessAsAgent(company);
        sendMailForServiceDesk(userInfo);
    }

    @Transactional
    public RegistrationResultDTO registerAsEmployerOrAgent(String accessCode) throws RoleCouldNotBeAddedException, InvalidAccessCodeException {
        boolean isValid = this.validateAccessCode(accessCode);
        if (!isValid) {
            throw new InvalidAccessCodeException();
        }
        RegistrationResultDTO result = new RegistrationResultDTO(false, Constants.TYPE_UNKOWN);
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        if (RegistrationStatus.VALIDATION_EMP.equals(userInfo.getRegistrationStatus())) {
            result.setEmployerType();
            addCompanyRoleToEiam(userPrincipal);
            addCompanyRoleToSession();
        } else if (RegistrationStatus.VALIDATION_PAV.equals(userInfo.getRegistrationStatus())) {
            result.setAgentType();
            addAgentRoleToEiam(userPrincipal);
            addAgentRoleToSession();
        } else {
            throw new RoleCouldNotBeAddedException("User with id=" + userPrincipal.getUserExtId() + " tried to register as employer/agent, but has a wrong registration status: " + registrationStatus);
        }
        result.setSuccess(true);
        userInfo.closeRegistration();
        return result;
    }

    @Transactional
    public void registerExistingAgent(String username, String password) throws RoleCouldNotBeAddedException, InvalidOldLoginException {
        if (!validateOldLogin(username, password)) {
            throw new InvalidOldLoginException();
        }
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        addAgentRoleToEiam(userPrincipal);
        addAgentRoleToSession();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        Optional<User> oldUser = this.userRepository.findOneWithAuthoritiesByLogin(username);
        Organization avgCompany = oldUser.get().getOrganization();
        Company company = storeCompany(avgCompany);
        userInfo.registerExistingAgent(company);
    }

    private boolean validateOldLogin(String username, String password) {
        Assert.notNull(username, "A username must be provided.");
        Assert.notNull(password, "A password must be provided.");
        Optional<User> oldUser = this.userRepository.findOneWithAuthoritiesByLogin(username);
        if (!oldUser.isPresent()) {
            return false;
        }
        MD5PasswordEncoder md5PasswordEncoder = new MD5PasswordEncoder();
        return md5PasswordEncoder.matches(password, oldUser.get().getPassword());
    }

    @Transactional
    public FirmData getCompanyByUid(long uid) throws UidClientException, UidNotUniqueException, CompanyNotFoundException {
        return this.uidClient.getCompanyByUid(uid);
    }

    private UserInfo getUserInfo(UserInfoId userInfoId) {
        Optional<UserInfo> userInfo = this.userInfoRepository.findById(userInfoId);
        if (!userInfo.isPresent()) {
            throw new IllegalStateException("User was not found in");
        }
        return userInfo.get();
    }

    private boolean validatePersonNumber(LocalDate birthdate, Long personNumber) throws StesServiceException {
        StesVerificationRequest jobseekerRequestData = new StesVerificationRequest(personNumber, birthdate);
        try {
            StesVerificationResult stesVerificationResult = this.stesService.verifyStesRegistrationData(jobseekerRequestData);
            return stesVerificationResult.isVerified();
        } catch (Exception e) {
            throw new StesServiceException(e);
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

    private void addJobseekerRoleToEiam(UserPrincipal userPrincipal) throws RoleCouldNotBeAddedException {
        this.iamService.addJobSeekerRoleToUser(userPrincipal.getUserExtId(), userPrincipal.getUserDefaultProfileExtId());
    }

    private void addCompanyRoleToEiam(UserPrincipal userPrincipal) throws RoleCouldNotBeAddedException {
        this.iamService.addCompanyRoleToUser(userPrincipal.getUserExtId(), userPrincipal.getUserDefaultProfileExtId());
    }

    private void addAgentRoleToEiam(UserPrincipal userPrincipal) throws RoleCouldNotBeAddedException {
        this.iamService.addAgentRoleToUser(userPrincipal.getUserExtId(), userPrincipal.getUserDefaultProfileExtId());
    }

    private void addJobseekerRoleToSession() {
        this.currentUserService.addRoleToSession(AuthoritiesConstants.ROLE_JOBSEEKER_CLIENT);
    }

    private void addCompanyRoleToSession() {
        this.currentUserService.addRoleToSession(AuthoritiesConstants.ROLE_COMPANY);
    }

    private void addAgentRoleToSession() {
        this.currentUserService.addRoleToSession(AuthoritiesConstants.ROLE_PRIVATE_EMPLOYMENT_AGENT);
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

    private String getFullUid(String uidPrefix, int uid) {
        return uidPrefix + "-" + String.valueOf(uid);
    }

    private boolean validateAccessCode(String accessCode) {
        Assert.notNull(accessCode, "An access code must be provided.");
        Assert.isTrue(accessCode.length() == ACCESS_CODE_LENGTH, "The access code has an invalid length.");
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        String storedAccessCode = userInfo.getAccessCode();
        if (StringUtils.isEmpty(storedAccessCode)) {
            throw new IllegalArgumentException("User with extId=" + userInfo.getUserExternalId() + " has no access code stored in the database");
        }
        return accessCode.equals(storedAccessCode);
    }

    public void setAccessCodeMailRecipient(String accessCodeMailRecipient) {
        this.accessCodeMailRecipient = accessCodeMailRecipient;
    }

}

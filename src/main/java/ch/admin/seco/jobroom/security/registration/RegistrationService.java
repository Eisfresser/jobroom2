package ch.admin.seco.jobroom.security.registration;

import static ch.admin.seco.jobroom.domain.UserInfo_.registrationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.Organization;
import ch.admin.seco.jobroom.domain.StesInformation;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.domain.enumeration.CompanySource;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.repository.CompanyRepository;
import ch.admin.seco.jobroom.repository.OrganizationRepository;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.repository.UserRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.security.registration.eiam.EiamAdminService;
import ch.admin.seco.jobroom.security.registration.eiam.EiamClientRole;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.security.registration.uid.FirmData;
import ch.admin.seco.jobroom.security.registration.uid.UidClient;
import ch.admin.seco.jobroom.security.registration.uid.UidCompanyNotFoundException;
import ch.admin.seco.jobroom.service.CandidateService;
import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.UserInfoNotFoundException;
import ch.admin.seco.jobroom.service.dto.AccountabilityDTO;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import ch.admin.seco.jobroom.service.dto.StesInformationDto;
import ch.admin.seco.jobroom.service.dto.StesVerificationRequest;
import ch.admin.seco.jobroom.service.dto.StesVerificationResult;
import ch.admin.seco.jobroom.service.dto.UserInfoDTO;

@Service
@ConfigurationProperties(prefix = "security")
@Transactional(rollbackFor = Exception.class)
public class RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);

    private final UserInfoRepository userInfoRepository;

    private final CompanyRepository companyRepository;

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    private final MailService mailService;

    private final UidClient uidClient;

    private final EiamAdminService eiamAdminService;

    private final CandidateService candidateService;

    private final CurrentUserService currentUserService;

    private String accessCodeMailRecipient;

    public RegistrationService(UserInfoRepository userInfoRepository, CompanyRepository companyRepository, OrganizationRepository organizationRepository, UserRepository userRepository, MailService mailService, UidClient uidClient, EiamAdminService eiamAdminService, CandidateService candidateService, CurrentUserService currentUserService) {
        this.userInfoRepository = userInfoRepository;
        this.companyRepository = companyRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.uidClient = uidClient;
        this.eiamAdminService = eiamAdminService;
        this.candidateService = candidateService;
        this.currentUserService = currentUserService;
    }

    public void registerAsJobSeeker(LocalDate birthdate, Long personNumber) throws InvalidPersonenNumberException, StesPersonNumberAlreadyTaken {
        if (!this.validatePersonNumber(birthdate, personNumber)) {
            throw new InvalidPersonenNumberException(personNumber, birthdate);
        }
        if (isPersonNumberAlreadyTaken(personNumber)) {
            throw new StesPersonNumberAlreadyTaken(personNumber);
        }
        UserPrincipal principal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(principal.getId());
        addJobseekerRoleToEiam(principal);
        addJobseekerRoleToSession();
        userInfo.registerAsJobSeeker(personNumber);
        LOGGER.info("Registered user with id: {} as job-seeker", userInfo.getUserExternalId());
    }


    public void requestAccessAsEmployer(Long uid) throws UidCompanyNotFoundException {
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        FirmData firmData = this.uidClient.getCompanyByUid(uid);
        Company company = storeCompany(firmData);
        userInfo.requestAccessAsEmployer(company);
        sendMailForServiceDesk(userInfo);
    }

    public void requestAccessAsAgent(String avgId) throws AvgNotFoundException {
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        Optional<Organization> avgOrganization = this.organizationRepository.findByExternalId(avgId);
        if (!avgOrganization.isPresent()) {
            throw new AvgNotFoundException(avgId);
        }
        Company company = storeCompany(avgOrganization.get());
        userInfo.requestAccessAsAgent(company);
        sendMailForServiceDesk(userInfo);
    }

    public RegistrationResultDTO registerAsEmployerOrAgent(String accessCode) throws InvalidAccessCodeException {
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
            LOGGER.info("Registered user with id: {} as company user", userInfo.getUserExternalId());
        } else if (RegistrationStatus.VALIDATION_PAV.equals(userInfo.getRegistrationStatus())) {
            result.setAgentType();
            addAgentRoleToEiam(userPrincipal);
            addAgentRoleToSession();
            LOGGER.info("Registered user with id: {} as pav user", userInfo.getUserExternalId());
        } else {
            throw new IllegalStateException("User with id=" + userPrincipal.getUserExtId() + " tried to register as employer/agent, but has a wrong registration status: " + registrationStatus);
        }
        result.setSuccess(true);
        userInfo.finishRegistration();
        return result;
    }

    public void registerExistingAgent(String username, String password) throws InvalidOldLoginException {
        if (!validateOldLogin(username, password)) {
            throw new InvalidOldLoginException();
        }
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        Optional<User> oldUser = this.userRepository.findOneWithAuthoritiesByLogin(username);
        if (!oldUser.isPresent()) {
            throw new IllegalStateException("Existing User " + username + " was not found");
        }
        Organization avgCompany = oldUser.get().getOrganization();
        if (avgCompany == null) {
            throw new IllegalStateException("Existing User " + username + " has no organization");
        }
        Company company = storeCompany(avgCompany);
        userInfo.registerExistingAgent(company);
        addAgentRoleToEiam(userPrincipal);
        addAgentRoleToSession();
    }

    public FirmData getCompanyByUid(long uid) throws UidCompanyNotFoundException {
        return this.uidClient.getCompanyByUid(uid);
    }

    public void setAccessCodeMailRecipient(String accessCodeMailRecipient) {
        this.accessCodeMailRecipient = accessCodeMailRecipient;
    }

    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public void unregisterJobSeeker(String eMail) throws UserNotFoundException {
        doUnregister(eMail, EiamClientRole.ROLE_JOBSEEKER);
    }

    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public void unregisterPrivateAgent(String eMail) throws UserNotFoundException {
        doUnregister(eMail, EiamClientRole.ROLE_PRIVATE_EMPLOYMENT_AGENT);
    }

    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public void unregisterCompany(String eMail) throws UserNotFoundException {
        doUnregister(eMail, EiamClientRole.ROLE_COMPANY);
    }

    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public UserInfoDTO getUserInfo(String eMail) throws UserInfoNotFoundException {
        Optional<UserInfo> userInfo = this.userInfoRepository.findByEMail(eMail);
        return userInfo.map(RegistrationService::toUserInfoDTO)
            .orElseThrow(() -> new UserInfoNotFoundException(eMail));
    }

    private static UserInfoDTO toUserInfoDTO(UserInfo userInfo) {
        return new UserInfoDTO.Builder()
            .setId(userInfo.getId().getValue())
            .setUserExternalId(userInfo.getUserExternalId())
            .setFirstName(userInfo.getFirstName())
            .setLastName(userInfo.getLastName())
            .setEmail(userInfo.getEmail())
            .setRegistrationStatus(userInfo.getRegistrationStatus())
            .setAccountabilities(toAccountabilityDTOs(userInfo))
            .setStesInformation(userInfo.getStesInformation()
                .map(RegistrationService::toStesInformationDto)
                .orElse(null)
            )
            .setCreatedAt(userInfo.getCreatedAt())
            .setModifiedAt(userInfo.getModifiedAt())
            .setLastLoginAt(userInfo.getLastLoginAt())
            .build();
    }

    private static StesInformationDto toStesInformationDto(StesInformation stesInformation) {
        return new StesInformationDto(
            stesInformation.getPersonNumber(),
            stesInformation.getVerificationType(),
            stesInformation.getVerifiedAt()
        );
    }

    private static List<AccountabilityDTO> toAccountabilityDTOs(UserInfo userInfo) {
        return userInfo.getAccountabilities().stream()
            .map(accountability -> new AccountabilityDTO(
                accountability.getType(),
                accountability.getCompany().getName(),
                accountability.getCompany().getExternalId(),
                accountability.getCompany().getSource()
            ))
            .collect(Collectors.toList());
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

    private UserInfo getUserInfo(UserInfoId userInfoId) {
        Optional<UserInfo> userInfo = this.userInfoRepository.findById(userInfoId);
        if (!userInfo.isPresent()) {
            throw new IllegalStateException("User was not found in");
        }
        return userInfo.get();
    }

    private boolean validatePersonNumber(LocalDate birthdate, Long personNumber) {
        StesVerificationRequest jobseekerRequestData = new StesVerificationRequest(personNumber, birthdate);
        StesVerificationResult stesVerificationResult = this.candidateService.verifyStesRegistrationData(jobseekerRequestData);
        return stesVerificationResult.isVerified();
    }

    private boolean isPersonNumberAlreadyTaken(Long personNumber) {
        return this.userInfoRepository.findByPersonNumber(personNumber).isPresent();
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

    private void addJobseekerRoleToEiam(UserPrincipal userPrincipal) {
        this.eiamAdminService.addRole(userPrincipal.getUserExtId(), userPrincipal.getUserDefaultProfileExtId(), EiamClientRole.ROLE_JOBSEEKER);
    }

    private void addCompanyRoleToEiam(UserPrincipal userPrincipal) {
        this.eiamAdminService.addRole(userPrincipal.getUserExtId(), userPrincipal.getUserDefaultProfileExtId(), EiamClientRole.ROLE_COMPANY);
    }

    private void addAgentRoleToEiam(UserPrincipal userPrincipal) {
        this.eiamAdminService.addRole(userPrincipal.getUserExtId(), userPrincipal.getUserDefaultProfileExtId(), EiamClientRole.ROLE_PRIVATE_EMPLOYMENT_AGENT);
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
        String streetRepresentation = firmData.getAddress().getStreet();
        if (firmData.getAddress().getStreetAddOn() != null) {
            streetRepresentation += " " + firmData.getAddress().getStreetAddOn();
        }
        if (firmData.getAddress().getBuildingNum() != null) {
            streetRepresentation += " " + firmData.getAddress().getBuildingNum();
        }
        return streetRepresentation;
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
        UserPrincipal userPrincipal = this.currentUserService.getPrincipal();
        UserInfo userInfo = getUserInfo(userPrincipal.getId());
        String storedAccessCode = userInfo.getAccessCode();
        if (StringUtils.isEmpty(storedAccessCode)) {
            throw new IllegalArgumentException("User with extId=" + userInfo.getUserExternalId() + " has no access code stored in the database");
        }
        return accessCode.equals(storedAccessCode);
    }

    private void doUnregister(String eMail, EiamClientRole role) throws UserNotFoundException {
        Optional<UserInfo> userInfoByMail = userInfoRepository.findByEMail(eMail);
        UserInfo userInfo = userInfoByMail.get();
        userInfo.unregister();
        this.eiamAdminService.removeRole(userInfo.getUserExternalId(), role);
    }

}

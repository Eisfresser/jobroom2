package ch.admin.seco.jobroom.domain;

import ch.admin.seco.jobroom.domain.enumeration.AccountabilityType;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.service.CompanyContactTemplateNotFoundException;
import com.google.common.base.Preconditions;
import org.apache.commons.codec.binary.Base32;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus.UNREGISTERED;

/**
 * Additional information about a user. The eIAM is master, which means users are registered
 * and managed in the eIAM. Every entry of this table in the Jobroom database relates to
 * an eIAM user through the userExternalId. This allows for additional user attributes not
 * existing in the eIAM and mainly to keep the relationship to an organisation.
 * Note: the entity contains no authorization related information, since that is handled
 * by the eIAM.
 */
@Entity
@Table(name = "user_info")
public class UserInfo implements Serializable {

    private static final int RANDOM_NUMBER_LENGTH = 5;

    private static final long serialVersionUID = 1L;

    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDate.of(2018, 7, 1).atTime(LocalTime.NOON);

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    @Valid
    private UserInfoId id;

    @Size(max = 10)
    @Column(name = "external_id", length = 10)
    private String userExternalId;

    @NotNull
    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @NotNull
    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Size(min = 2, max = 6)
    @Column(name = "lang_key", length = 6)
    private String langKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", length = 15)
    @NotNull
    private RegistrationStatus registrationStatus;

    @Column(name = "access_code", length = 15)
    private String accessCode;

    @ElementCollection
    @CollectionTable(name = "accountability", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "accountability_id")
    private Set<Accountability> accountabilities = new HashSet<>();

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "personNumber", column = @Column(name = "stes_person_number")),
        @AttributeOverride(name = "verificationType", column = @Column(name = "stes_verification_type")),
        @AttributeOverride(name = "verifiedAt", column = @Column(name = "stes_verified_at"))
    })
    @Valid
    private StesInformation stesInformation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "legal_terms_accepted_at")
    private LocalDateTime legalTermsAcceptedAt;

    @Valid
    @ElementCollection
    @CollectionTable(name = "company_contact_templates", joinColumns = @JoinColumn(name = "user_id"))
    private Set<CompanyContactTemplate> companyContactTemplates = new HashSet<>();

    public UserInfo(String firstName, String lastName, String email, String userExternalId, String langKey) {
        this.id = new UserInfoId();
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        this.userExternalId = Preconditions.checkNotNull(userExternalId);
        this.langKey = langKey;
        this.registrationStatus = UNREGISTERED;
        this.createdAt = LocalDateTime.now();
        this.lastLoginAt = LocalDateTime.now();
        this.touch();
    }

    private UserInfo() {
        // FOR JPA
    }

    public void loginWithUpdate(String firstName, String lastName, String email, String langKey) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        this.langKey = langKey;
        this.lastLoginAt = LocalDateTime.now();
        this.touch();
    }


    public void registerAsJobSeeker(Long personNumber) {
        this.stesInformation = new StesInformation(personNumber, StesVerificationType.SIMPLE);
        this.finishRegistration();
    }

    public void finishRegistration() {
        this.changeRegistrationStatus(RegistrationStatus.REGISTERED);
        this.accessCode = null;
        this.acceptLegalTerms();
        this.touch();
    }


    public void requestAccessAsEmployer(Company company) {
        this.addCompany(company);
        this.setAccessCode(createAccessCode());
        this.changeRegistrationStatus(RegistrationStatus.VALIDATION_EMP);
        this.touch();
    }

    public void requestAccessAsAgent(Company company) {
        this.addCompany(company);
        this.setAccessCode(createAccessCode());
        this.changeRegistrationStatus(RegistrationStatus.VALIDATION_PAV);
        this.touch();
    }

    public void unregister() {
        this.changeRegistrationStatus(UNREGISTERED);
        this.accountabilities.clear();
        this.companyContactTemplates.clear();
        this.stesInformation = null;
        this.touch();
    }

    /**
     * Method will be deprecated by feature JR2-1216.
     * @deprecated will be deprecated since it will be possible to get multipleCompanies
     * @return A Company Object
     */
    @Deprecated
    public Company getCompany() {
        if (accountabilities.isEmpty()) {
            return null;
        }
        return accountabilities.stream()
            .filter(accountability -> accountability.getType().equals(AccountabilityType.USER))
            .findFirst()
            .map((Accountability::getCompany))
            .orElseThrow(() -> new IllegalStateException("No accountabilites with a company found for user: " + this.id));
    }

    public void addCompanyContactTemplate(CompanyContactTemplate companyContactTemplate) {
        if (!this.hasAccountability(companyContactTemplate.getCompanyId())) {
            throw new IllegalStateException("No Accountability for company with id: " + companyContactTemplate.getCompanyId());
        }
        this.companyContactTemplates.remove(companyContactTemplate);
        this.companyContactTemplates.add(companyContactTemplate);
        this.touch();
    }

    public void removeContactTemplate(CompanyId companyId) {
        if (!this.hasAccountability(companyId)) {
            throw new IllegalStateException("No Accountability for company with id: " + companyId);
        }
        this.companyContactTemplates.removeIf(contactTemplate -> contactTemplate.getCompanyId().equals(companyId));
    }

    public boolean hasAccountability(CompanyId companyId) {
        return this.accountabilities.stream()
            .anyMatch(accountability -> accountability.getCompany().getId().equals(companyId));
    }

    public CompanyContactTemplate getCompanyContactTemplate(CompanyId companyId) throws CompanyContactTemplateNotFoundException {
        if (!this.hasAccountability(companyId)) {
            throw new IllegalStateException("No Accountability for company with id: " + companyId);
        }
        return this.companyContactTemplates.stream().
            filter(contactTemplate -> contactTemplate.getCompanyId().equals(companyId))
            .findAny()
            .orElseThrow(() -> new CompanyContactTemplateNotFoundException(companyId));
    }

    public Set<CompanyContactTemplate> getCompanyContactTemplates() {
        return Collections.unmodifiableSet(this.companyContactTemplates);
    }

    public Optional<StesInformation> getStesInformation() {
        return Optional.ofNullable(this.stesInformation);
    }

    public UserInfoId getId() {
        return id;
    }

    public String getUserExternalId() {
        return userExternalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public Set<Accountability> getAccountabilities() {
        return Collections.unmodifiableSet(this.accountabilities);
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getLegalTermsAcceptedAt() {
        return legalTermsAcceptedAt;
    }

    public void acceptLegalTerms() {
        this.legalTermsAcceptedAt = LocalDateTime.now();
    }

    public boolean isLatestLegalTermsAccepted(LocalDate legalTermsEffectiveDate) {
        Assert.notNull(legalTermsEffectiveDate, "legalTermsEffectiveDate is required");

        if (legalTermsAcceptedAt != null) {
            return legalTermsAcceptedAt.isAfter(legalTermsEffectiveDate.atStartOfDay());
        }

        return false;
    }

    void addCompany(Company company) {
        Accountability accountability = new Accountability(AccountabilityType.USER, company);
        accountabilities.add(accountability);
        this.touch();
    }

    private String createAccessCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[RANDOM_NUMBER_LENGTH];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    private void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    private void setFirstName(String firstName) {
        this.firstName = Preconditions.checkNotNull(firstName);
    }

    private void setLastName(String lastName) {
        this.lastName = Preconditions.checkNotNull(lastName);
    }

    private void setEmail(String email) {
        this.email = Preconditions.checkNotNull(email).toLowerCase();
    }

    private void changeRegistrationStatus(RegistrationStatus registrationStatus) {
        Preconditions.checkArgument(this.registrationStatus.canChangeTo(registrationStatus),
            "Can not change RegistrationStatus from  " + this.registrationStatus + " to " + registrationStatus);
        this.registrationStatus = registrationStatus;
    }

    private void touch() {
        this.modifiedAt = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserInfo user = (UserInfo) o;
        return !(user.getId() == null || getId() == null) && Objects.equals(getId(), user.getId());
    }

    @Override
    public String toString() {
        return "User{" +
            "userExternalId='" + userExternalId + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", langKey='" + langKey + '\'' +
            ", accessCode='" + accessCode + '\'' +
            ", registrationStatus='" + (registrationStatus != null ? registrationStatus.name() : "null") + '\'' +
            "}";
    }
}

package ch.admin.seco.jobroom.domain;

import static ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus.UNREGISTERED;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.binary.Base32;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import ch.admin.seco.jobroom.domain.enumeration.AccountabilityType;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;

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
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserInfo implements Serializable {

    private static final int RANDOM_NUMBER_LENGTH = 5;

    private static final int ACCESS_CODE_LENGTH = 8; // note that this number is related to the RANDOM_NUMBER_LENGTH

    private static final long serialVersionUID = 1L;

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
    private RegistrationStatus registrationStatus;

    @Column(name = "access_code", length = 15)
    private String accessCode;

    @ElementCollection
    @CollectionTable(name = "accountability", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "accountability_id")
    private Set<Accountability> accountabilities = new HashSet<>();

    public UserInfo(String firstName, String lastName, String email, String userExternalId, String langKey) {
        this.id = new UserInfoId();
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        this.userExternalId = Preconditions.checkNotNull(userExternalId);
        this.langKey = langKey;
        this.registrationStatus = UNREGISTERED;
    }

    private UserInfo() {
        // FOR JPA
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

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public Set<Accountability> getAccountabilities() {
        return accountabilities;
    }

    public void update(String firstName, String lastName, String email, String langKey) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        this.langKey = langKey;
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

    /**
     * Get the user's company. Since through the multiple accountability, the user could be
     * related to multiple companies, this method returns the company referenced by the
     * first found accountability with type USER.
     *
     * @return the user's company (referenced by the first USER accountability); if no company is found the return value is <code>null</code>
     */
    public Company getCompany() {
        if (accountabilities.isEmpty()) {
            return null;
        }
        return accountabilities.stream().filter(accountability -> accountability.getType().equals(AccountabilityType.USER)).findFirst().get().getCompany();
    }

    public void addCompany(Company company) {
        Accountability accountability = new Accountability(AccountabilityType.USER, company);
        accountabilities.add(accountability);
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void closeRegistration() {
        this.changeRegistrationStatus(RegistrationStatus.REGISTERED);
        this.accessCode = null;
    }

    public void requestAccessAsEmployer(Company company) {
        this.addCompany(company);
        this.setAccessCode(createAccessCode());
        this.changeRegistrationStatus(RegistrationStatus.VALIDATION_EMP);
    }

    public void requestAccessAsAgent(Company company) {
        this.addCompany(company);
        this.setAccessCode(createAccessCode());
        this.changeRegistrationStatus(RegistrationStatus.VALIDATION_PAV);
    }

    public void registerExistingAgent(Company company) {
        this.addCompany(company);
        this.changeRegistrationStatus(RegistrationStatus.REGISTERED);
    }

    public void unregister() {
        this.registrationStatus = UNREGISTERED;
        this.accountabilities.clear();
    }

    private void changeRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    private String createAccessCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[RANDOM_NUMBER_LENGTH];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
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

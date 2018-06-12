package ch.admin.seco.jobroom.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

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
    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String phone;

    @Size(min = 2, max = 6)
    @Column(name = "lang_key", length = 6)
    private String langKey;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private RegistrationStatus registrationStatus;

    @Column(length = 15)
    private String accessCode;

    @ElementCollection
    @CollectionTable(name = "accountability", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "accountability_id")
    private Set<Accountability> accountabilities = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserExternalId() {
        return userExternalId;
    }

    public void setUserExternalId(String userExternalId) {
        this.userExternalId = userExternalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    /**
     * Get the user's company. Since through the multiple accountability, the user could be
     * related to multiple companies, this method returns the company referenced by the
     * first found accountability with type USER.
     * @return  the user's company (referenced by the first USER accountability); if no company is found the return value is <code>null</code>
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

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
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

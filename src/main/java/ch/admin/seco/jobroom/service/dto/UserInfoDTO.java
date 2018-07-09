package ch.admin.seco.jobroom.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;

public class UserInfoDTO {

    private String id;

    private String userExternalId;

    private String firstName;

    private String lastName;

    private String email;

    private RegistrationStatus registrationStatus;

    private List<AccountabilityDTO> accountabilities;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime lastLoginAt;

    public UserInfoDTO(String id, String userExternalId, String firstName, String lastName, String email, RegistrationStatus registrationStatus, List<AccountabilityDTO> accountabilities, LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.userExternalId = userExternalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationStatus = registrationStatus;
        this.accountabilities = accountabilities;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.lastLoginAt = lastLoginAt;
    }

    public String getId() {
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

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public List<AccountabilityDTO> getAccountabilities() {
        return accountabilities;
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
}

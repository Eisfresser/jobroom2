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

    private StesInformationDto stesInformation;

    public UserInfoDTO(Builder builder) {
        this.id = builder.id;
        this.userExternalId = builder.userExternalId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.registrationStatus = builder.registrationStatus;
        this.accountabilities = builder.accountabilities;
        this.stesInformation = builder.stesInformation;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
        this.lastLoginAt = builder.lastLoginAt;
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

    public StesInformationDto getStesInformation() {
        return stesInformation;
    }

    public static class Builder {
        private String id;
        private String userExternalId;
        private String firstName;
        private String lastName;
        private String email;
        private RegistrationStatus registrationStatus;
        private List<AccountabilityDTO> accountabilities;
        private StesInformationDto stesInformation;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private LocalDateTime lastLoginAt;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setUserExternalId(String userExternalId) {
            this.userExternalId = userExternalId;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setRegistrationStatus(RegistrationStatus registrationStatus) {
            this.registrationStatus = registrationStatus;
            return this;
        }

        public Builder setAccountabilities(List<AccountabilityDTO> accountabilities) {
            this.accountabilities = accountabilities;
            return this;
        }

        public Builder setStesInformation(StesInformationDto stesInformation) {
            this.stesInformation = stesInformation;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setModifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public Builder setLastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public UserInfoDTO build() {
            return new UserInfoDTO(this);
        }
    }
}

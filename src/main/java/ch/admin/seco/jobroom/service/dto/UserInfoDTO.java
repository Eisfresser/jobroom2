package ch.admin.seco.jobroom.service.dto;

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

    public UserInfoDTO(String id, String userExternalId, String firstName, String lastName, String email, RegistrationStatus registrationStatus, List<AccountabilityDTO> accountabilities) {
        this.id = id;
        this.userExternalId = userExternalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationStatus = registrationStatus;
        this.accountabilities = accountabilities;
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
}

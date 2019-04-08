package ch.admin.seco.jobroom.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import ch.admin.seco.jobroom.config.Constants;

/**
 * A DTO representing the result of an employer/agent registration.
 */
public class RegistrationResultDTO {

    @NotBlank
    private boolean success;

    @NotBlank
    @Pattern(regexp = Constants.USER_TYPE_REGEX)
    @Size(min = 5, max = 8)
    private String type;

    public RegistrationResultDTO() {
        // Empty constructor needed for Jackson.
    }

    public RegistrationResultDTO(boolean success, String type) {
        this.success = success;
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getType() {
        return type;
    }

    public void setEmployerType() {
        this.type = Constants.TYPE_EMPLOYER;
    }

    public void setAgentType() {
        this.type = Constants.TYPE_AGENT;
    }

    @Override
    public String toString() {
        return "RegistrationResultDTO{" +
            "success=" + success +
            ", type='" + type + '\'' +
            '}';
    }
}

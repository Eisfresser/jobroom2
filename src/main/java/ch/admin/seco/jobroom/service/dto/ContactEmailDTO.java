package ch.admin.seco.jobroom.service.dto;

import javax.validation.constraints.NotNull;

public class ContactEmailDTO {

    @NotNull
    private String email;

    public ContactEmailDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

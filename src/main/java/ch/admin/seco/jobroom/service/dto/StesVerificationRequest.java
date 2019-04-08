package ch.admin.seco.jobroom.service.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

public class StesVerificationRequest {

    @NotNull
    private Long personNumber;

    @NotNull
    private LocalDate birthdate;

    public StesVerificationRequest(Long personNumber, LocalDate birthdate) {
        this.personNumber = personNumber;
        this.birthdate = birthdate;
    }

    public Long getPersonNumber() {
        return personNumber;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    @Override
    public String toString() {
        return "JobseekerRequestData{" +
            "personNumber=" + personNumber +
            ", birthdate=" + birthdate +
            '}';
    }
}

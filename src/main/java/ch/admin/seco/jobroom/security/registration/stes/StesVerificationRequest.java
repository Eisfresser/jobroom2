package ch.admin.seco.jobroom.security.registration.stes;

import java.time.LocalDate;

public class StesVerificationRequest {

    private Long personNumber;

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

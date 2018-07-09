package ch.admin.seco.jobroom.security.registration;

import java.time.LocalDate;

public class InvalidPersonenNumberException extends RegistrationException {

    private final Long personNumber;

    private final LocalDate birthdate;

    InvalidPersonenNumberException(Long personNumber, LocalDate birthdate) {
        this.personNumber = personNumber;
        this.birthdate = birthdate;
    }

    public Long getPersonNumber() {
        return personNumber;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }
}

package ch.admin.seco.jobroom.service;

import java.time.LocalDate;

public class InvalidPersonenNumberException extends RegistrationException {

    private final Long personNumber;

    private final LocalDate birthdate;

    InvalidPersonenNumberException(Long personNumber, LocalDate birthdate) {
        super("No matching candidate found for PersonNumber: " + personNumber + " and Birthday: " + birthdate);
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

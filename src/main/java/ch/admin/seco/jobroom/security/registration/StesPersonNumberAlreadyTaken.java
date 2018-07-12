package ch.admin.seco.jobroom.security.registration;

public class StesPersonNumberAlreadyTaken extends RegistrationException {

    private final Long personNumber;

    StesPersonNumberAlreadyTaken(Long personNumber) {
        super("PersonNumber already taken: " + personNumber);
        this.personNumber = personNumber;
    }

    public Long getPersonNumber() {
        return personNumber;
    }
}

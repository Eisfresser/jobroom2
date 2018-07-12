package ch.admin.seco.jobroom.security.registration;

public abstract class RegistrationException extends Exception {

    RegistrationException() {
        super("Exception during Registration");
    }

    RegistrationException(String message) {
        super(message);
    }

}

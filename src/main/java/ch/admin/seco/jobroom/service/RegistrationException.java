package ch.admin.seco.jobroom.service;

public abstract class RegistrationException extends Exception {

    RegistrationException() {
        super("Exception during Registration");
    }

    RegistrationException(String message) {
        super(message);
    }

}

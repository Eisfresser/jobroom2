package ch.admin.seco.jobroom.security.registration.eiam;

public abstract class EiamClientRuntimeException extends RuntimeException {

    EiamClientRuntimeException(String message) {
        super(message);
    }
}

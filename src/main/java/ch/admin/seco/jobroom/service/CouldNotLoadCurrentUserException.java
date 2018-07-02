package ch.admin.seco.jobroom.service;

public class CouldNotLoadCurrentUserException extends RuntimeException {

    CouldNotLoadCurrentUserException(String message) {
        super(message);
    }

}

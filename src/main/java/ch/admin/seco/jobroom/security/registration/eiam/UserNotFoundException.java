package ch.admin.seco.jobroom.security.registration.eiam;

public class UserNotFoundException extends Exception {

    UserNotFoundException(String userExtId) {
        super("User with extId " + userExtId + "was not found");
    }

}

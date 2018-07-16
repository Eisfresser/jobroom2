package ch.admin.seco.jobroom.security.registration.eiam;

public class UserNotFoundException extends Exception {

    UserNotFoundException(Identification identification, String id) {
        super("User with " + identification.name() + " " + id + "was not found");
    }

    enum Identification {
        EMAIL, EXT_ID
    }

}

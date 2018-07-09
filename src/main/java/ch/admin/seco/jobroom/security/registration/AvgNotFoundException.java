package ch.admin.seco.jobroom.security.registration;

public class AvgNotFoundException extends Exception {

    AvgNotFoundException(String avgId) {
        super("Not AVG-Organization found with Id: " + avgId);
    }
}

package ch.admin.seco.jobroom.service;

public class AvgNotFoundException extends Exception {

    AvgNotFoundException(String avgId) {
        super("Not AVG-Organization found with Id: " + avgId);
    }
}

package ch.admin.seco.jobroom.service;

public class CandidateNotFoundException extends Exception {

    CandidateNotFoundException(String id) {
        super("No Candidate found having Id:" + id);
    }
}

package ch.admin.seco.jobroom.service;

class CurrentLegalTermsNotFoundException extends RuntimeException {
    CurrentLegalTermsNotFoundException() {
        super("Current Legal Terms cannot have been found!");
    }
}

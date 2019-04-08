package ch.admin.seco.jobroom.service;

public class PastEffectiveLegalTermsException extends Exception {
    PastEffectiveLegalTermsException() {
        super(String.format("Legal terms cannot be created nor modified with effective date on past."));
    }
}

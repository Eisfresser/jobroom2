package ch.admin.seco.jobroom.service;

public class IrremovableLegalTermsException extends Exception {
    IrremovableLegalTermsException(String legalTermsId) {
        super(String.format("Legal terms Effective legal terms with id %s are irremovable!", legalTermsId));
    }
}

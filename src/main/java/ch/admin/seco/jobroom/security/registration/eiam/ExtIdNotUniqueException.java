package ch.admin.seco.jobroom.security.registration.eiam;

class ExtIdNotUniqueException extends EiamClientRuntimeException {
    ExtIdNotUniqueException(String extId) {
        super("Multiple Users found having extId: " + extId);
    }
}

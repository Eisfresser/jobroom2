package ch.admin.seco.jobroom.security.registration.eiam;

class MultipleEiamUsersFound extends EiamClientRuntimeException {

    MultipleEiamUsersFound(EiamClientRuntimeException.Identification identification, String id) {
        super("Multiple Users found having : " + identification + " " + id);
    }

}

package ch.admin.seco.jobroom.security.registration.uid;

public class UidNotUniqueException extends UidClientRuntimeException {

    UidNotUniqueException(long uid) {
        super("Multiple results for uid: " + uid);
    }
}

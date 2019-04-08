package ch.admin.seco.jobroom.security.registration.uid;

public interface UidClient {

    /**
     * Retrieve the user details from the eIAM. The returned user contains information
     * which is not in the SAML assertion (e.g. telephone).
     *
     * @param uid id of the firm in the UID register
     * @return firm data (as returned by the web service)
     * @exception UidCompanyNotFoundException no firm with the given uid was found
     */
    FirmData getCompanyByUid(long uid) throws UidCompanyNotFoundException;

}

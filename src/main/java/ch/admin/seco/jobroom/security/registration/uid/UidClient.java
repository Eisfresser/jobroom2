package ch.admin.seco.jobroom.security.registration.uid;

public interface UidClient {

    /**
     * Retrieve the user details from the eIAM. The returned user contains information
     * which is not in the SAML assertion (e.g. telephone).
     *
     * @param uid id of the firm in the UID register
     * @return firm data (as returned by the web service)
     * @exception CompanyNotFoundException no firm with the given uid was found
     * @exception UidNotUniqueException the given uid returned more than one firm; this should never ever happen
     * @exception UidClientException problem while calling the UID web service
     */
    FirmData getCompanyByUid(long uid) throws CompanyNotFoundException, UidNotUniqueException, UidClientException;

}

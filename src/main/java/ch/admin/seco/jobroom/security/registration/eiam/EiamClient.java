package ch.admin.seco.jobroom.security.registration.eiam;


import ch.adnovum.nevisidm.ws.services.v1.User;

import ch.admin.seco.jobroom.security.registration.eiam.exceptions.ExtIdNotUniqueException;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.UserNotFoundException;

public interface EiamClient {

    //TODO: take from config -> rolemapping
    String ROLE_PREFIX = "ALV-jobroom";
    String ROLE_JOBSEEKER = "ROLE_JOBSEEKER";
    String ROLE_COMPANY = "ROLE_COMPANY";
    String ROLE_PRIVATE_EMPLOYMENT_AGENT = "ROLE_PRIVATE_EMPLOYMENT_AGENT";

    /**
     * Retrieve the user details from the eIAM. The returned user contains information
     * which is not in the SAML assertion (e.g. telephone).
     *
     * @param userExtId id of the user in eIAM
     * @return user details (as returned by the web service)
     * @exception UserNotFoundException no user with the given userExtId was found
     * @exception ExtIdNotUniqueException the given userExtId returned more than one user; this should never ever happen
     */
    User getUserByExtId(String userExtId) throws UserNotFoundException, ExtIdNotUniqueException;

    /**
     * Add the given role to the user in eIAM. This is used during the Jobroom registration
     * process to add the appropriate application role to the user. Since all role related
     * information is hold in the eIAM and not the Jobroom database, this method must be
     * used to add a role to the user.
     * Note: only one of the given role constants should be added in this way. If you try
     * to add a role, which does not exist in eIAM, then you will receive a NevisidmBusinessException.
     *
     * @param userExtId id of the user in eIAM to which the role should be added
     * @param profileExtId id of the user's profile in eIAM to which the role should be added
     * @param roleName name of the role without the application prefix (e.g. ROLE_PRIVATE_EMPLOYMENT_AGENT; this must be an existing role in eIAM)
     * @param applicationName application prefix (e.g. ALV-jobroom)
     * @exception RoleCouldNotBeAddedException  thrown if the role was not added successfully (see cause for details)
     */
    void addRoleToUser(String userExtId, String profileExtId, String roleName, String applicationName) throws RoleCouldNotBeAddedException;

}

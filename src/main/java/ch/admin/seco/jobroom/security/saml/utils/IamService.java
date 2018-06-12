package ch.admin.seco.jobroom.security.saml.utils;

import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.ExtIdNotUniqueException;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.UserNotFoundException;

/**
 * IAM Service to access organizational units and user information.
 */
public interface IamService {

    /**
     * Retrieve the details of the user with the extId contained in the given user, add
     * the missing details to the user object and return it.
     *
     * Note: the user must exist in the eIAM, since he/she authenticated via eIAM and
     * retrieved a valid SAML assertion; if the user is not found it must be a technical
     * problem.
     *
     * @param samlUser  given user (with the details read from the SAML assertion) to which the missing details should be added
     * @return  user object populated with the additional data read from the eIAM webservice
     * @exception UserNotFoundException the userExtId was not found via the eIAM webservice
     * @exception ExtIdNotUniqueException   the userExtId did produce multiple results
     */
    EiamUserPrincipal populateWithEiamData(EiamUserPrincipal samlUser) throws UserNotFoundException, ExtIdNotUniqueException;

    /**
     * Give a user the role JobSeeker in eIAM.
     *
     * @param userExtId id of the user in eIAM to which the role should be added
     * @param profileExtId id of the user's default profile in eIAM to which the role should be added
     * @exception RoleCouldNotBeAddedException  thrown if the role was not added successfully (see cause for details)
     */
    void addJobSeekerRoleToUser(String userExtId, String profileExtId) throws RoleCouldNotBeAddedException;

    /**
     * Give a user the role Company in eIAM.
     *
     * @param extId id of the user in eIAM to which the role should be added
     * @param profileExtId id of the user's default profile in eIAM to which the role should be added
     * @exception RoleCouldNotBeAddedException  thrown if the role was not added successfully (see cause for details)
     */
    void addCompanyRoleToUser(String extId, String profileExtId) throws RoleCouldNotBeAddedException;

    /**
     * Give a user the role PrivateEmploymentAgent in eIAM.
     *
     * @param extId id of the user in eIAM to which the role should be added
     * @param profileExtId id of the user's default profile in eIAM to which the role should be added
     * @exception RoleCouldNotBeAddedException  thrown if the role was not added successfully (see cause for details)
     */
    void addAgentRoleToUser(String extId, String profileExtId) throws RoleCouldNotBeAddedException;

}

package ch.admin.seco.jobroom.security.saml.utils;

import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;

/**
 * IAM Service to access organizational units and user information.
 */
public interface IamService {

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

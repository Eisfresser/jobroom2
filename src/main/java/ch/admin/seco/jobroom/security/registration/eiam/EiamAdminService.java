package ch.admin.seco.jobroom.security.registration.eiam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EiamAdminService {

    private final EiamClient eiamClient;

    @Autowired
    public EiamAdminService(EiamClient eiamClient) {
        this.eiamClient = eiamClient;
    }

    /**
     * Give a user the role JobSeeker in eIAM.
     *
     * @param userExtId id of the user in eIAM to which the role should be added
     * @param profileExtId id of the user's default profile in eIAM to which the role should be added
     * @exception RoleCouldNotBeAddedException  thrown if the role was not added successfully (see cause for details)
     */
    public void addJobSeekerRoleToUser(String userExtId, String profileExtId) throws RoleCouldNotBeAddedException {
        this.eiamClient.addRoleToUser(userExtId, profileExtId, EiamClient.ROLE_JOBSEEKER, EiamClient.ROLE_PREFIX);
    }

    /**
     * Give a user the role Company in eIAM.
     *
     * @param extId id of the user in eIAM to which the role should be added
     * @param profileExtId id of the user's default profile in eIAM to which the role should be added
     * @exception RoleCouldNotBeAddedException  thrown if the role was not added successfully (see cause for details)
     */
    public void addCompanyRoleToUser(String extId, String profileExtId) throws RoleCouldNotBeAddedException {
        this.eiamClient.addRoleToUser(extId, profileExtId, EiamClient.ROLE_COMPANY, EiamClient.ROLE_PREFIX);
    }

    /**
     * Give a user the role PrivateEmploymentAgent in eIAM.
     *
     * @param extId id of the user in eIAM to which the role should be added
     * @param profileExtId id of the user's default profile in eIAM to which the role should be added
     * @exception RoleCouldNotBeAddedException  thrown if the role was not added successfully (see cause for details)
     */
    public void addAgentRoleToUser(String extId, String profileExtId) throws RoleCouldNotBeAddedException {
        this.eiamClient.addRoleToUser(extId, profileExtId, EiamClient.ROLE_PRIVATE_EMPLOYMENT_AGENT, EiamClient.ROLE_PREFIX);
    }
}

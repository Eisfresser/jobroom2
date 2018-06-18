package ch.admin.seco.jobroom.security.saml.utils;

import ch.admin.seco.jobroom.security.registration.eiam.EiamClient;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EiamAdminService implements IamService {

    private final EiamClient eiamClient;

    @Autowired
    public EiamAdminService(EiamClient eiamClient) {
        this.eiamClient = eiamClient;
    }

    @Override
    public void addJobSeekerRoleToUser(String userExtId, String profileExtId) throws RoleCouldNotBeAddedException {
        this.eiamClient.addRoleToUser(userExtId, profileExtId, EiamClient.ROLE_JOBSEEKER, EiamClient.ROLE_PREFIX);
    }

    @Override
    public void addCompanyRoleToUser(String extId, String profileExtId) throws RoleCouldNotBeAddedException {
        this.eiamClient.addRoleToUser(extId, profileExtId, EiamClient.ROLE_COMPANY, EiamClient.ROLE_PREFIX);
    }

    @Override
    public void addAgentRoleToUser(String extId, String profileExtId) throws RoleCouldNotBeAddedException {
        this.eiamClient.addRoleToUser(extId, profileExtId, EiamClient.ROLE_PRIVATE_EMPLOYMENT_AGENT, EiamClient.ROLE_PREFIX);
    }
}

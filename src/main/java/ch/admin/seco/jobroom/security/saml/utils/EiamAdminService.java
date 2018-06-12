package ch.admin.seco.jobroom.security.saml.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.registration.eiam.EiamClient;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;

@Service
public class EiamAdminService implements IamService {

    private final EiamClient eiamClient;

    @Autowired
    public EiamAdminService(EiamClient eiamClient) {
        this.eiamClient = eiamClient;
    }

    @Override
    public EiamUserPrincipal populateWithEiamData(EiamUserPrincipal userPrincipal) {
        Assert.notNull(userPrincipal, "the given principal cannot be null");
        Assert.notNull(userPrincipal.getUser(), "the given principal contains no user");
        Assert.notNull(userPrincipal.getUser().getUserExternalId(), "the user in the given principal must have an extId, otherwise its UserInfo can't be located in the database");
        // currently there is no data we have to retrieve via eIAM webservice call
        // User eiamUser = this.eiamClient.getUserByExtId(userPrincipal.getUser().getUserExternalId());
        // userPrincipal.getUser().setPhone(eiamUser.getTelephone());
        return userPrincipal;
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

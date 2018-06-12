package ch.admin.seco.jobroom.security.saml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamEnrichedSamlUser;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlUser;
import ch.admin.seco.jobroom.security.saml.utils.IamService;

public class DefaultSamlBasedUserDetailsProvider implements SamlBasedUserDetailsProvider {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSamlBasedUserDetailsProvider.class);

    private IamService iamService;

    private Map<String, String> rolemapping;

    public DefaultSamlBasedUserDetailsProvider(IamService iamService, Map<String, String> rolemapping) {
        this.iamService = iamService;
        this.rolemapping = rolemapping;
    }

    /**
     * The user's details are mainly taken from the SAML assertion, but are enriched with
     * data from to other sources. The data is combined from these sources:
     * from SAML assertion: userExtId, roles, givenname, surname, emailaddress, language
     * from eIAM webservice: phone (because it is missing in the SAML assertion)
     * from Jobroom database: organisation (because no organisations and relations to them are held in the eIAM) and some application-specific data
     *
     * @param samlUser  object with the data read from the SAML assertion
     * @return  UserDetails object with the data from eIAM (via SAML assertion and eIAM webservice call)
     */
    @Override
    public UserDetails createUserDetailsFromSaml(SamlUser samlUser) {
        if (!(samlUser instanceof EiamEnrichedSamlUser)) {
            throw new IllegalArgumentException("EIAMEnrichedSAMLUser needed for getting userExtId");
        }

        EiamEnrichedSamlUser eIamSamlUser = (EiamEnrichedSamlUser) samlUser;
        if (!eIamSamlUser.getUserExtId().isPresent()) {
            throw new IllegalArgumentException("EIAMEnrichedSAMLUser has no userExtId");
        }

        return toEiamUserPrincipal(eIamSamlUser);
    }

    private EiamUserPrincipal toEiamUserPrincipal(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        // create a new principal with the information from the SAML assertion
        EiamUserPrincipal eiamUserPrincipal = new EiamUserPrincipal();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserExternalId(eiamEnrichedSamlUser.getUserExtId().get());
        userInfo.setLastName(eiamEnrichedSamlUser.getSurname().get());
        userInfo.setFirstName(eiamEnrichedSamlUser.getGivenname().get());
        userInfo.setEmail(eiamEnrichedSamlUser.getEmail().get());
        userInfo.setLangKey(eiamEnrichedSamlUser.getLanguage().get().toLowerCase());
        eiamUserPrincipal.setUser(userInfo);
        List<String> jobRoomRoles = mapEiamRolesToJobRoomRoles(eiamEnrichedSamlUser.getRoles());
        eiamUserPrincipal.setAuthoritiesFromStringCollection(jobRoomRoles);
        eiamUserPrincipal.setAuthenticationMethod(eiamEnrichedSamlUser.getAuthnContext());
        eiamUserPrincipal.setUserDefaultProfileExtId(eiamEnrichedSamlUser.getDefaultProfileExtId().get());

        if (LOG.isDebugEnabled()) {
            LOG.debug(userInfo.toString());
        }

        // add information which is only available through the eIAM webservice (currently only the phone)
        EiamUserPrincipal result;
        try {
            result = this.iamService.populateWithEiamData(eiamUserPrincipal);
        } catch (Throwable e) {
            // TODO: question is, if we can live without the phone and thus just ignore a problem here
            LOG.error("The retrieval of the phone number via eIAM webservice failed for user with extId {}", userInfo.getUserExternalId());
            result = eiamUserPrincipal;
        }
        return result;
    }

    private List<String> mapEiamRolesToJobRoomRoles(List<String> eiamRoles) {
        Map<String, String> reverseMap = new HashMap<>(rolemapping.size());
        for (Map.Entry<String, String> mapping : rolemapping.entrySet()) {
            reverseMap.put(mapping.getValue(), mapping.getKey());
        }
        List<String> jobRoomRoles = new ArrayList<>();
        for (String eiamRole : eiamRoles) {
            String jobRoomRole = reverseMap.get(eiamRole);
            if (jobRoomRole != null) {
                jobRoomRoles.add(jobRoomRole);
            }
        }
        return jobRoomRoles;
    }

}

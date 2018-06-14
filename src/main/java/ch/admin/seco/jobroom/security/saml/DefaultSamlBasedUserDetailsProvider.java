package ch.admin.seco.jobroom.security.saml;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.saml.dsl.NotEiamEnrichedSamlUserAuthenticationException;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamEnrichedSamlUser;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class DefaultSamlBasedUserDetailsProvider implements SamlBasedUserDetailsProvider {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSamlBasedUserDetailsProvider.class);

    private final UserInfoRepository userInfoRepository;

    private final Map<String, String> rolemapping;

    public DefaultSamlBasedUserDetailsProvider(UserInfoRepository userInfoRepository, Map<String, String> rolemapping) {
        this.userInfoRepository = userInfoRepository;
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
            throw new NotEiamEnrichedSamlUserAuthenticationException(samlUser);
        }
        return toEiamUserPrincipal((EiamEnrichedSamlUser) samlUser);
    }

    private EiamUserPrincipal toEiamUserPrincipal(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        final String userExternalId = eiamEnrichedSamlUser.getUserExtId().get();
        EiamUserPrincipal eiamUserPrincipal = new EiamUserPrincipal();
        List<String> jobRoomRoles = mapEiamRolesToJobRoomRoles(eiamEnrichedSamlUser.getRoles());
        eiamUserPrincipal.setAuthoritiesFromStringCollection(jobRoomRoles);
        eiamUserPrincipal.setAuthenticationMethod(eiamEnrichedSamlUser.getAuthnContext());
        eiamUserPrincipal.setUserDefaultProfileExtId(eiamEnrichedSamlUser.getDefaultProfileExtId().get());

        UserInfo userInfoFromSaml = toUserInfo(eiamEnrichedSamlUser);
        Optional<UserInfo> existingDbUser = userInfoRepository.findOneByUserExternalId(userExternalId);
        if (existingDbUser.isPresent()) {   // existing user
            UserInfo existingUserInfo = existingDbUser.get();
            updateUserInfo(userInfoFromSaml, existingUserInfo);
            existingUserInfo = this.userInfoRepository.save(existingUserInfo);
            // Hack to make sure we load all the accountabilities
            existingUserInfo.getAccountabilities().size();
            eiamUserPrincipal.setUser(existingUserInfo);
            eiamUserPrincipal.setNeedsRegistration(false);
        } else {
            eiamUserPrincipal.setUser(userInfoFromSaml);
            eiamUserPrincipal.setNeedsRegistration(true);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(userInfoFromSaml.toString());
        }
        return eiamUserPrincipal;
    }

    private UserInfo toUserInfo(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserExternalId(eiamEnrichedSamlUser.getUserExtId().get());
        userInfo.setLastName(eiamEnrichedSamlUser.getSurname().get());
        userInfo.setFirstName(eiamEnrichedSamlUser.getGivenname().get());
        userInfo.setEmail(eiamEnrichedSamlUser.getEmail().get());
        userInfo.setLangKey(eiamEnrichedSamlUser.getLanguage().get().toLowerCase());
        return userInfo;
    }

    private void updateUserInfo(UserInfo fromUserInfo, UserInfo toUserInfo) {
        toUserInfo.setUserExternalId(fromUserInfo.getUserExternalId());
        toUserInfo.setLastName(fromUserInfo.getLastName());
        toUserInfo.setFirstName(fromUserInfo.getFirstName());
        toUserInfo.setEmail(fromUserInfo.getEmail());
        toUserInfo.setPhone(fromUserInfo.getPhone());
        toUserInfo.setLangKey(fromUserInfo.getLangKey());
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

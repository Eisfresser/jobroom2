package ch.admin.seco.jobroom.security.saml;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamEnrichedSamlUser;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlUser;

public class DefaultSamlBasedUserDetailsProvider implements SamlBasedUserDetailsProvider {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSamlBasedUserDetailsProvider.class);

    private final UserInfoRepository userInfoRepository;

    private final EiamRoleMapper eiamRoleMapper;

    private final TransactionTemplate transactionTemplate;

    public DefaultSamlBasedUserDetailsProvider(UserInfoRepository userInfoRepository, Map<String, String> rolemapping, TransactionTemplate transactionTemplate) {
        this.userInfoRepository = userInfoRepository;
        this.eiamRoleMapper = new EiamRoleMapper(rolemapping);
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public UserDetails createUserDetailsFromSaml(SamlUser samlUser) {
        if (!(samlUser instanceof EiamEnrichedSamlUser)) {
            return new User(samlUser.getNameId(), "N/A", Collections.emptyList());
        }

        return this.transactionTemplate.execute((TransactionCallback<UserDetails>) status -> toEiamUserPrincipal((EiamEnrichedSamlUser) samlUser));
    }

    private UserPrincipal toEiamUserPrincipal(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        UserInfo userInfo = determineUserInfo(eiamEnrichedSamlUser);
        return prepareEiamUserPrincipal(eiamEnrichedSamlUser, userInfo);
    }

    private UserInfo determineUserInfo(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        String userExtId = eiamEnrichedSamlUser.getUserExtId().get();
        Optional<UserInfo> existingDbUser = this.userInfoRepository.findOneByUserExternalId(userExtId);
        if (existingDbUser.isPresent()) {
            LOG.debug("User with extId: {} already exists in db", userExtId);
            updateDbUser(eiamEnrichedSamlUser, existingDbUser.get());
            return this.userInfoRepository.save(existingDbUser.get());
        } else {
            LOG.debug("User with extId: {} does not exist yet in db: ", userExtId);
            return this.userInfoRepository.save(toUserInfo(eiamEnrichedSamlUser));
        }
    }

    private UserPrincipal prepareEiamUserPrincipal(EiamEnrichedSamlUser eiamEnrichedSamlUser, UserInfo userInfo) {
        UserPrincipal userPrincipal = new UserPrincipal(
            userInfo.getId(),
            eiamEnrichedSamlUser.getGivenname().get(),
            eiamEnrichedSamlUser.getSurname().get(),
            eiamEnrichedSamlUser.getEmail().get(),
            eiamEnrichedSamlUser.getUserExtId().get(),
            eiamEnrichedSamlUser.getLanguage().get().toLowerCase()
        );
        userPrincipal.setAuthoritiesFromStringCollection(this.eiamRoleMapper.mapEiamRolesToJobRoomRoles(eiamEnrichedSamlUser.getRoles()));
        userPrincipal.setUserDefaultProfileExtId(eiamEnrichedSamlUser.getDefaultProfileExtId().get());

        return userPrincipal;
    }

    private void updateDbUser(EiamEnrichedSamlUser eiamEnrichedSamlUser, UserInfo userInfo) {
        userInfo.loginWithUpdate(
            eiamEnrichedSamlUser.getGivenname().get(),
            eiamEnrichedSamlUser.getSurname().get(),
            eiamEnrichedSamlUser.getEmail().get(),
            eiamEnrichedSamlUser.getLanguage().get().toLowerCase()
        );
    }

    private UserInfo toUserInfo(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        return new UserInfo(
            eiamEnrichedSamlUser.getGivenname().get(),
            eiamEnrichedSamlUser.getSurname().get(),
            eiamEnrichedSamlUser.getEmail().get(),
            eiamEnrichedSamlUser.getUserExtId().get(),
            eiamEnrichedSamlUser.getLanguage().get().toLowerCase()
        );
    }

}

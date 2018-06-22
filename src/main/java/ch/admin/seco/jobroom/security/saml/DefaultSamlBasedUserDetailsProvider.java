package ch.admin.seco.jobroom.security.saml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamEnrichedSamlUser;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlUser;

@Transactional
public class DefaultSamlBasedUserDetailsProvider implements SamlBasedUserDetailsProvider {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSamlBasedUserDetailsProvider.class);

    private final UserInfoRepository userInfoRepository;

    private final Map<String, String> rolemapping;

    private final TransactionTemplate transactionTemplate;

    public DefaultSamlBasedUserDetailsProvider(UserInfoRepository userInfoRepository, Map<String, String> rolemapping, TransactionTemplate transactionTemplate) {
        this.userInfoRepository = userInfoRepository;
        this.rolemapping = rolemapping;
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

    private boolean hasJobRoomAllowRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ROLE_ALLOW));
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
        userPrincipal.setAuthoritiesFromStringCollection(mapEiamRolesToJobRoomRoles(eiamEnrichedSamlUser.getRoles()));
        userPrincipal.setAuthenticationMethod(eiamEnrichedSamlUser.getAuthnContext());
        userPrincipal.setUserDefaultProfileExtId(eiamEnrichedSamlUser.getDefaultProfileExtId().get());

        if (!hasJobRoomAllowRole(userPrincipal.getAuthorities())) {
            throw new InsufficientAuthenticationException("User with ext-id: " + eiamEnrichedSamlUser.getUserExtId() + " doesn't have the ALLOW role");
        }
        return userPrincipal;
    }

    private void updateDbUser(EiamEnrichedSamlUser eiamEnrichedSamlUser, UserInfo userInfo) {
        userInfo.update(
            eiamEnrichedSamlUser.getSurname().get(),
            eiamEnrichedSamlUser.getGivenname().get(),
            eiamEnrichedSamlUser.getEmail().get(),
            eiamEnrichedSamlUser.getLanguage().get().toLowerCase()
        );
    }

    private UserInfo toUserInfo(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        return new UserInfo(
            eiamEnrichedSamlUser.getSurname().get(),
            eiamEnrichedSamlUser.getGivenname().get(),
            eiamEnrichedSamlUser.getEmail().get(),
            eiamEnrichedSamlUser.getUserExtId().get(),
            eiamEnrichedSamlUser.getLanguage().get().toLowerCase()
        );
    }

    private Set<String> mapEiamRolesToJobRoomRoles(List<String> eiamRoles) {
        Map<String, List<String>> multimap = new HashMap<>();
        for (Map.Entry<String, String> mappingEntry : this.rolemapping.entrySet()) {
            String jobRoomRole = mappingEntry.getKey();
            String eiamRole = mappingEntry.getValue();
            multimap.computeIfAbsent(eiamRole, k -> new ArrayList<>())
                .add(jobRoomRole);
        }
        Set<String> result = new HashSet<>();
        for (String eiamRole : eiamRoles) {
            List<String> strings = multimap.getOrDefault(eiamRole, Collections.emptyList());
            result.addAll(strings);
        }
        return result;
    }

}

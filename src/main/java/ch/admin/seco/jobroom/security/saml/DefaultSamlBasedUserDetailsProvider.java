package ch.admin.seco.jobroom.security.saml;

import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamEnrichedSamlUser;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlUser;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.utils.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.MapBasedAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class DefaultSamlBasedUserDetailsProvider implements SamlBasedUserDetailsProvider {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSamlBasedUserDetailsProvider.class);

    private static final String DEFAULT_DUMMY_PASSWORD = "N/A"; //NOSONAR

    //private IamService iamService;
    private Map<String, String> roleMapping;

    private final Attributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper;

    @Autowired
    public DefaultSamlBasedUserDetailsProvider(Map<String, String> roleMapping) { //IamService iamService) {
        //this.iamService = iamService;
        this.roleMapping = roleMapping;
        this.attributes2GrantedAuthoritiesMapper = buildAttributes2GrantedAuthoritiesMapper();
    }

    @Override
    public UserDetails createUserDetails(SamlUser samlUser) {
        if (!(samlUser instanceof EiamEnrichedSamlUser)) {
            throw new IllegalArgumentException("EIAMEnrichedSAMLUser needed for getting userExtId");
        }
        EiamEnrichedSamlUser eIamSamlUser = (EiamEnrichedSamlUser) samlUser;
        String username = eIamSamlUser.getDisplayName().orElse(eIamSamlUser.getUserExtId().orElse(eIamSamlUser.getNameId()));
        //TODO: remove -> workaround for capital login name
        username = username.toLowerCase();
        return new AuthenticatedUser(username, toJobroomUser(eIamSamlUser), toGrantedAuthorities(eIamSamlUser.getRoles()), DEFAULT_DUMMY_PASSWORD);
    }

    private User toJobroomUser(EiamEnrichedSamlUser eiamEnrichedSamlUser) {
        String phone = null;

/*        if (eiamEnrichedSamlUser.getUserExtId().isPresent()) {
            User user = this.iamService.getUser(eiamEnrichedSamlUser.getUserExtId().get());
            phone = user.getPhone();
        }
        else {
            LOG.error("The EIAM user does not have an extId.");
            return null;
        } */

        User jobroomUser = new User();
        //jobroomUser.setId(eiamEnrichedSamlUser.getUserExtId().get());
        jobroomUser.setLastName(eiamEnrichedSamlUser.getSurname().get());
        jobroomUser.setFirstName(eiamEnrichedSamlUser.getGivenname().get());
        jobroomUser.setEmail(eiamEnrichedSamlUser.getEmail().get());
        jobroomUser.setLangKey(eiamEnrichedSamlUser.getLanguage().get().toLowerCase());
        jobroomUser.setPhone(phone);

        if (LOG.isDebugEnabled()) {
            LOG.debug(jobroomUser.toString());
        }
        return jobroomUser;
    }

    private Set<GrantedAuthority> toGrantedAuthorities(List<String> roles) {
        return new HashSet<>(attributes2GrantedAuthoritiesMapper.getGrantedAuthorities(roles));
    }

    private Attributes2GrantedAuthoritiesMapper buildAttributes2GrantedAuthoritiesMapper() {
        MapBasedAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
        attributes2GrantedAuthoritiesMapper.setAttributes2grantedAuthoritiesMap(this.roleMapping);
        return attributes2GrantedAuthoritiesMapper;
    }
}

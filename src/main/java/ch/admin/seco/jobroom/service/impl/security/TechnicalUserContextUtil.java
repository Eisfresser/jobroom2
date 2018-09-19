package ch.admin.seco.jobroom.service.impl.security;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.UserPrincipal;

final class TechnicalUserContextUtil {

    private static final String DEFAULT_ROLE = AuthoritiesConstants.ROLE_ADMIN;

    private static final UserInfoId TECH_USER_ID = new UserInfoId("tech-user-id");

    private static final String TECH_USER_NAME = "Tech-User";

    private static final String TECH_USER_EMAIL = "techuser@example.com";

    private static final String TECH_USER_LANG_KEY = "de";

    private static final String TECH_USER_EXT_ID = "tech-user-ext-id";

    private TechnicalUserContextUtil() {
    }

    static void initContext() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(DEFAULT_ROLE);
        securityContext.setAuthentication(prepareAuthentication(authorities));
        SecurityContextHolder.setContext(securityContext);
    }

    static void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private static UsernamePasswordAuthenticationToken prepareAuthentication(List<GrantedAuthority> authorities) {
        return new UsernamePasswordAuthenticationToken(prepareUser(authorities), null, authorities);
    }

    private static UserPrincipal prepareUser(List<GrantedAuthority> authorities) {
        UserPrincipal userPrincipal = new UserPrincipal(
            TECH_USER_ID,
            TECH_USER_NAME,
            TECH_USER_NAME,
            TECH_USER_EMAIL,
            TECH_USER_EXT_ID,
            TECH_USER_LANG_KEY
        );
        userPrincipal.setAuthorities(authorities);
        return userPrincipal;
    }

}

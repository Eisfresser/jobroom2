package ch.admin.seco.jobroom.security.jwt;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ch.admin.seco.jobroom.domain.Organization;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.LoginFormUserPrincipal;

class TestAuthenticationFactory {
    static Authentication anonymousAuthentication() {
        List<SimpleGrantedAuthority> authorities = singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        return new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities);
    }

    static Authentication domainUserAuthentication() {
        LoginFormUserPrincipal principal = new LoginFormUserPrincipal(jobRoomUser());
        List<SimpleGrantedAuthority> authorities = singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        return new UsernamePasswordAuthenticationToken(principal, EMPTY, authorities);
    }

    private static User jobRoomUser() {
        User user = new User();
        user.setEmail("Email");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setPhone("123 456 789");
        user.setId(randomUUID());
        user.setOrganization(organization());
        return user;
    }

    private static Organization organization() {
        Organization organization = new Organization();
        organization.setId(randomUUID());
        return organization;
    }
}

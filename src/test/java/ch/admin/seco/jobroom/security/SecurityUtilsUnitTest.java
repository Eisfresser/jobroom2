package ch.admin.seco.jobroom.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;

import static ch.admin.seco.jobroom.security.AuthoritiesConstants.*;
import static ch.admin.seco.jobroom.security.SecurityUtils.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;
import static org.springframework.security.core.context.SecurityContextHolder.setContext;

/**
 * Test class for the SecurityUtils utility class.
 *
 * @see SecurityUtils
 */
public class SecurityUtilsUnitTest {

    private SecurityContext securityContext;

    @Before
    public void setUp()  {
        securityContext = createEmptyContext();
        setContext(securityContext);
    }

    @Test
    public void testgetCurrentUserLogin() {
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));

        Optional<String> login = getCurrentUserLogin();

        assertThat(login).contains("admin");
    }

    @Test
    public void testgetCurrentUserJWT() {
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "token"));

        Optional<String> jwt = getCurrentUserJWT();

        assertThat(jwt).contains("token");
    }

    @Test
    public void testIsAuthenticated() {
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));

        boolean authenticated = isAuthenticated();

        assertThat(authenticated).isTrue();
    }

    @Test
    public void testAnonymousIsNotAuthenticated() {
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", singletonList(new SimpleGrantedAuthority(ANONYMOUS))));

        boolean isAuthenticated = isAuthenticated();

        assertThat(isAuthenticated).isFalse();
    }

    @Test
    public void testIsCurrentUserInRole() {
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", singletonList(new SimpleGrantedAuthority(USER))));

        boolean hasRoleUser = isCurrentUserInRole(USER);
        boolean hasRoleAdmin = isCurrentUserInRole(ROLE_ADMIN);

        assertThat(hasRoleUser).isTrue();
        assertThat(hasRoleAdmin).isFalse();
    }
}

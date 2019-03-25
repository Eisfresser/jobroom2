package ch.admin.seco.jobroom.security;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;

import static ch.admin.seco.jobroom.security.SecurityUtils.getCurrentUserLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;
import static org.springframework.security.core.context.SecurityContextHolder.setContext;

/**
 * Test class for the SecurityUtils utility class.
 *
 * @see SecurityUtils
 */
@Ignore("FIXME")
public class SecurityUtilsUnitTest {

    private SecurityContext securityContext;

    @Before
    public void setUp() {
        securityContext = createEmptyContext();
        setContext(securityContext);
    }

    @Test
    public void testGetCurrentUserLogin() {
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));

        Optional<String> login = getCurrentUserLogin();

        assertThat(login).contains("admin");
    }


}

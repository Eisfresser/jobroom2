package ch.admin.seco.jobroom.service.impl.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobroom.security.UserPrincipal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TechnicalUserContextAspectTest {

    @Autowired
    private TestService testService;

    @Before
    public void setUp() {
        this.testService.reset();
    }

    @Test
    public void testSecurityContextIsInitialized() {
        // when
        this.testService.testWithAnnotation();

        //then
        Object currentAuthentication = this.testService.getCurrentAuthentication();
        assertThat(currentAuthentication)
            .isNotNull()
            .isInstanceOf(UsernamePasswordAuthenticationToken.class);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) currentAuthentication;
        assertThat(usernamePasswordAuthenticationToken.getAuthorities())
            .containsExactly(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Object principal = usernamePasswordAuthenticationToken.getPrincipal();
        assertThat(principal)
            .isNotNull()
            .isInstanceOf(UserPrincipal.class);

        UserPrincipal userPrincipal = (UserPrincipal) principal;
        assertThat(userPrincipal.getAuthorities())
            .isNotEmpty();
    }

    @Test
    public void testSecurityContextIsNotInitialized() {
        // when
        this.testService.testWithoutAnnotation();

        //then
        assertThat(this.testService.getCurrentAuthentication()).isNull();
    }

    static class TestService {

        private Object currentAuthentication;

        @LoginAsTechnicalUser
        void testWithAnnotation() {
            this.currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        }

        void testWithoutAnnotation() {
            // method has not @LoginAsTechnicalUser annotation
        }

        Object getCurrentAuthentication() {
            return currentAuthentication;
        }

        void reset() {
            this.currentAuthentication = null;
        }

    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TestService testService() {
            return new TestService();
        }

    }

}

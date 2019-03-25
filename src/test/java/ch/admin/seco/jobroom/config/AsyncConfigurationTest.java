package ch.admin.seco.jobroom.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncConfigurationTest {

    private static final String TEST_USER = "TEST-USER";

    @Autowired
    private TestAsyncDummy testAsyncDummy;

    @Test
    public void testAsyncInvocation() {
        // given
        loginAsTestUser();

        // when
        this.testAsyncDummy.invokeAsyncTestMethod();

        await().until(() -> Objects.nonNull(this.testAsyncDummy.getCapturedPrincipal()));

        // then
        Object capturedPrincipal = this.testAsyncDummy.getCapturedPrincipal();
        assertThat(capturedPrincipal).isEqualTo(TEST_USER);
    }

    private void loginAsTestUser() {
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(TEST_USER, "N/A"));
        SecurityContextHolder.setContext(emptyContext);
    }

    static class TestAsyncDummy {

        private Object capturedPrincipal;

        @Async
        public void invokeAsyncTestMethod() {
            this.capturedPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        Object getCapturedPrincipal() {
            return capturedPrincipal;
        }
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TestAsyncDummy testAsyncDummy() {
            return new TestAsyncDummy();
        }
    }


}

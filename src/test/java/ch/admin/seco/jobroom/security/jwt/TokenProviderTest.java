package ch.admin.seco.jobroom.security.jwt;

import static ch.admin.seco.jobroom.security.jwt.TestAuthenticationFactory.anonymousAuthentication;
import static ch.admin.seco.jobroom.security.jwt.TestAuthenticationFactory.domainUserAuthentication;
import static ch.admin.seco.jobroom.security.jwt.TestJHipsterPropertiesFactory.TOKEN_VALID_60_SECONDS;
import static ch.admin.seco.jobroom.security.jwt.TestJHipsterPropertiesFactory.jHipsterProperties;
import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.EMPTY_TOKEN;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.UNSUPPORTED_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

public class TokenProviderTest {

    private TokenProvider tokenProvider;
    private TokenValidator validator;

    @Before
    public void setup() {
        tokenProvider = new TokenProvider(jHipsterProperties());
        validator = new TokenValidator(e5c9ee274ae87bc031adda32e27fa98b9290da83.name());
    }

    @Test
    public void shouldCreateTokenReturnValidTokenIfValidUserAndRememberMeIsFalse() {
        Authentication authentication = domainUserAuthentication();

        String token = tokenProvider.createToken(authentication, false);

        assertThat(validator.validateToken(token)).isTrue();
    }

    @Test
    public void shouldCreateTokenReturnValidTokenIfValidUserAndRememberMeIsTrue() {
        Authentication authentication = domainUserAuthentication();

        String token = tokenProvider.createToken(authentication, false);

        assertThat(validator.validateToken(token)).isTrue();
    }

    @Test
    public void testReturnFalseWhenJWTisMalformed() {
        Authentication authentication = anonymousAuthentication();

        String token = tokenProvider.createToken(authentication, false);
        String invalidToken = token.substring(1);

        assertThat(validator.validateToken(invalidToken)).isFalse();
    }

    @Test
    public void testReturnFalseWhenJWTisExpired() {
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -TOKEN_VALID_60_SECONDS);

        Authentication authentication = anonymousAuthentication();
        String token = tokenProvider.createToken(authentication, false);

        assertThat(validator.validateToken(token)).isFalse();
    }

    @Test
    public void testReturnFalseWhenJWTisUnsupported() {
        String token = UNSUPPORTED_TOKEN;

        assertThat(validator.validateToken(token)).isFalse();
    }

    @Test
    public void testReturnFalseWhenJWTisInvalid() {
        final String token = EMPTY_TOKEN;

        assertThat(validator.validateToken(token)).isFalse();
    }

}


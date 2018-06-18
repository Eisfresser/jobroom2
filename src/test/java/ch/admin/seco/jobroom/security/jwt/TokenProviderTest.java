package ch.admin.seco.jobroom.security.jwt;

import ch.admin.seco.jobroom.repository.UserInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.util.Collection;

import static ch.admin.seco.jobroom.security.jwt.TestAuthenticationFactory.anonymousAuthentication;
import static ch.admin.seco.jobroom.security.jwt.TestAuthenticationFactory.domainUserAuthentication;
import static ch.admin.seco.jobroom.security.jwt.TestJHipsterPropertiesFactory.jHipsterProperties;
import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class TokenProviderTest {


    private TokenProvider tokenProvider;

    private TokenValidator validator;

    @Parameter
    public Authentication authentication;

    @Parameter(1)
    public boolean rememberMe;

    @Parameter(2)
    public boolean validToken;

    @Before
    public void setup() {
        tokenProvider = new TokenProvider(jHipsterProperties(), mock(UserInfoRepository.class));
        validator = new TokenValidator(e5c9ee274ae87bc031adda32e27fa98b9290da83.name());
    }

    @Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{
                {domainUserAuthentication(), false, true},
                {domainUserAuthentication(), true, true},
                {anonymousAuthentication(), false, true},
                {anonymousAuthentication(), true, true}
            }
        );
    }

    @Test
    public void shouldCreateToken() {

        String token = tokenProvider.createToken(authentication, rememberMe);

        assertThat(validator.validateToken(token)).isEqualTo(validToken);
    }

    @Test
    public void shouldCreateAccessToken() {
        final DefaultOAuth2AccessToken accessToken = tokenProvider.createAccessToken(authentication);

        assertThat(validator.validateToken(accessToken.getValue())).isEqualTo(validToken);
    }
}


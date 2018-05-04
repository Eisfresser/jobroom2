package ch.admin.seco.jobroom.security.jwt;

import static ch.admin.seco.jobroom.security.jwt.TestJHipsterPropertiesFactory.TOKEN_VALID_60_SECONDS;
import static ch.admin.seco.jobroom.security.jwt.TestJHipsterPropertiesFactory.jHipsterProperties;
import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.EMPTY_TOKEN;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.UNSUPPORTED_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import ch.admin.seco.jobroom.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import ch.admin.seco.jobroom.security.AuthoritiesConstants;

public class TokenProviderTest {

    private TokenProvider tokenProvider;
    private TokenValidator tokenValidator;

    private User user;

    @Before
    public void setup() {
        user = Mockito.mock(User.class);
        tokenProvider = new TokenProvider(jHipsterProperties());
        tokenValidator = new TokenValidator(e5c9ee274ae87bc031adda32e27fa98b9290da83.name());
    }

    @Test
    public void testReturnFalseWhenJWTisMalformed() {
        Authentication authentication = createAuthentication();

        String token = tokenProvider.createToken(authentication, false, user);
        String invalidToken = token.substring(1);
        boolean isTokenValid = tokenValidator.validateToken(invalidToken);

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisExpired() {
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -TOKEN_VALID_60_SECONDS);

        Authentication authentication = createAuthentication();
        String token = tokenProvider.createToken(authentication, false, user);

        boolean isTokenValid = tokenValidator.validateToken(token);

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisUnsupported() {
        String unsupportedToken = UNSUPPORTED_TOKEN;

        boolean isTokenValid = tokenValidator.validateToken(unsupportedToken);

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisInvalid() {
        final String emptyToken = EMPTY_TOKEN;

        boolean isTokenValid = tokenValidator.validateToken(emptyToken);

        assertThat(isTokenValid).isEqualTo(false);
    }

    private Authentication createAuthentication() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        return new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities);
    }
}


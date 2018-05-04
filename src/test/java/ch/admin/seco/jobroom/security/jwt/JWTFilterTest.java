package ch.admin.seco.jobroom.security.jwt;

import ch.admin.seco.jobroom.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import java.io.IOException;

import static ch.admin.seco.jobroom.security.jwt.JWTFilter.AUTHORIZATION_HEADER;
import static ch.admin.seco.jobroom.security.jwt.JWTFilter.TOKEN_PREFIX;
import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.INVALID_TOKEN;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.TOKEN_VALID_19_YEARS;
import static org.assertj.core.api.Assertions.assertThat;

public class JWTFilterTest {

    static final String WRONG_AUTHORIZATION_HEADER = "Basic ";

    private TokenToAuthenticationConverter tokenToAuthenticationConverter;

    private JWTFilter jwtFilter;

    private MockHttpServletRequest request;

    private FilterChain filterChain;

    private MockHttpServletResponse response;

    @Before
    public void setup() {
        tokenToAuthenticationConverter = new TokenToAuthenticationConverter(e5c9ee274ae87bc031adda32e27fa98b9290da83.name());
        jwtFilter = new JWTFilter(tokenToAuthenticationConverter, TokenResolver.of(e5c9ee274ae87bc031adda32e27fa98b9290da83.name()));
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        filterChain = new MockFilterChain();
        SecurityContextHolder.getContext()
                             .setAuthentication(null);
    }

    @Test
    public void shouldDoFilterSetAuthenticationWithTokenAsCredentials() throws Exception {
        request.addHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX + TOKEN_VALID_19_YEARS);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(authentication()).isNotNull();
        assertThat(authentication().getCredentials()
                                 .toString()).isEqualTo(TOKEN_VALID_19_YEARS);
    }

    @Test
    public void shouldDoFilterSetNullAuthenticationIfInvalidToken() throws Exception {        ;
        request.addHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX + INVALID_TOKEN);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(authentication()).isNull();
    }

    @Test
    public void shouldDoFilterSetNullAuthenticationIfAuthorizationHeaderIsMissing() throws Exception {
        jwtFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(authentication()).isNull();
    }

    @Test
    public void shouldDoFilterSetNullAuthenticationIfTokenIsMissing() throws Exception {
        request.addHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(authentication()).isNull();
    }

    @Test
    public void shouldDoFilterSetNullAuthenticationIfAuthorizationHeaderIsWrong() throws IOException, ServletException {
        request.addHeader(AUTHORIZATION_HEADER, WRONG_AUTHORIZATION_HEADER + TOKEN_VALID_19_YEARS);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(authentication()).isNull();
    }

    private Authentication authentication() {
        return SecurityContextHolder.getContext()
                                    .getAuthentication();
    }
}


package ch.admin.seco.jobroom.security.jwt;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.AUTHORIZATION_HEADER;
import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.TOKEN_PREFIX;
import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.INVALID_TOKEN;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.TOKEN_VALID_19_YEARS;
import static org.assertj.core.api.Assertions.assertThat;

public class JWTFilterTest {

    static final String WRONG_AUTHORIZATION_HEADER = "Basic ";

    private JWTConfigurer.TokenToAuthenticationConverter tokenToAuthenticationConverter;

    private JWTConfigurer.JWTFilter jwtFilter;

    private MockHttpServletRequest request;

    private FilterChain filterChain;

    private MockHttpServletResponse response;

    @Before
    public void setup() {
        tokenToAuthenticationConverter = new JWTConfigurer.TokenToAuthenticationConverter(e5c9ee274ae87bc031adda32e27fa98b9290da83.name());
        jwtFilter = new JWTConfigurer.JWTFilter(tokenToAuthenticationConverter, JWTConfigurer.TokenResolver.of(e5c9ee274ae87bc031adda32e27fa98b9290da83.name()));
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        filterChain = new MockFilterChain();
        SecurityContextHolder.getContext()
                             .setAuthentication(null);
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


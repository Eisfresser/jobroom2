package ch.admin.seco.jobroom.security.jwt;

import java.io.IOException;
import java.util.function.Consumer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
public class JWTFilter extends GenericFilterBean {

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenToAuthenticationConverter tokenToAuthenticationConverter;

    private final TokenResolver tokenResolver;

    JWTFilter(TokenToAuthenticationConverter converter, TokenResolver resolver) {
        this.tokenToAuthenticationConverter = converter;
        this.tokenResolver = resolver;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        tokenResolver.resolveToken(servletRequest).ifPresent(authenticateWithToken());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Consumer<String> authenticateWithToken() {
        return token -> SecurityContextHolder.getContext()
                                             .setAuthentication(this.tokenToAuthenticationConverter.convertTokenToAuthentication(token));
    }
}

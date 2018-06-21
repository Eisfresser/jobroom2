package ch.admin.seco.jobroom.security.jwt;

import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.security.UserPrincipal;
import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.*;
import static io.jsonwebtoken.Jwts.parser;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final Jwt jwt;

    public JWTConfigurer(Jwt jwt) {
        this.jwt = jwt;
    }

    @Override
    public void configure(HttpSecurity http) {
        String secretKey = jwt.getSecret();
        JWTFilter jwtFilter = new JWTFilter(new TokenToAuthenticationConverter(secretKey), TokenResolver.of(secretKey));
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public static class JWTFilter extends GenericFilterBean {

        private final TokenToAuthenticationConverter tokenToAuthenticationConverter;

        private final TokenResolver tokenResolver;

        JWTFilter(TokenToAuthenticationConverter converter, TokenResolver resolver) {
            this.tokenToAuthenticationConverter = converter;
            this.tokenResolver = resolver;
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            try {
                if (!isAlreadyAuthenticated()) {
                    Optional<String> token = tokenResolver.resolveToken(servletRequest);
                    token.ifPresent(authenticateWithToken());
                }
            } finally {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }

        private boolean isAlreadyAuthenticated() {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context == null) {
                return false;
            }
            Authentication authentication = context.getAuthentication();
            if (authentication == null) {
                return false;
            }
            return authentication.isAuthenticated();
        }

        private Consumer<String> authenticateWithToken() {
            return token -> SecurityContextHolder.getContext()
                .setAuthentication(this.tokenToAuthenticationConverter.convertTokenToAuthentication(token));
        }
    }

    public static final class TokenResolver {

        public static final String TOKEN_PREFIX = "Bearer ";

        public static final String AUTHORIZATION_HEADER = "Authorization";

        static TokenResolver of(String secretKey) {
            return new TokenResolver(new TokenValidator(secretKey));
        }

        private final TokenValidator validator;

        private TokenResolver(TokenValidator validator) {
            this.validator = validator;
        }

        Optional<String> resolveToken(ServletRequest request) {
            return Optional.ofNullable(request)
                .map(HttpServletRequest.class::cast)
                .map(httpRequest -> httpRequest.getHeader(AUTHORIZATION_HEADER))
                .filter(StringUtils::hasText)
                .filter(token -> token.startsWith(TOKEN_PREFIX))
                .map(token -> token.substring(TOKEN_PREFIX.length()))
                .filter(validator::validateToken);
        }

    }

    static class TokenToAuthenticationConverter {

        static final String KEY_VALUE_DELIMITER = ",";

        private String secretKey;

        TokenToAuthenticationConverter(String secretKey) {
            this.secretKey = secretKey;
        }

        Authentication convertTokenToAuthentication(String token) {
            Claims claims = parseTokenToClaims(token);
            List<GrantedAuthority> authorities = convertClaimsToAuthorities(claims);
            UserPrincipal principal = new UserPrincipal(
                toUserInfoId(claims.get(userId.name(), String.class)),
                claims.get(firstName.name(), String.class),
                claims.get(lastName.name(), String.class),
                claims.get(email.name(), String.class),
                claims.get(externalId.name(), String.class),
                claims.get(langKey.name(), String.class)
            );
            principal.setAuthorities(authorities);
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        }

        private UserInfoId toUserInfoId(String value) {
            return new UserInfoId(value);
        }

        private List<GrantedAuthority> convertClaimsToAuthorities(Claims claims) {
            return Stream.of(claims)
                .map(claimMap -> claimMap.get(auth.name()))
                .map(Object::toString)
                .map(name -> name.split(KEY_VALUE_DELIMITER))
                .flatMap(Stream::of)
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        }

        private Claims parseTokenToClaims(String token) {
            return parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        }
    }

    static class TokenValidator {

        private final Logger log = LoggerFactory.getLogger(TokenValidator.class);

        private String secretKey;

        TokenValidator(String secretKey) {
            this.secretKey = secretKey;
        }

        boolean validateToken(String authToken) {
            try {
                parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(authToken);
                return true;
            } catch (SignatureException e) {
                log.info("Invalid JWT signature.");
                log.trace("Invalid JWT signature trace: {}", e);
            } catch (MalformedJwtException e) {
                log.info("Invalid JWT token.");
                log.trace("Invalid JWT token trace: {}", e);
            } catch (ExpiredJwtException e) {
                log.info("Expired JWT token.");
                log.trace("Expired JWT token trace: {}", e);
            } catch (UnsupportedJwtException e) {
                log.info("Unsupported JWT token.");
                log.trace("Unsupported JWT token trace: {}", e);
            } catch (IllegalArgumentException e) {
                log.info("JWT token compact of handler are invalid.");
                log.trace("JWT token compact of handler are invalid trace: {}", e);
            }
            return false;
        }
    }
}

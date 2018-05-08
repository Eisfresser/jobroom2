package ch.admin.seco.jobroom.security.jwt;

import java.util.Optional;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class TokenResolver {

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    static TokenResolver of(String secretKey) {
        return new TokenResolver(new TokenValidator(secretKey));
    }

    private final TokenValidator validator;

    TokenResolver(TokenValidator validator) {
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

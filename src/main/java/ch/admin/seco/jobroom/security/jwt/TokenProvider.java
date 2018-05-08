package ch.admin.seco.jobroom.security.jwt;

import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.mapUserAndAuthoritiesToClaims;
import static io.jsonwebtoken.Jwts.builder;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;

import java.util.Date;

import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Component;

import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.security.DomainUserPrincipal;

@Component
public class TokenProvider {

    private String secretKey;

    private long tokenValidityInMilliseconds;

    private long tokenValidityInMillisecondsForRememberMe;

    public TokenProvider(JHipsterProperties jHipsterProperties) {
        Jwt token = jHipsterProperties.getSecurity()
            .getAuthentication()
            .getJwt();
        this.secretKey = token.getSecret();
        this.tokenValidityInMilliseconds = 1000 * token.getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe = 1000 * token.getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        final Date expirationDate = calculateExpirationDate(rememberMe);
        Claims claims = mapUserAndAuthoritiesToClaims().apply(getUser(authentication), authentication.getAuthorities());
        return createToken(authentication.getName(), expirationDate, claims);
    }

    public DefaultOAuth2AccessToken createAccessToken(Authentication authentication) {
        String token = createToken(authentication, false);
        Date expirationDate = calculateExpirationDate(false);
        return createAccessToken(token, expirationDate);
    }

    private User getUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof DomainUserPrincipal) {
            final DomainUserPrincipal principal = (DomainUserPrincipal) authentication.getPrincipal();
            return principal.getUser();
        }
        return null;
    }

    private DefaultOAuth2AccessToken createAccessToken(String token, Date expirationDate) {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(token);
        oAuth2AccessToken.setExpiration(expirationDate);
        return oAuth2AccessToken;
    }

    private String createToken(String subject, Date validity, Claims claims) {
        return builder()
            .setClaims(claims)
            .setSubject(subject)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(validity)
            .compact();
    }

    private Date calculateExpirationDate(Boolean rememberMe) {
        return Date.from(now()
            .plus(rememberMe ? this.tokenValidityInMillisecondsForRememberMe : this.tokenValidityInMilliseconds, MILLIS)
            .atZone(systemDefault())
            .toInstant());
    }
}

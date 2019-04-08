package ch.admin.seco.jobroom.security.jwt;

import static io.jsonwebtoken.Jwts.builder;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;

import java.util.Date;

import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Component;

import ch.admin.seco.jobroom.security.UserPrincipal;

@Component
public class TokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    private String secretKey;

    private long tokenValidityInMilliseconds;

    private long tokenValidityInMillisecondsForRememberMe;

    private final ClaimMapper claimMapper;

    public TokenProvider(JHipsterProperties jHipsterProperties, ClaimMapper claimMapper) {
        this.claimMapper = claimMapper;
        Jwt token = jHipsterProperties.getSecurity()
            .getAuthentication()
            .getJwt();
        this.secretKey = token.getSecret();
        this.tokenValidityInMilliseconds = 1000 * token.getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe = 1000 * token.getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        return createToken(
            authentication.getName(),
            calculateExpirationDate(rememberMe),
            prepareClaims(authentication)
        );
    }

    public DefaultOAuth2AccessToken createAccessToken(Authentication authentication) {
        String token = createToken(authentication, false);
        Date expirationDate = calculateExpirationDate(false);
        return createAccessToken(token, expirationDate);
    }

    private Claims prepareClaims(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return this.claimMapper.map((UserPrincipal) principal);
        }
        throw new IllegalArgumentException("The principal in the authentication is of an unknown type " + principal.getClass().getSimpleName());
    }


    private DefaultOAuth2AccessToken createAccessToken(String token, Date expirationDate) {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(token);
        oAuth2AccessToken.setExpiration(expirationDate);
        return oAuth2AccessToken;
    }

    private String createToken(String subject, Date validity, Claims claims) {
        LOGGER.info("About to create Token for subject: {}", subject);
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

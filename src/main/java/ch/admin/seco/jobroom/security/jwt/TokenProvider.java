package ch.admin.seco.jobroom.security.jwt;

import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.LoginFormUserPrincipal;
import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static io.jsonwebtoken.Jwts.builder;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;

@Component
public class TokenProvider {

    private String secretKey;

    private long tokenValidityInMilliseconds;

    private long tokenValidityInMillisecondsForRememberMe;

    private final UserInfoRepository userInfoRepository;

    public TokenProvider(JHipsterProperties jHipsterProperties, UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
        Jwt token = jHipsterProperties.getSecurity()
            .getAuthentication()
            .getJwt();
        this.secretKey = token.getSecret();
        this.tokenValidityInMilliseconds = 1000 * token.getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe = 1000 * token.getTokenValidityInSecondsForRememberMe();
    }

    @Transactional
    public String createToken(Authentication authentication, boolean rememberMe) {
        return createToken(
            authentication.getName(),
            calculateExpirationDate(rememberMe),
            prepareClaims(authentication)
        );
    }

    private Claims prepareClaims(Authentication authentication) {
        if (authentication.getPrincipal() instanceof LoginFormUserPrincipal) {
            LoginFormUserPrincipal principal = (LoginFormUserPrincipal) authentication.getPrincipal();
            User user = principal.getUser();
            return NoEiamClaimMapper.mapUserAndAuthoritiesToClaims().apply(user, authentication.getAuthorities());
        } else if (authentication.getPrincipal() instanceof EiamUserPrincipal) {
            EiamUserPrincipal principal = (EiamUserPrincipal) authentication.getPrincipal();
            Optional<UserInfo> userInfo = this.userInfoRepository.findOneByUserExternalId(principal.getUserExtId());
            return ClaimMapper.mapUserAndAuthoritiesToClaims().apply(userInfo.get(), authentication.getAuthorities());
        } else if (authentication.getPrincipal() instanceof String) {
            return Jwts.claims();
        } else {
            throw new IllegalArgumentException("The principal in the authentication is of an unknown type " + authentication.getPrincipal().getClass().getSimpleName());
        }
    }

    public DefaultOAuth2AccessToken createAccessToken(Authentication authentication) {
        String token = createToken(authentication, false);
        Date expirationDate = calculateExpirationDate(false);
        return createAccessToken(token, expirationDate);
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

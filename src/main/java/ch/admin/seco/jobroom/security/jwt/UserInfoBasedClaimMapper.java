package ch.admin.seco.jobroom.security.jwt;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.*;
import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenToAuthenticationConverter.KEY_VALUE_DELIMITER;
import static java.util.stream.Collectors.joining;

@Component
@Transactional
public class UserInfoBasedClaimMapper implements ClaimMapper {

    private final UserInfoRepository userInfoRepository;

    public UserInfoBasedClaimMapper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public Claims map(UserPrincipal principal) {
        UserInfo userInfo = getUserInfo(principal);
        Claims claims = Jwts.claims();
        claims.put(auth.name(), toString(principal.getAuthorities()));
        claims.put(userId.name(), userInfo.getId().getValue());
        claims.put(firstName.name(), userInfo.getFirstName());
        claims.put(lastName.name(), userInfo.getLastName());
        claims.put(email.name(), userInfo.getEmail());
        claims.put(langKey.name(), userInfo.getLangKey());
        claims.put(externalId.name(), userInfo.getUserExternalId());
        if (userInfo.getCompany() != null) {
            claims.put(companyId.name(), userInfo.getCompany().getExternalId());
        }
        return claims;
    }

    private UserInfo getUserInfo(UserPrincipal principal) {
        return this.userInfoRepository.findOneWithAccountabilites(principal.getId())
            .orElseThrow(() -> new IllegalStateException("No User found"));
    }

    private String toString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(joining(KEY_VALUE_DELIMITER));
    }
}

package ch.admin.seco.jobroom.security.jwt;

import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.auth;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.companyId;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.email;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.externalId;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.firstName;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.langKey;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.lastName;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.userId;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.userProfileExtId;
import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenToAuthenticationConverter.KEY_VALUE_DELIMITER;
import static java.util.stream.Collectors.joining;

import java.util.Collection;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.UserPrincipal;

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
        if (StringUtils.isNotBlank(principal.getUserDefaultProfileExtId())) {
            claims.put(userProfileExtId.name(), principal.getUserDefaultProfileExtId());
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

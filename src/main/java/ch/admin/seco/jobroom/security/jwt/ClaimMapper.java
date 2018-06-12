package ch.admin.seco.jobroom.security.jwt;

import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.auth;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.companyId;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.email;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.firstName;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.lastName;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.phone;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.userExternalId;
import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.userId;
import static ch.admin.seco.jobroom.security.jwt.TokenToAuthenticationConverter.KEY_VALUE_DELIMITER;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.security.core.GrantedAuthority;

import ch.admin.seco.jobroom.domain.UserInfo;

final class ClaimMapper {

    private ClaimMapper() {
    }

    enum ClaimKey {
        auth, userExternalId, companyId, firstName, lastName, email, phone, userId;
    }

    static BiFunction<UserInfo, Collection<? extends GrantedAuthority>, Claims> mapUserAndAuthoritiesToClaims() {
        return (user, authorities) -> {
            Claims claims = Jwts.claims();
            joinAuthorityNamesWithComma().accept(claims, authorities);
            mapUserToClaims().accept(claims, user);
            return claims;
        };
    }

    private static BiConsumer<Claims, UserInfo> mapUserToClaims() {
        return (claims, user) -> {
            if (user != null) {
                claims.put(firstName.name(), user.getFirstName());
                claims.put(lastName.name(), user.getLastName());
                claims.put(email.name(), user.getEmail());
                claims.put(userId.name(), user.getId());
                claims.put(phone.name(), user.getPhone());
                claims.put(userExternalId.name(), user.getUserExternalId());
                if (user.getCompany() != null) {
                    claims.put(companyId.name(), user.getCompany().getId());
                }
            }
        };
    }

    static BiConsumer<Claims, Collection<? extends GrantedAuthority>> joinAuthorityNamesWithComma() {
        return (claims, authorities) -> claims.put(auth.name(), authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(joining(KEY_VALUE_DELIMITER)));
    }
}

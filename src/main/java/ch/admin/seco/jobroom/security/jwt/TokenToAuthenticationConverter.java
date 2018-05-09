package ch.admin.seco.jobroom.security.jwt;

import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.auth;
import static io.jsonwebtoken.Jwts.parser;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jsonwebtoken.Claims;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

class TokenToAuthenticationConverter {

    static final String KEY_VALUE_DELIMITER = ",";

    private String secretKey;

    TokenToAuthenticationConverter(String secretKey) {
        this.secretKey = secretKey;
    }

    Authentication convertTokenToAuthentication(String token) {
        Claims claims = parseTokenToClaims(token);
        Collection<? extends GrantedAuthority> authorities = convertClaimsToAuthorities(claims);
        User principal = new User(claims.getSubject(), EMPTY, authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Collection<? extends GrantedAuthority> convertClaimsToAuthorities(Claims claims) {
        return Stream.of(claims)
            .map(claimMap -> claimMap.get(auth.name()))
            .map(Object::toString)
            .map(name -> name.split(KEY_VALUE_DELIMITER))
            .flatMap(Stream::of)
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

package ch.admin.seco.jobroom.security.jwt;

import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.auth;
import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.companyId;
import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.email;
import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.firstName;
import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.lastName;
import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.phone;
import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.userExternalId;
import static ch.admin.seco.jobroom.security.jwt.NoEiamClaimMapper.ClaimKey.userId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import org.junit.Test;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ch.admin.seco.jobroom.domain.Organization;
import ch.admin.seco.jobroom.domain.User;

public class NoEiamClaimMapperTest {

    private static final String FIRSTNAME = "Hans";
    private static final String LASTNAME = "Muster";
    private static final String EMAIL = "hans.muster@mail.ch";
    private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String PHONE = "031123456789";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String COMPANY_ID = "12345";

    @Test
    public void mapUserAndAuthoritiesToClaims() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
        User user = new User();
        user.setFirstName(FIRSTNAME);
        user.setLastName(LASTNAME);
        user.setEmail(EMAIL);
        user.setId(ID);
        user.setPhone(PHONE);
        Organization organization = new Organization();
        organization.setId(ID);
        user.setOrganization(organization);

        Claims claims = NoEiamClaimMapper.mapUserAndAuthoritiesToClaims().apply(user, authorities);

        assertNotNull(claims);
        assertEquals(FIRSTNAME, claims.get(firstName.name()));
        assertEquals(LASTNAME, claims.get(lastName.name()));
        assertEquals(EMAIL, claims.get(email.name()));
        assertEquals(PHONE, claims.get(phone.name()));
        assertEquals(ID, claims.get(userId.name()));
        assertEquals(null, claims.get(userExternalId.name()));
        assertEquals(ID, claims.get(companyId.name()));
        assertEquals(ROLE_USER + "," + ROLE_ADMIN, claims.get(auth.name()));
    }

}

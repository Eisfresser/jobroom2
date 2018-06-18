package ch.admin.seco.jobroom.security.jwt;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.CompanyId;
import ch.admin.seco.jobroom.domain.UserInfo;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static ch.admin.seco.jobroom.security.jwt.ClaimMapper.ClaimKey.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClaimMapperTest {

    private static final String FIRSTNAME = "Hans";
    private static final String LASTNAME = "Muster";
    private static final String EMAIL = "hans.muster@mail.ch";
    private static final String USER_EXTID = "CH2102565";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String COMPANY_ID = "12345";

    @Test
    public void mapUserAndAuthoritiesToClaims() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
        UserInfo user = new UserInfo(FIRSTNAME, LASTNAME, EMAIL, USER_EXTID, Constants.DEFAULT_LANGUAGE);
        Company company = new Company("ACME AG", "CHE-1234");
        company.setId(new CompanyId(COMPANY_ID));
        user.addCompany(company);
        Claims claims = ClaimMapper.mapUserAndAuthoritiesToClaims().apply(user, authorities);

        assertNotNull(claims);
        assertEquals(FIRSTNAME, claims.get(firstName.name()));
        assertEquals(LASTNAME, claims.get(lastName.name()));
        assertEquals(EMAIL, claims.get(email.name()));
        assertEquals(USER_EXTID, claims.get(userExternalId.name()));
        assertEquals(new CompanyId(COMPANY_ID), claims.get(companyId.name()));
        assertEquals(ROLE_USER + "," + ROLE_ADMIN, claims.get(auth.name()));
    }

}

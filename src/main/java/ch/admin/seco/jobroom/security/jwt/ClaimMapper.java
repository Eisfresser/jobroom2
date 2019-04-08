package ch.admin.seco.jobroom.security.jwt;

import io.jsonwebtoken.Claims;

import ch.admin.seco.jobroom.security.UserPrincipal;

public interface ClaimMapper {

    Claims map(UserPrincipal userPrincipal);

    enum ClaimKey {
        auth, companyId, firstName, lastName, email, userId, langKey, externalId, userProfileExtId
    }
}

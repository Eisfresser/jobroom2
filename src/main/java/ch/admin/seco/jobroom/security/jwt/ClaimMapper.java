package ch.admin.seco.jobroom.security.jwt;

import ch.admin.seco.jobroom.security.UserPrincipal;
import io.jsonwebtoken.Claims;

public interface ClaimMapper {

    Claims map(UserPrincipal userPrincipal);

    enum ClaimKey {
        auth, companyId, firstName, lastName, email, userId, langKey, externalId
    }
}

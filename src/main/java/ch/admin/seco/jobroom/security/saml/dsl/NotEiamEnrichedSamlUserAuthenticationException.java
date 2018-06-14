package ch.admin.seco.jobroom.security.saml.dsl;

import ch.admin.seco.jobroom.security.saml.infrastructure.SamlUser;
import org.springframework.security.core.AuthenticationException;

public class NotEiamEnrichedSamlUserAuthenticationException extends AuthenticationException {

    public NotEiamEnrichedSamlUserAuthenticationException(SamlUser samlUser) {
        super("User is not a eiam-enriched-user: " + samlUser);
    }
}

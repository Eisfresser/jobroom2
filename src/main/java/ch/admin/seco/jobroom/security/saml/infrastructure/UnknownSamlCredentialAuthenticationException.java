package ch.admin.seco.jobroom.security.saml.infrastructure;

import org.springframework.security.core.AuthenticationException;

class UnknownSamlCredentialAuthenticationException extends AuthenticationException {

    UnknownSamlCredentialAuthenticationException(String msg) {
        super(msg);
    }
}

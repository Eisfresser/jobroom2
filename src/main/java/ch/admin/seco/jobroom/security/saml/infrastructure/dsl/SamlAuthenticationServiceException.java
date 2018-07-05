package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Provides the StatusCode and StatusMessage in order to distinct SAML failure behaviours.
 */
public class SamlAuthenticationServiceException extends AuthenticationServiceException {

    private final String statusCode;

    private final String statusMessage;

    SamlAuthenticationServiceException(AuthenticationServiceException e, String statusCode, String statusMessage) {
        super(prepareMessage(e, statusCode, statusMessage), e.getCause());
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    private static String prepareMessage(AuthenticationServiceException e, String statusCode, String statusMessage) {
        return e.getMessage() + " - Assertion Status-Code: " + statusCode + ", Status-Message: " + statusMessage;
    }
}

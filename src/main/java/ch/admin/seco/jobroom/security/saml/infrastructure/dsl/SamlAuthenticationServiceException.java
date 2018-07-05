package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Provides the StatusCode and StatusMessage in order to distinct SAML failure behaviours.
 */
public class SamlAuthenticationServiceException extends AuthenticationServiceException {

    private final List<String> statusCodes;

    private final String statusMessage;

    SamlAuthenticationServiceException(AuthenticationServiceException e, List<String> statusCodes, String statusMessage) {
        super(prepareMessage(e, statusCodes, statusMessage), e.getCause());
        this.statusCodes = statusCodes;
        this.statusMessage = statusMessage;
    }

    public List<String> getStatusCodes() {
        return statusCodes;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    private static String prepareMessage(AuthenticationServiceException e, List<String> statusCodes, String statusMessage) {
        return e.getMessage() + " - Assertion Status-Codes: " + toString(statusCodes) + ", Status-Message: " + statusMessage;
    }

    private static String toString(List<String> statusCodes) {
        if (statusCodes == null) {
            return "";
        }
        return statusCodes.stream().collect(Collectors.joining(";"));
    }
}

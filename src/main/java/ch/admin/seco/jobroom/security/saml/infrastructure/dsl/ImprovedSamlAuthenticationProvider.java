package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.common.SAMLObject;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.context.SAMLMessageContext;

/**
 * This SAMLAuthenticationProvider is improving the Saml Exception flow by extracting
 * the StatusCode and StatusMessage from the SAMLAuthenticationToken and provide them
 * in the SamlAuthenticationServiceException. Can but used for later usage such as in a
 *  {@link org.springframework.security.web.authentication.AuthenticationFailureHandler}
 */
class ImprovedSamlAuthenticationProvider extends SAMLAuthenticationProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImprovedSamlAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            return super.authenticate(authentication);
        } catch (AuthenticationServiceException e) {
            if (!(authentication instanceof SAMLAuthenticationToken)) {
                LOGGER.warn("Expected Authentication to be of type: SAMLAuthenticationToken but was {}", authentication.getClass().getSimpleName());
                throw e;
            }
            SAMLAuthenticationToken samlAuthenticationToken = (SAMLAuthenticationToken) authentication;
            Response response = extractResponse(samlAuthenticationToken, e);
            if (response == null) {
                LOGGER.warn("No response is available");
                throw e;
            }
            List<String> statusCodes = extractStatusCodes(response);
            String statusMessage = extractStatusMessage(response);
            throw new SamlAuthenticationServiceException(e, statusCodes, statusMessage);
        }
    }

    private String extractStatusMessage(Response response) {
        Status responseStatus = response.getStatus();
        if (responseStatus == null) {
            return null;
        }
        StatusMessage statusMessage = responseStatus.getStatusMessage();
        if (statusMessage == null) {
            return null;
        }
        return statusMessage.getMessage();
    }

    List<String> extractStatusCodes(Response response) {
        Status responseStatus = response.getStatus();
        if (responseStatus == null) {
            return Collections.emptyList();
        }
        StatusCode statusCode = responseStatus.getStatusCode();
        if (statusCode == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        result.add(statusCode.getValue());
        while (statusCode.getStatusCode() != null) {
            statusCode = statusCode.getStatusCode();
            result.add(statusCode.getValue());
        }
        return result;
    }

    private Response extractResponse(SAMLAuthenticationToken samlAuthenticationToken, AuthenticationServiceException e) {
        SAMLMessageContext samlMessageContext = samlAuthenticationToken.getCredentials();
        SAMLObject message = samlMessageContext.getInboundSAMLMessage();
        if (!(message instanceof Response)) {
            LOGGER.warn("Expected Message to be a Response but was {}", message.getClass().getSimpleName());
            throw e;
        }
        return (Response) message;
    }
}

package ch.admin.seco.jobroom.security.saml;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.opensaml.saml2.core.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import ch.admin.seco.jobroom.security.saml.infrastructure.dsl.SamlAuthenticationServiceException;

public class SamlAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamlAuthenticationFailureHandler.class);

    private static final HttpStatus ERROR_HTTP_STATUS = HttpStatus.I_AM_A_TEAPOT;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final String homePageUrl;

    private final boolean redirectToHomeOnCancellation;

    public SamlAuthenticationFailureHandler(String homePageUrl, boolean redirectToHomeOnCancellation) {
        this.homePageUrl = Preconditions.checkNotNull(homePageUrl);
        this.redirectToHomeOnCancellation = redirectToHomeOnCancellation;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        if (exception instanceof SamlAuthenticationServiceException) {
            SamlAuthenticationServiceException samlAuthenticationServiceException = (SamlAuthenticationServiceException) exception;
            if (isCancelAuthentication(samlAuthenticationServiceException) && this.redirectToHomeOnCancellation) {
                LOGGER.warn("Saml authentication failure looks like cancellation -> redirect to {}", this.homePageUrl);
                this.redirectStrategy.sendRedirect(request, response, this.homePageUrl);
                return;
            }
        }
        LOGGER.warn("Saml authentication failure is unknown ->  send error-status: {}", ERROR_HTTP_STATUS);
        response.sendError(ERROR_HTTP_STATUS.value(), ERROR_HTTP_STATUS.getReasonPhrase());
    }

    /**
     *
     * Decides whether the status-code and status-messages matches the one that is being
     * send to use from eiam once the user hits the cancel button.
     * <br/>
     * Eiam sends us the following Assertion once the user hits the cancel button:
     *
     <pre>
     * {@code
     *
     * <saml2p:Response xmlns:saml2p="urn:oasis:names:tc:SAML:2.0:protocol"
     *                  Destination="https://dev.job-room.ch/saml/SSO"
     *                  ID="Response_821d0e699950cade13608ad2ef8b0dc386bf8097"
     *                  InResponseTo="a36b20a846c3b3a328f4374j2c2ii1c"
     *                  IssueInstant="2018-07-04T23:57:24.653Z" Version="2.0">
     *     <saml2:Issuer xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion">
     *         urn:eiam.admin.ch:pep:ALV-jobroom-R
     *     </saml2:Issuer>
     *     <saml2p:Status>
     *         <saml2p:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Responder">
     *             <saml2p:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:AuthnFailed"/>
     *         </saml2p:StatusCode>
     *     </saml2p:Status>
     * </saml2p:Response>
     * }
     * </pre>
     *
     * @param exception the {@link SamlAuthenticationServiceException}
     * @return true if the saml-assertion contains a responder-status-code AND a failed-status-code AND the status-message is null
     */
    private boolean isCancelAuthentication(SamlAuthenticationServiceException exception) {
        List<String> statusCodes = exception.getStatusCodes();
        return statusCodes.contains(StatusCode.AUTHN_FAILED_URI)
            && statusCodes.contains(StatusCode.RESPONDER_URI)
            && StringUtils.isBlank(exception.getStatusMessage());
    }

}

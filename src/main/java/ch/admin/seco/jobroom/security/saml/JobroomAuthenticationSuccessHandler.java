package ch.admin.seco.jobroom.security.saml;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer;

/**
 * The success handler is called after the user has been authenticated successfully. It is
 * mainly used to redirect the user to the appropriate page (application or one of the
 * Jobroom registration pages) depending on the user's state.
 * The user can now be in one of the following states:
 * 1) principal.needsRegistration=true -> first time user -> start registration process (the UserInfo contains only data from SAML assertion; no UserInfo database entry exists)
 * 2) principal.userInfo.registrationStatus=UNREGISTERED  -> user has not yet started registration -> this actually the same as 1)
 * 3) principal.userInfo.registrationStatus=VALIDATION_EMP or VALIDATION_PAV -> (the UserInfo in the database contains an accessCode; user is lead to access code page)
 * 4) principal.userInfo.registrationStatus=REGISTERED -> fully registered user -> send to default authentication success handler
 */
public class JobroomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final static Logger LOG = LoggerFactory.getLogger(JobroomAuthenticationSuccessHandler.class);

    private final String targetUrlEiamAccessRequest;

    public JobroomAuthenticationSuccessHandler(String targetUrlEiamAccessRequest) {
        this.targetUrlEiamAccessRequest = targetUrlEiamAccessRequest;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws ServletException, IOException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Found roles: " + authentication.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(", ")));
        }

        if (hasJobRoomAllowRole(authentication)) {
            if (authentication.getPrincipal() instanceof EiamUserPrincipal) {
                EiamUserPrincipal principal = (EiamUserPrincipal) authentication.getPrincipal();
                handleAuthenticatedUser(authentication, principal, request, response);
            } else {
                throw new MissingPrincipalException();
            }
        } else {
            // if user has no Jobroom role, he/she must be sent to the access request page of eIAM
            redirectTo(this.targetUrlEiamAccessRequest, request, response);
        }
    }

    private void handleAuthenticatedUser(Authentication authentication, EiamUserPrincipal principal, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (principal.isNeedsRegistration()) {
            // first-time user -> send to registration process
            redirectTo(SAMLConfigurer.TARGET_URL_REGISTRATION_PROCESS, request, response);
        } else {
            UserInfo userInfo = principal.getUser();
            if (userInfo.getRegistrationStatus() == RegistrationStatus.UNREGISTERED) {
                throw new IllegalArgumentException("The user's needsRegistration flag is set to 'false', but it's status is UNREGISTERED");
            }
            if (userInfo.getRegistrationStatus() == RegistrationStatus.VALIDATION_EMP
                || userInfo.getRegistrationStatus() == RegistrationStatus.VALIDATION_PAV) {
                // PAV and company users need 2-factor authentication!
                if (principal.hasOnlyOneFactorAuthentication()) {
                    // send to 2 factor setup in eIAM
                    redirectTo(SAMLConfigurer.TARGET_URL_FORCE_TWO_FACTOR_AUTH, request, response);
                    authentication.setAuthenticated(false);
                } else {
                    // if user has 2-factor authentication -> show page for entering access code
                    redirectTo(SAMLConfigurer.TARGET_URL_ENTER_ACCESS_CODE, request, response);
                }
            } else {
                // make sure PAV and company users are authenticated with 2-factor!
                // this is a functionality which should be provided by a future PeP implementation
                if ((isAgent(authentication) || isEmployer(authentication)) && principal.hasOnlyOneFactorAuthentication()) {
                    // send to 2 factor setup in eIAM
                    redirectTo(SAMLConfigurer.TARGET_URL_FORCE_TWO_FACTOR_AUTH, request, response);
                    authentication.setAuthenticated(false);
                }
                // if user is registered in jobroom process with default authentication success handler
                super.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }

    private boolean hasJobRoomAllowRole(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ROLE_ALLOW));
    }

    private boolean isAgent(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ROLE_PRIVATE_EMPLOYMENT_AGENT));
    }

    private boolean isEmployer(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ROLE_COMPANY));
    }

    private void redirectTo(String targetUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Redirecting to target url: " + targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        clearAuthenticationAttributes(request);
    }

}

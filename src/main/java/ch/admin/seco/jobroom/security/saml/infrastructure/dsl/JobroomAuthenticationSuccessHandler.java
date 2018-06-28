package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.UserPrincipal;

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

    private final UserInfoRepository userInfoRepository;

    private final Map<RegistrationStatus, RegistrationStatusStrategy> registrationStatusStrategyMap = new HashMap<>();

    private final AuthenticationEventPublisher authenticationEventPublisher;

    JobroomAuthenticationSuccessHandler(String targetUrlEiamAccessRequest, UserInfoRepository userInfoRepository, AuthenticationEventPublisher authenticationEventPublisher) {
        this.targetUrlEiamAccessRequest = targetUrlEiamAccessRequest;
        this.userInfoRepository = userInfoRepository;
        this.authenticationEventPublisher = authenticationEventPublisher;
        this.registrationStatusStrategyMap.put(RegistrationStatus.UNREGISTERED, this::redirectToRegistrationPage);
        this.registrationStatusStrategyMap.put(RegistrationStatus.VALIDATION_EMP, this::redirectToAccessCodePage);
        this.registrationStatusStrategyMap.put(RegistrationStatus.VALIDATION_PAV, this::redirectToAccessCodePage);
        this.registrationStatusStrategyMap.put(RegistrationStatus.REGISTERED, super::onAuthenticationSuccess);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws ServletException, IOException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Found roles: " + rolesAsString(authentication));
        }

        Object principal = authentication.getPrincipal();
        if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
            throw new AuthenticationServiceException("Expected Principal to be of type 'UserDetails' but was " + principal.getClass());
        }

        UserDetails userDetails = (UserDetails) principal;
        if (!hasJobRoomAllowRole(userDetails.getAuthorities())) {
            logger.info("User '" + userDetails.getUsername() + "' doesn't have the ALLOW role -> redirect to eiam");
            redirectTo(this.targetUrlEiamAccessRequest, request, response);
            this.authenticationEventPublisher.publishAuthenticationSuccess(authentication);
            return;
        }

        if (!(userDetails instanceof UserPrincipal)) {
            throw new AuthenticationServiceException("Expected Principal to be of type 'UserPrincipal' but was " + principal.getClass());
        }
        UserPrincipal userPrincipal = (UserPrincipal) principal;
        handleAuthenticatedUser(authentication, userPrincipal, request, response);
        this.authenticationEventPublisher.publishAuthenticationSuccess(authentication);
    }

    private void handleAuthenticatedUser(Authentication authentication, UserPrincipal userPrincipal, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UserInfo userInfo = userInfoRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new IllegalStateException("No user found with Id: " + userPrincipal.getId().getValue()));

        if (this.isAdmin(authentication)) {
            logger.debug("User is Admin -> redirect to home");
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // TODO make sure PAV and company users are authenticated with 2-factor and if
        // not redirect them with a different "authnContexts" to eiam

        RegistrationStatus registrationStatus = userInfo.getRegistrationStatus();
        if (registrationStatus == null) {
            logger.warn("User's registration status is: null -> redirect to home");
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        RegistrationStatusStrategy registrationStatusStrategy = this.registrationStatusStrategyMap.get(registrationStatus);
        if (registrationStatusStrategy == null) {
            logger.warn("No Registration redirect strategy defined for: " + registrationStatus + " -> redirect to home");
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        registrationStatusStrategy.handleRedirect(request, response, authentication);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ROLE_ADMIN)
                || a.getAuthority().equals(AuthoritiesConstants.ROLE_SYSADMIN));
    }

    private String rolesAsString(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(Object::toString)
            .collect(Collectors.joining(", "));
    }

    private void redirectTo(String targetUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Redirecting to target url: " + targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        clearAuthenticationAttributes(request);
    }

    private void redirectToRegistrationPage(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        redirectTo(SAMLConfigurer.TARGET_URL_REGISTRATION_PROCESS, request, response);
    }

    private void redirectToAccessCodePage(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        redirectTo(SAMLConfigurer.TARGET_URL_ENTER_ACCESS_CODE, request, response);
    }

    private boolean hasJobRoomAllowRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ROLE_ALLOW));
    }

    interface RegistrationStatusStrategy {

        void handleRedirect(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException;

    }

}

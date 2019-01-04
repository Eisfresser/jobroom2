package ch.admin.seco.jobroom.security.saml;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.service.logging.BusinessLogEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import static ch.admin.seco.jobroom.service.logging.BusinessLogEventType.USER_LOGIN;

public class SamlAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final String eiamAccessRequestTargetUrl;

    private final UserInfoRepository userInfoRepository;

    private final AuthenticationEventPublisher authenticationEventPublisher;

    private final ApplicationEventPublisher applicationEventPublisher;

    public SamlAuthenticationSuccessHandler(String eiamAccessRequestTargetUrl, UserInfoRepository userInfoRepository, AuthenticationEventPublisher authenticationEventPublisher, ApplicationEventPublisher applicationEventPublisher) {
        this.eiamAccessRequestTargetUrl = eiamAccessRequestTargetUrl;
        this.authenticationEventPublisher = authenticationEventPublisher;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Found roles: " + rolesAsString(authentication));
        }

        Object principal = authentication.getPrincipal();
        if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
            throw new AuthenticationServiceException("Expected Principal to be of type 'UserDetails' but was " + principal.getClass());
        }

        UserDetails userDetails = (UserDetails) principal;
        if (!hasJobRoomAllowRole(userDetails.getAuthorities())) {
            logger.info("User '" + userDetails.getUsername() + "' doesn't have the ALLOW role -> redirect to eiam");
            redirectTo(this.eiamAccessRequestTargetUrl, request, response);
            this.authenticationEventPublisher.publishAuthenticationSuccess(authentication);
            return;
        }

        UserPrincipal userPrincipal = (UserPrincipal) principal;
        UserInfo userInfo = userInfoRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new IllegalStateException("No user found with Id: " + userPrincipal.getId().getValue()));

        applicationEventPublisher.publishEvent(BusinessLogEvent.of(USER_LOGIN).withObjectId(userInfo.getId().getValue()));

        super.onAuthenticationSuccess(request, response, authentication);
        this.authenticationEventPublisher.publishAuthenticationSuccess(authentication);
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

    private boolean hasJobRoomAllowRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ROLE_ALLOW));
    }

}

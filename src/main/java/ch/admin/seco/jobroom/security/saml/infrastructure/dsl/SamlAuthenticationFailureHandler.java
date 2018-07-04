package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class SamlAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    SamlAuthenticationFailureHandler(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        this.applicationEventPublisher.publishEvent(new SamlAuthenticationFailureEvent(new SamlFailedAuthentication(request), exception));
        response.sendError(HttpStatus.I_AM_A_TEAPOT.value(), HttpStatus.I_AM_A_TEAPOT.getReasonPhrase());
    }

    public static class SamlFailedAuthentication extends AbstractAuthenticationToken {

        SamlFailedAuthentication(HttpServletRequest httpServletRequest) {
            super(Collections.emptyList());
            super.setDetails(new WebAuthenticationDetails(httpServletRequest));
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }
    }

    static class SamlAuthenticationFailureEvent extends AbstractAuthenticationFailureEvent {

        SamlAuthenticationFailureEvent(Authentication authentication, AuthenticationException exception) {
            super(authentication, exception);
        }
    }

}

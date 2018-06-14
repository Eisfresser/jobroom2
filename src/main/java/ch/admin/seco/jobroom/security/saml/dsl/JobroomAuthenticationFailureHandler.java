package ch.admin.seco.jobroom.security.saml.dsl;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JobroomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final String targetUrlEiamAccessRequest;

    JobroomAuthenticationFailureHandler(String targetUrlEiamAccessRequest) {
        this.targetUrlEiamAccessRequest = targetUrlEiamAccessRequest;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof NotEiamEnrichedSamlUserAuthenticationException) {
            redirectTo(this.targetUrlEiamAccessRequest, request, response);
            return;
        }
        super.onAuthenticationFailure(request, response, exception);
    }

    private void redirectTo(String targetUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Redirecting to target url: " + targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

package ch.admin.seco.jobroom.security.saml;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class JobroomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private String targetUrlEiamAccessRequest;
    private Set<String> jobroomRoles;

    public JobroomAuthenticationSuccessHandler(String targetUrlEiamAccessRequest, Set<String> jobroomRoles) {
        this.targetUrlEiamAccessRequest = targetUrlEiamAccessRequest;
        this.jobroomRoles = jobroomRoles;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
        throws ServletException, IOException {

        // check, if user has any Jobroom role
        if (authentication.getAuthorities().stream().anyMatch(a -> this.jobroomRoles.contains(a.getAuthority()))) {
            super.onAuthenticationSuccess(request, response, authentication);
        } else {
            // if user has now Jobroom role, he/she must be sent to the access request page of eIAM
            redirectTo(this.targetUrlEiamAccessRequest, request, response);
        }
    }

    private void redirectTo(String targetUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Redirecting to target url: " + targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        clearAuthenticationAttributes(request);
    }

}

package ch.admin.seco.jobroom.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.admin.seco.jobroom.config.LoginRedirectURISessionAttribute;

@Controller
@RequestMapping
public class AuthenticationRedirectResource {

    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationRedirectResource.class);

    private static final String REDIRECT_PREFIX = "redirect:";

    private final LoginRedirectURISessionAttribute loginRedirectURISessionAttribute;

    @Value("${security.user.logout_url}")
    private String logoutURL;

    @Value("${security.user.profile_url}")
    private String profileURL;

    @Value("${security.user.login_url}")
    private String loginUrl;

    public AuthenticationRedirectResource(LoginRedirectURISessionAttribute loginRedirectURISessionAttribute) {
        this.loginRedirectURISessionAttribute = loginRedirectURISessionAttribute;
    }

    @GetMapping("/authentication/logout")
    public String getUserLogoutUrl() {
        return REDIRECT_PREFIX + logoutURL;
    }

    @GetMapping("/authentication/profile")
    public String getUserProfileUrl() {
        return REDIRECT_PREFIX + profileURL;
    }

    @GetMapping("/login")
    public String getLoginUrl(@RequestParam String redirectUrl) {
        this.loginRedirectURISessionAttribute.setAbsoluteRedirectURI(redirectUrl);
        return REDIRECT_PREFIX + loginUrl;
    }

    @GetMapping("/samllogin")
    public String samlLogin() {
        String sessionRedirectURI = this.loginRedirectURISessionAttribute.getRedirectURI();
        String redirectURI = StringUtils.isEmpty(sessionRedirectURI)
            ? REDIRECT_PREFIX + "/"
            : REDIRECT_PREFIX + sessionRedirectURI;
        LOG.debug("Redirect URI is " + redirectURI);
        return redirectURI;
    }
}

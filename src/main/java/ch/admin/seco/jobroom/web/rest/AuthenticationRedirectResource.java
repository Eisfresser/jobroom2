package ch.admin.seco.jobroom.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AuthenticationRedirectResource {

    private static final String REDIRECT_PREFIX = "redirect:";

    @Value("${security.user.logout_url}")
    private String logoutURL;

    @Value("${security.user.profile_url}")
    private String profileURL;

    @Value("${security.user.login_url}")
    private String loginUrl;

    public AuthenticationRedirectResource() {
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
    public String getLoginUrl() {
        return REDIRECT_PREFIX + loginUrl;
    }

    @GetMapping("/samllogin")
    public String samlLogin() {
        return REDIRECT_PREFIX + "/";
    }

}

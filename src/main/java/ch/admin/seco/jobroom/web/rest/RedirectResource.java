package ch.admin.seco.jobroom.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/redirect")
public class RedirectResource {

    private static final String REDIRECT_PREFIX = "redirect:";

    @Value("${security.user.logout_url}")
    private String logoutURL;

    @Value("${security.user.profile_url}")
    private String profileURL;

    public RedirectResource() {
    }

    @GetMapping("/logout")
    public String getUserLogoutUrl() {
        return REDIRECT_PREFIX + logoutURL;
    }

    @GetMapping("/profile")
    public String getUserProfileUrl() {
        return REDIRECT_PREFIX + profileURL;
    }

}

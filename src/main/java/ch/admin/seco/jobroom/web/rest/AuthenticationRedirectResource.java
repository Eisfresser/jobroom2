package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.logging.BusinessLogData;
import ch.admin.seco.jobroom.service.logging.BusinessLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static ch.admin.seco.jobroom.service.logging.BusinessLogEventType.USER_LOGGED_OUT_EVENT;
import static ch.admin.seco.jobroom.service.logging.BusinessLogObjectType.USER;
import static org.apache.commons.lang.WordUtils.capitalize;

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

    private final BusinessLogger businessLogger;

    private final CurrentUserService currentUserService;

    public AuthenticationRedirectResource(BusinessLogger businessLogger, CurrentUserService currentUserService) {
        this.businessLogger = businessLogger;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/authentication/logout")
    public String getUserLogoutUrl() {
        businessLogger.log(BusinessLogData.of(USER_LOGGED_OUT_EVENT)
            .withObjectType(capitalize(USER.name()))
            .withObjectId(currentUserService.getPrincipal().getId().getValue()));
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

package ch.admin.seco.jobroom.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SamlLoginController {

    private static final String REDIRECT_PREFIX = "redirect:";

    @GetMapping("/samllogin")
    public String home() {
        return REDIRECT_PREFIX + "/";
    }

}

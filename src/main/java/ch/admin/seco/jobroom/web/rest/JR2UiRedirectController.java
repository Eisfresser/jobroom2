package ch.admin.seco.jobroom.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JR2UiRedirectController {

    @GetMapping("/jr2/")
    public String index() {
        return "forward:/jr2/index.html";
    }

}

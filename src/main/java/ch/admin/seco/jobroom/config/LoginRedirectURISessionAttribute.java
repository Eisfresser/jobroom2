package ch.admin.seco.jobroom.config;

import org.springframework.beans.factory.annotation.Value;

public class LoginRedirectURISessionAttribute {

    @Value("${security.user.redirect-base-url}")
    private String baseUrl;

    private String redirectURI;

    public String getRedirectURI() {
        return redirectURI;
    }

    public void setAbsoluteRedirectURI(String redirectURI) {
        this.redirectURI = baseUrl + redirectURI;
        System.out.println("set redirect url: " + this.redirectURI);
    }
}

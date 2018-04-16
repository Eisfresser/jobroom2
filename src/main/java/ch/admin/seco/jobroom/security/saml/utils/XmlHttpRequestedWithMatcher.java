package ch.admin.seco.jobroom.security.saml.utils;

import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class XmlHttpRequestedWithMatcher implements RequestMatcher {

    private static final RequestMatcher MATCHER = new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");

    @Override
    public boolean matches(HttpServletRequest request) {
        return MATCHER.matches(request);
    }
}

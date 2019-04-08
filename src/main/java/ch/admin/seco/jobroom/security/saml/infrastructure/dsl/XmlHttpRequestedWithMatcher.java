package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

class XmlHttpRequestedWithMatcher implements RequestMatcher {

    private static final RequestMatcher MATCHER = new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");

    @Override
    public boolean matches(HttpServletRequest request) {
        return MATCHER.matches(request);
    }
}

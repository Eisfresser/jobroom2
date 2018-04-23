package ch.admin.seco.jobroom.security.saml;

import ch.admin.seco.jobroom.security.saml.utils.ToBeRemovedTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTAuthenticationStrategy implements SessionAuthenticationStrategy {

    //private TokenProvider tokenProvider;

    /**
     * Creates a new instance
     */
    // TODO: tokenProvider must be set by the caller and injected into the caller; but this currently does not work
    public JWTAuthenticationStrategy() { //TokenProvider tokenProvider) {
        //Assert.notNull(tokenProvider, "TokenProvider cannot be null");
        //this.tokenProvider = tokenProvider;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.session.
     * SessionAuthenticationStrategy
     * #onAuthentication(org.springframework.security.core.Authentication,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void onAuthentication(Authentication authentication,
                                 HttpServletRequest request, HttpServletResponse response)
        throws SessionAuthenticationException {

        // TODO: implement "remember me"
        boolean rememberMe = false;
        // TODO: generate JWT via injected tokenProvider
        ToBeRemovedTokenProvider tokenProvider = new ToBeRemovedTokenProvider();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        //  response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);  // setting the header here does not help, because it is removed during the redirect
        // setting the jwt as a cookie, because the cookie "survives" the redirect. JS reads the cookie and stores it in the session storage
        settingJwtAsShortLivedCookie(jwt, response);
    }

    void settingJwtAsShortLivedCookie(String jwt, HttpServletResponse response){
        final String cookieName = "jwt";
        final String cookieValue = jwt;
        final Boolean useSecureCookie = false;
        final int expiryTime = 60 * 10;  // 10 minutes -> reduce to 60 seconds
        final String cookiePath = "/";

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setSecure(useSecureCookie);  // determines whether the cookie should only be sent using a secure protocol, such as HTTPS or SSL
        cookie.setMaxAge(expiryTime);  // A negative value means that the cookie is not stored persistently and will be deleted when the Web browser exits. A zero value causes the cookie to be deleted.
        cookie.setPath(cookiePath);  // The cookie is visible to all the pages in the directory you specify, and all the pages in that directory's subdirectories
        response.addCookie(cookie);
    }
}

package ch.admin.seco.jobroom.security.saml;

import ch.admin.seco.jobroom.security.jwt.JWTConfigurer;
import ch.admin.seco.jobroom.security.jwt.TokenProvider;
import ch.admin.seco.jobroom.web.rest.UserJWTController;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.Assert;

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
        // TODO: generate real JWT via tokenProvider
        //String jwt = tokenProvider.createToken(authentication, rememberMe);
        String jwt = "THIS_IS_A_DUMMY_JWT_JUST_FOR_TESTING";
        response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);

    }
}

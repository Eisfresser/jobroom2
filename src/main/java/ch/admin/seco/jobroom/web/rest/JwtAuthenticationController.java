package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.security.jwt.TokenProvider;
import ch.admin.seco.jobroom.web.rest.vm.LoginVM;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.AUTHORIZATION_HEADER;
import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.TOKEN_PREFIX;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class JwtAuthenticationController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationController(TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    @Timed
    public ResponseEntity authorize(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticateWithUsernamePasswordAndRequest(loginVM.getUsername(), loginVM.getPassword(), request);
        Boolean rememberMe = Optional.ofNullable(loginVM.isRememberMe()).orElse(false);
        String token = tokenProvider.createToken(authentication, rememberMe);
        return new ResponseEntity<>(new JWTToken(token), httpHeaders(token), HttpStatus.OK);
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Timed
    public ResponseEntity authorizeOauth(@RequestParam String username, @RequestParam String password,
                                         HttpServletRequest request) {
        Authentication authentication = authenticateWithUsernamePasswordAndRequest(username, password, request);
        final DefaultOAuth2AccessToken accessToken = tokenProvider.createAccessToken(authentication);
        return ok(accessToken);
    }

    private Authentication authenticateWithUsernamePasswordAndRequest(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = this.authenticationManager.authenticate(token);
        SecurityContextHolder.getContext()
            .setAuthentication(authentication);

        return authentication;
    }

    private static HttpHeaders httpHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, TOKEN_PREFIX + token);
        return httpHeaders;
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }

    }
}

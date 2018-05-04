package ch.admin.seco.jobroom.web.rest;

import static ch.admin.seco.jobroom.security.jwt.JWTFilter.TOKEN_PREFIX;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.repository.UserRepository;
import ch.admin.seco.jobroom.security.jwt.JWTFilter;
import ch.admin.seco.jobroom.security.jwt.TokenProvider;
import ch.admin.seco.jobroom.web.rest.vm.LoginVM;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public UserJWTController(TokenProvider tokenProvider, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    @Timed
    public ResponseEntity authorize(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticateWithUsernamePasswordAndRequest(loginVM.getUsername(), loginVM.getPassword(), request);
        String token = createJwtToken(loginVM, authentication);
        HttpHeaders httpHeaders = createHttpHeaders(token);

        return new ResponseEntity<>(new JWTToken(token), httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Timed
    public ResponseEntity authorizeOauth(@RequestParam String username, @RequestParam String password,
                                         HttpServletRequest request) {
        return userRepository.findOneByLogin(username)
                             .map(user -> {
                                 Authentication authentication = authenticateWithUsernamePasswordAndRequest(username, password, request);
                                 DefaultOAuth2AccessToken accessToken = tokenProvider.createAccessToken(authentication, user);
                                 return ok(accessToken);
                             })
                             .orElseGet(status(UNAUTHORIZED)::build);
    }

    private Authentication authenticateWithUsernamePasswordAndRequest(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = this.authenticationManager.authenticate(token);
        SecurityContextHolder.getContext()
                             .setAuthentication(authentication);

        return authentication;
    }

    private HttpHeaders createHttpHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, TOKEN_PREFIX + token);

        return httpHeaders;
    }

    private String createJwtToken(@Valid @RequestBody LoginVM loginVM, Authentication authentication) {
        final Boolean rememberMe = Optional.ofNullable(loginVM.isRememberMe())
                                           .orElse(false);

        return userRepository.findOneByLogin(loginVM.getUsername())
                             .map(user -> tokenProvider.createToken(authentication, rememberMe, user))
                             .orElse(EMPTY);
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

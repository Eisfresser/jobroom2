package ch.admin.seco.jobroom.web.rest;

import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.AUTHORIZATION_HEADER;
import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.TOKEN_PREFIX;

import java.util.HashSet;
import java.util.Set;

import com.codahale.metrics.annotation.Timed;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.security.jwt.TokenProvider;
import ch.admin.seco.jobroom.service.CurrentUserService;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api/current-user")
public class CurrentUserResource {

    private final CurrentUserService currentUserService;

    private final TokenProvider tokenProvider;

    private final CurrentUserMapper currentUserMapper;

    public CurrentUserResource(CurrentUserService currentUserService, TokenProvider tokenProvider, CurrentUserMapper currentUserMapper) {
        this.currentUserService = currentUserService;
        this.tokenProvider = tokenProvider;
        this.currentUserMapper = currentUserMapper;
    }

    @GetMapping
    @Timed
    public ResponseEntity<CurrentUserDTO> getCurrentUser() {
        UserPrincipal principal = this.currentUserService.getPrincipal();
        CurrentUserDTO currentUserDTO = this.currentUserMapper.toCurrentUserResource(principal);
        String token = this.tokenProvider.createToken(SecurityContextHolder.getContext().getAuthentication(), false);
        return ResponseEntity.ok()
            .header(AUTHORIZATION_HEADER, TOKEN_PREFIX + token)
            .body(currentUserDTO);
    }

    public static class CurrentUserDTO {

        private String id;

        private String login;

        private String firstName;

        private String lastName;

        private String email;

        private String langKey;

        private Set<String> authorities = new HashSet<>();

        private String companyId;

        private String companyName;

        CurrentUserDTO(String id,
            String login,
            String firstName,
            String lastName,
            String email,
            String langKey,
            Set<String> authorities,
            String externalId,
            String name) {
            this.id = id;
            this.login = login;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.langKey = langKey;
            this.authorities.addAll(authorities);
            this.companyId = externalId;
            this.companyName = name;
        }

        public String getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getLangKey() {
            return langKey;
        }

        public Set<String> getAuthorities() {
            return authorities;
        }

        public String getCompanyId() {
            return companyId;
        }

        public String getCompanyName() {
            return companyName;
        }
    }
}

package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.service.CurrentUserService;
import com.codahale.metrics.annotation.Timed;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@Profile("!no-eiam")
public class AccountResource {

    private final CurrentUserService currentUserService;

    private final UserInfoRepository userInfoRepository;

    public AccountResource(CurrentUserService currentUserService, UserInfoRepository userInfoRepository) {
        this.currentUserService = currentUserService;
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public AccountDTO getAccount() {
        EiamUserPrincipal principal = this.currentUserService.getPrincipal();
        Optional<UserInfo> userInfo = this.userInfoRepository.findOneByUserExternalId(principal.getUserExtId());
        if (!userInfo.isPresent()) {
            throw new IllegalStateException("User not found: " + principal.getUserExtId());
        }
        return new AccountDTO(
            userInfo.get().getId().getValue(),
            principal.getEmail(),
            principal.getFirstName(),
            principal.getLastName(),
            principal.getEmail(),
            principal.getLangKey(),
            principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
            userInfo.get().getCompany()
        );
    }

    private class AccountDTO {

        private String id;

        private String login;

        private String firstName;

        private String lastName;

        private String email;

        private String langKey;

        private Set<String> authorities = new HashSet<>();

        private String organizationId;

        private String organizationName;

        AccountDTO(String id,
                   String login,
                   String firstName,
                   String lastName,
                   String email,
                   String langKey,
                   Set<String> authorities,
                   Company company) {
            this.id = id;
            this.login = login;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.langKey = langKey;
            this.authorities.addAll(authorities);
            if (company != null) {
                this.organizationId = company.getExternalId();
                this.organizationName = company.getName();
            }
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

        public String getOrganizationId() {
            return organizationId;
        }

        public String getOrganizationName() {
            return organizationName;
        }
    }
}

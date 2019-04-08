package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.security.jwt.TokenProvider;
import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.dto.CurrentUserDTO;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.AUTHORIZATION_HEADER;
import static ch.admin.seco.jobroom.security.jwt.JWTConfigurer.TokenResolver.TOKEN_PREFIX;

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

}

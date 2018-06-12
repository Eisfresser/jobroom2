package ch.admin.seco.jobroom.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.service.dto.UserDTO;
import ch.admin.seco.jobroom.web.rest.errors.InternalServerErrorException;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@Profile("!no-eiam")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    /**
     * GET  /account : get the current user.
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public UserDTO getAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null
            && authentication.getPrincipal() instanceof EiamUserPrincipal) {
            return new UserDTO((EiamUserPrincipal) authentication.getPrincipal());
        } else {
            throw new InternalServerErrorException("User could not be found");
        }
    }

}

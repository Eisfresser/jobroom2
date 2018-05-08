package ch.admin.seco.jobroom.security;

import static java.lang.String.format;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.repository.UserRepository;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    public static final String BAD_CREDENTIALS_MSG = "The credentials are invalid!";

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DomainUserPrincipal loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        return userRepository.findOneWithAuthoritiesByLogin(login.toLowerCase(Locale.ENGLISH))
                             .map(DomainUserPrincipal::new)
                             .orElseThrow(() ->
                                 new BadCredentialsException(format(BAD_CREDENTIALS_MSG)));
    }
}

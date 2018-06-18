package ch.admin.seco.jobroom.security;

import ch.admin.seco.jobroom.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@Profile("no-eiam")
public class LoginFormUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(LoginFormUserDetailsService.class);

    private final UserRepository userRepository;

    public LoginFormUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LoginFormUserPrincipal loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        return userRepository.findOneWithAuthoritiesByLogin(login.toLowerCase(Locale.ENGLISH))
            .map(LoginFormUserPrincipal::new)
            .orElseThrow(() ->
                new UsernameNotFoundException("No User found with login: " + login));
    }
}

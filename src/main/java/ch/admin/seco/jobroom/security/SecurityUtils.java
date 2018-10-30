package ch.admin.seco.jobroom.security;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean hasAnyRole(String... roles) {
        final List<String> rolesList = Arrays.asList(roles);
        return getAuthentication()
            .map(authentication -> authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rolesList::contains))
            .orElse(false);
    }

    public static Optional<String> getCurrentUserLogin() {
        return getUserDetails()
            .map(UserDetails::getUsername);
    }

    private static Optional<Authentication> getAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication());
    }

    private static Optional<UserDetails> getUserDetails() {
        return getAuthentication()
            .map(Authentication::getPrincipal)
            .filter(principal -> principal instanceof UserDetails)
            .map(UserDetails.class::cast);
    }

}

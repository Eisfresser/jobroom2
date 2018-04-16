package ch.admin.seco.jobroom.security.saml.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
        // Avoid instantiation
    }

    public static AuthenticatedUser getAuthenticatedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthenticatedUser) {
            return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            throw new IllegalStateException(String.format("Authentication object in the security context must be of type %s",
                AuthenticatedUser.class.getCanonicalName()));
        }
    }

}

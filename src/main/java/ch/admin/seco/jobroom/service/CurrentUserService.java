package ch.admin.seco.jobroom.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ch.admin.seco.jobroom.security.UserPrincipal;

@Component
public class CurrentUserService {

    public UserPrincipal getPrincipal() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            throw new CouldNotLoadCurrentUserException("No Security Context is available");
        }

        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            throw new CouldNotLoadCurrentUserException("No Authentication is available");
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            throw new CouldNotLoadCurrentUserException("No Principal is available");
        }
        if (!(principal instanceof UserPrincipal)) {
            throw new CouldNotLoadCurrentUserException("Principal is not of type UserPrincipal but was: " + principal.getClass());
        }

        return (UserPrincipal) authentication.getPrincipal();
    }

    public void addRoleToSession(String role) {
        UserPrincipal userPrincipal = this.getPrincipal();
        SimpleGrantedAuthority newGrantedAuthority = new SimpleGrantedAuthority(role);
        userPrincipal.addAuthority(newGrantedAuthority);
        // because the authorities collection in authentication is immutable, we have to make a new one
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        authorities.add(newGrantedAuthority);
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(userPrincipal, authentication.getCredentials(), authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

}

package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.saml.DefaultSamlBasedUserDetailsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CurrentUserService {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSamlBasedUserDetailsProvider.class);

    private final Environment environment;

    public CurrentUserService(Environment environment) {
        this.environment = environment;
    }

    public EiamUserPrincipal getPrincipal() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            throw new IllegalStateException("No Security Context is available");
        }

        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No Authentication is available");
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            throw new IllegalStateException("No Principal is available");
        }

        if (!(authentication.getPrincipal() instanceof EiamUserPrincipal)) {
            // Workaround for local development
            if (noEiamProfileActive() && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                return toEiamUserPrincipal(userDetails);
            }
            throw new IllegalStateException("The principal found in the user's session is not of type EiamUserPrincipal.");
        }

        if (!(authentication.getPrincipal() instanceof EiamUserPrincipal)) {
            throw new IllegalStateException("Principal is not of type EiamUserPrincipal but was: " + authentication.getClass());
        }


        return (EiamUserPrincipal) authentication.getPrincipal();
    }

    public void addRoleToSession(String role) {
        EiamUserPrincipal eiamUserPrincipal = this.getPrincipal();
        SimpleGrantedAuthority newGrantedAuthority = new SimpleGrantedAuthority(role);
        eiamUserPrincipal.addAuthority(newGrantedAuthority);
        // because the authorities collection in authentication is immutable, we have to make a new one
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        authorities.add(newGrantedAuthority);
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(eiamUserPrincipal, authentication.getCredentials(), authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    @Deprecated
    private EiamUserPrincipal toEiamUserPrincipal(org.springframework.security.core.userdetails.User userDetails) {
        throw new UnsupportedOperationException("Not ready yet");
    }

    private boolean noEiamProfileActive() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch(profile -> (profile.equals("no-eiam")));
    }


}

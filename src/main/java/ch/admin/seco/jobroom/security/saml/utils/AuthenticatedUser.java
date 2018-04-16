package ch.admin.seco.jobroom.security.saml.utils;

import ch.admin.seco.jobroom.domain.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AuthenticatedUser extends org.springframework.security.core.userdetails.User {

    private final User user;

    private final List<String> roles;

    public AuthenticatedUser(User user, Collection<? extends GrantedAuthority> authorities, String password) {
        super(String.valueOf(user.getExtId()), password, authorities);

        this.user = user;
        this.roles = new ArrayList<>(getRolesAsString(authorities));
    }

    private List<String> getRolesAsString(Collection<? extends GrantedAuthority> grantedAuthorities) {
        List<String> rolesAsString = new ArrayList<>();
        for (GrantedAuthority authority : grantedAuthorities) {
            rolesAsString.add(authority.getAuthority());
        }
        return rolesAsString;
    }

    public List<String> getRoles() {
        return Collections.unmodifiableList(this.roles);
    }

    public User getUser() {
        return this.user;
    }

    public boolean hasRole(String roleName) {
        return this.roles.contains(roleName);
    }
}


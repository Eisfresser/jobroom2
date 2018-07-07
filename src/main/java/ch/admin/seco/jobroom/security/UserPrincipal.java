package ch.admin.seco.jobroom.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ch.admin.seco.jobroom.domain.UserInfoId;

public class UserPrincipal implements UserDetails {

    private final UserInfoId id;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String userExtId;

    private final String langKey;

    private List<GrantedAuthority> authorities = new ArrayList<>();

    private String userDefaultProfileExtId;

    private String password;

    private boolean accountEnabled = true;

    public UserPrincipal(UserInfoId id, String firstName, String lastName, String email, String userExtId, String langKey) {
        this.id = Preconditions.checkNotNull(id);
        this.firstName = Preconditions.checkNotNull(firstName);
        this.lastName = Preconditions.checkNotNull(lastName);
        this.email = Preconditions.checkNotNull(email);
        this.userExtId = Preconditions.checkNotNull(userExtId);
        this.langKey = langKey;
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

    public String getUserExtId() {
        return userExtId;
    }

    public String getLangKey() {
        return langKey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userExtId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.accountEnabled;
    }

    public void setAuthoritiesFromStringCollection(Collection<String> authorities) {
        this.setAuthorities(authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList()));
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(GrantedAuthority authority) {
        this.authorities.add(authority);
    }

    public void setUserDefaultProfileExtId(String defaultProfileExtId) {
        this.userDefaultProfileExtId = defaultProfileExtId;
    }

    void setAccountEnabled(boolean accountEnabled) {
        this.accountEnabled = accountEnabled;
    }

    public String getUserDefaultProfileExtId() {
        return userDefaultProfileExtId;
    }

    public UserInfoId getId() {
        return id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", userExtId='" + userExtId + '\'' +
            '}';
    }
}

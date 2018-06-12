package ch.admin.seco.jobroom.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer;

public class EiamUserPrincipal implements UserDetails {

    private UserInfo user;

    private List<GrantedAuthority> authorities = new ArrayList<>();

    private boolean needsRegistration = false;

    private String authenticationMethod;

    private String userDefaultProfileExtId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        throw new NotImplementedException();
    }

    @Override
    public String getUsername() {
        return user.getUserExternalId();
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
        return true;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isNeedsRegistration() {
        return needsRegistration;
    }

    public void setNeedsRegistration(boolean needsRegistration) {
        this.needsRegistration = needsRegistration;
    }

    public void setAuthoritiesFromStringCollection(Collection<String> authorities) {
        this.authorities = authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(GrantedAuthority authority) {
        this.authorities.add(authority);
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public boolean hasOnlyOneFactorAuthentication() {
        return SAMLConfigurer.ONE_FACTOR_AUTHN_CTX.equals(authenticationMethod);
    }

    public void setUserDefaultProfileExtId(String defaultProfileExtId) {
        this.userDefaultProfileExtId = defaultProfileExtId;
    }

    public String getUserDefaultProfileExtId() {
        return userDefaultProfileExtId;
    }
}

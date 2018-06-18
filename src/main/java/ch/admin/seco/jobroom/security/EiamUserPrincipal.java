package ch.admin.seco.jobroom.security;

import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;
import ch.admin.seco.jobroom.security.saml.infrastructure.dsl.SAMLConfigurer;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EiamUserPrincipal implements UserDetails {

    private final UserInfoId id;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String userExtId;

    private final String langKey;

    private List<GrantedAuthority> authorities = new ArrayList<>();

    private String authenticationMethod;

    private String userDefaultProfileExtId;

    private RegistrationStatus registrationStatus;

    public EiamUserPrincipal(UserInfoId id, String firstName, String lastName, String email, String userExtId, String langKey) {
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
        throw new NotImplementedException();
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
        return true;
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

    public boolean hasOnlyOneFactorAuthentication() {
        return SAMLConfigurer.ONE_FACTOR_AUTHN_CTX.equals(authenticationMethod);
    }

    public void setUserDefaultProfileExtId(String defaultProfileExtId) {
        this.userDefaultProfileExtId = defaultProfileExtId;
    }

    public String getUserDefaultProfileExtId() {
        return userDefaultProfileExtId;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public UserInfoId getId() {
        return id;
    }

}

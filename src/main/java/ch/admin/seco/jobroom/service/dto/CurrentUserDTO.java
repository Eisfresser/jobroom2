package ch.admin.seco.jobroom.service.dto;

import java.util.HashSet;
import java.util.Set;

import ch.admin.seco.jobroom.domain.enumeration.RegistrationStatus;

public class CurrentUserDTO {

    private String id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private String langKey;
    private Set<String> authorities = new HashSet<>();
    private RegistrationStatus registrationStatus;

    public String getId() {
        return id;
    }

    public CurrentUserDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public CurrentUserDTO setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public CurrentUserDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public CurrentUserDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CurrentUserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getLangKey() {
        return langKey;
    }

    public CurrentUserDTO setLangKey(String langKey) {
        this.langKey = langKey;
        return this;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public CurrentUserDTO setAuthorities(Set<String> authorities) {
        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
        return this;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public CurrentUserDTO setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
        return this;
    }
}

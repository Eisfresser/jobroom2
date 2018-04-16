package ch.admin.seco.jobroom.security.saml.infrastructure;


import org.springframework.security.core.userdetails.UserDetails;

public interface SamlBasedUserDetailsProvider {

    UserDetails createUserDetails(SamlUser samlUser);

}

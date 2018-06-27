package ch.admin.seco.jobroom.security.saml.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SamlUser {

    static final String SCHEMAS_XMLSOAP_2005_05_PREFIX = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/";

    private final String nameId;

    private final Map<String, List<String>> attributes;

    private final String authnContext;

    SamlUser(String nameId, Map<String, List<String>> attributes, String authnContext) {
        this.nameId = nameId;
        this.attributes = attributes;
        this.authnContext = authnContext;
    }

    public Optional<List<String>> getAttribute(String key) {
        if (attributes.containsKey(key)) {
            return Optional.ofNullable(attributes.get(key));
        }
        return Optional.empty();
    }

    public String getNameId() {
        return nameId;
    }

    public Optional<String> getGivenname() {
        return getAttributeSingleValue(SCHEMAS_XMLSOAP_2005_05_PREFIX + "givenname");
    }

    public Optional<String> getSurname() {
        return getAttributeSingleValue(SCHEMAS_XMLSOAP_2005_05_PREFIX + "surname");
    }

    public Optional<String> getEmail() {
        return getAttributeSingleValue(SCHEMAS_XMLSOAP_2005_05_PREFIX + "emailaddress");
    }

    public String getAuthnContext() {
        return authnContext;
    }

    @Override
    public String toString() {
        return "SamlUser{" +
            "nameId='" + nameId + '\'' +
            ", givenname='" + toStringhelper(getGivenname()) + '\'' +
            ", surname='" + toStringhelper(getSurname()) + '\'' +
            ", email='" + toStringhelper(getEmail()) + '\'' +
            ", attributes=" + attributes +
            '}';
    }

    Optional<String> getAttributeSingleValue(String key) {
        Optional<List<String>> attribute = this.getAttribute(key);
        if (attribute.isPresent()) {
            return Optional.ofNullable(attribute.get().get(0));
        }
        return Optional.empty();
    }

    final Object toStringhelper(Optional<?> optional) {
        return optional.orElse(null);
    }
}

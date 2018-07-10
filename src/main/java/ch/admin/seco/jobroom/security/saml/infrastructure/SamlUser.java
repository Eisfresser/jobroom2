package ch.admin.seco.jobroom.security.saml.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SamlUser {

    private static final String SCHEMAS_XMLSOAP_2005_05_PREFIX = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/";

    static final String EMAIL_KEY = SCHEMAS_XMLSOAP_2005_05_PREFIX + "emailaddress";

    static final String SURNAME_KEY = SCHEMAS_XMLSOAP_2005_05_PREFIX + "surname";

    static final String GIVEN_NAME_KEY = SCHEMAS_XMLSOAP_2005_05_PREFIX + "givenname";

    private final String nameId;

    private final Map<String, List<String>> attributes;

    SamlUser(String nameId, Map<String, List<String>> attributes) {
        this.nameId = nameId;
        this.attributes = attributes;
    }

    public String getNameId() {
        return nameId;
    }

    public Optional<String> getGivenname() {
        return getAttributeSingleValue(GIVEN_NAME_KEY);
    }

    public Optional<String> getSurname() {
        return getAttributeSingleValue(SURNAME_KEY);
    }

    public Optional<String> getEmail() {
        return getAttributeSingleValue(EMAIL_KEY);
    }

    Optional<List<String>> getAttribute(String key) {
        if (attributes.containsKey(key)) {
            return Optional.ofNullable(attributes.get(key));
        }
        return Optional.empty();
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

}

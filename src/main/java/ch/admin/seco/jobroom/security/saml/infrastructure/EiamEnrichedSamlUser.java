package ch.admin.seco.jobroom.security.saml.infrastructure;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EiamEnrichedSamlUser extends SamlUser {

    private static final String SCHEMAS_EIAM_2013_12_PREFIX = "http://schemas.eiam.admin.ch/ws/2013/12/identity/claims/";

    static final String USER_EXTID_KEY = SCHEMAS_EIAM_2013_12_PREFIX + "e-id/userExtId";

    static final String ROLES_KEY = SCHEMAS_EIAM_2013_12_PREFIX + "role";

    static final String LANGUAGE_KEY = SCHEMAS_EIAM_2013_12_PREFIX + "language";

    EiamEnrichedSamlUser(String nameId, Map<String, List<String>> attributes) {
        super(nameId, attributes);
    }

    @Override
    public String toString() {
        return "EIAMSAMLUser{" +
            "nameId='" + getNameId() + '\'' +
            ", roles=" + getRoles() +
            ", displayName='" + toStringhelper(getDisplayName()) + '\'' +
            ", language='" + toStringhelper(getLanguage()) + '\'' +
            ", loginId='" + toStringhelper(getLoginId()) + '\'' +
            ", clientExtId='" + toStringhelper(getClientExtId()) + '\'' +
            ", userExtId='" + toStringhelper(getUserExtId()) + '\'' +
            ", unitExtId='" + toStringhelper(getUnitExtId()) + '\'' +
            ", unitName='" + toStringhelper(getUnitName()) + '\'' +
            ", distinguishedName='" + toStringhelper(getDistinguishedName()) + '\'' +
            ", isDefaultProfile='" + isDefaultProfile() + '\'' +
            ", profileName='" + toStringhelper(getProfileName()) + '\'' +
            ", defaultProfileExtId='" + toStringhelper(getDefaultProfileExtId()) + '\'' +
            ", sessionProfileExtId='" + toStringhelper(getSessionProfileExtId()) + '\'' +
            ", homeRealm='" + toStringhelper(getHomeRealm()) + '\'' +
            ", homeName='" + toStringhelper(getHomeName()) + '\'' +
            '}' + super.toString();
    }

    public Optional<String> getDisplayName() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "displayName");
    }

    public Optional<String> getLanguage() {
        return getAttributeSingleValue(LANGUAGE_KEY);
    }

    public List<String> getRoles() {
        return getAttribute(ROLES_KEY).orElse(Collections.emptyList());
    }

    public Optional<String> getLoginId() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/loginId");
    }

    public Optional<String> getClientExtId() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/clientExtId");
    }

    public Optional<String> getUserExtId() {
        return getAttributeSingleValue(USER_EXTID_KEY);
    }

    public Optional<String> getProfileName() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/profile/profileName");
    }

    public Optional<String> getDefaultProfileExtId() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/profile/defaultProfileExtId");
    }

    public Optional<String> getSessionProfileExtId() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/profile/sessionProfileExtId");
    }

    public Optional<String> getUnitExtId() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/unitExtId");
    }

    public Optional<String> getDistinguishedName() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/distinguishedName");
    }

    public boolean isDefaultProfile() {
        return !getDefaultProfileExtId().isPresent() || !getSessionProfileExtId().isPresent() || getDefaultProfileExtId().get().equals(getSessionProfileExtId().get());
    }

    public Optional<String> getHomeRealm() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "fp/homeRealm");
    }

    public Optional<String> getHomeName() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "fp/homeName");
    }

    public Optional<String> getUnitName() {
        return getAttributeSingleValue(SCHEMAS_EIAM_2013_12_PREFIX + "e-id/unitName");
    }

}

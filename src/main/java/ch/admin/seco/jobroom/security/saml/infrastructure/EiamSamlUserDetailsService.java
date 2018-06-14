package ch.admin.seco.jobroom.security.saml.infrastructure;

import ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer;
import org.apache.commons.lang.StringUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.AttributeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Merges the information from the SAML assertion with the ones found in the UserInfo table
 * of the Jobroom database.
 */
public class EiamSamlUserDetailsService implements SAMLUserDetailsService {

    private static final QName ORIGINAL_ISSUED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2009/09/identity/claims", "OriginalIssuer");

    private static final Logger LOGGER = LoggerFactory.getLogger(EiamSamlUserDetailsService.class);

    private static final String USER_EXT_ID_ATTRIBUTE_NAME = "http://schemas.eiam.admin.ch/ws/2013/12/identity/claims/e-id/userExtId";

    private static final String EIAM_ISSUER_NAME = "uri:eiam.admin.ch:feds";

    private final SamlBasedUserDetailsProvider samlBasedUserDetailsProvider;

    public EiamSamlUserDetailsService(SamlBasedUserDetailsProvider samlBasedUserDetailsProvider) {
        Assert.notNull(samlBasedUserDetailsProvider, "a user detail provider must be set");
        this.samlBasedUserDetailsProvider = samlBasedUserDetailsProvider;
    }

    @Override
    @Transactional
    public Object loadUserBySAML(SAMLCredential credential) {
        String extId = credential.getNameID().getValue();
        LOGGER.debug("Authenticating user with extId {}", extId);
        SamlUser samlUser = toSamlUser(credential);
        return samlBasedUserDetailsProvider.createUserDetailsFromSaml(samlUser);
    }

    private SamlUser toSamlUser(SAMLCredential credential) {
        final Map<String, List<String>> attributes = extractAttributes(credential.getAttributes());
        if (LOGGER.isDebugEnabled()) {
            printOutAttributes(attributes);
        }
        final String nameId = credential.getNameID().getValue();
        SamlUser samlUser = doCreateSamlUser(attributes, nameId, getAuthnContext(credential).orElse(SAMLConfigurer.ONE_FACTOR_AUTHN_CTX));
        LOGGER.trace("SamlUser User: {}", samlUser);
        return samlUser;
    }

    private SamlUser doCreateSamlUser(Map<String, List<String>> attributes, String nameId, String authnContext) {
        if (isCredentialEnrichedByEIAM(attributes)) {
            LOGGER.debug("Credential was enriched by EIAM: creating an EiamEnrichedSamlUser");
           return new EiamEnrichedSamlUser(nameId, attributes, authnContext);
        } else {
            LOGGER.debug("Credential wasn't enriched by EIAM: creating a SamlUser");
            return new SamlUser(nameId, attributes, authnContext);
        }
    }

    private Optional<String> getAuthnContext(SAMLCredential credential) {
        Optional<String> authnContext = Optional.empty();
        Assertion assertion = credential.getAuthenticationAssertion();
        if (assertion.getAuthnStatements().size() > 0) {
            for (AuthnStatement statement : assertion.getAuthnStatements()) {
                if (statement.getAuthnContext() != null
                    && statement.getAuthnContext().getAuthnContextClassRef() != null
                    && StringUtils.isNotEmpty(statement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef())) {
                    if (authnContext.isPresent()) {
                        LOGGER.warn("More than one authn context found in SAML assertion. The first one found [{}] is used; this one [{}] is ignored.", authnContext, statement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef());
                    } else {
                        authnContext = Optional.of(statement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef());
                    }
                }
            }
        }
        return authnContext;
    }

    private void printOutAttributes(Map<String, List<String>> attributes) {
        attributes.forEach((key, value) -> LOGGER.debug("SAMLCredential attribute Name: {} -> Values: {}", key, value));
    }

    private static boolean isCredentialEnrichedByEIAM(Map<String, List<String>> attributes) {
        return attributes.containsKey(USER_EXT_ID_ATTRIBUTE_NAME);
    }

    private static Map<String, List<String>> extractAttributes(List<Attribute> credentialAttributes) {
        return credentialAttributes
                .stream()
                .filter(attribute -> isAttributeFromEiam(attribute) || isAttributeFromPEP(attribute))
                .collect(Collectors.toMap(Attribute::getName, attribute -> attribute.getAttributeValues()
                        .stream()
                        .filter(xmlObject -> xmlObject instanceof XSString)
                        .map(xmlObject -> (XSString) xmlObject)
                        .map(XSString::getValue)
                        .collect(Collectors.toList())));
    }

    private static boolean isAttributeFromEiam(Attribute attribute) {
        return EIAM_ISSUER_NAME.equals(extractOriginalIssuer(attribute));
    }

    private static boolean isAttributeFromPEP(Attribute attribute) {
        return StringUtils.isBlank(extractOriginalIssuer(attribute));
    }

    private static String extractOriginalIssuer(Attribute attribute) {
        AttributeMap unknownAttributes = attribute.getUnknownAttributes();
        return unknownAttributes.get(ORIGINAL_ISSUED_QNAME);
    }
}

package ch.admin.seco.jobroom.security.saml.infrastructure;

import ch.admin.seco.jobroom.security.saml.infrastructure.dsl.SAMLConfigurer;
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

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
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

    private static final String CH_LOGIN_ISSUER_NAME = "urn:eiam.admin.ch:idp:e-id:CH-LOGIN";

    private final SamlBasedUserDetailsProvider samlBasedUserDetailsProvider;

    public EiamSamlUserDetailsService(SamlBasedUserDetailsProvider samlBasedUserDetailsProvider) {
        this.samlBasedUserDetailsProvider = samlBasedUserDetailsProvider;
    }

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        String extId = credential.getNameID().getValue();
        LOGGER.debug("Authenticating user with extId {}", extId);
        return this.samlBasedUserDetailsProvider.createUserDetailsFromSaml(toSamlUser(credential));
    }

    private SamlUser toSamlUser(SAMLCredential credential) {
        SamlUser samlUser = doCreateSamlUser(credential);
        LOGGER.trace("SamlUser User: {}", samlUser);
        return samlUser;
    }

    private SamlUser doCreateSamlUser(SAMLCredential credential) {
        final String nameId = credential.getNameID().getValue();
        final String authnContext = getAuthnContext(credential).orElse(SAMLConfigurer.ONE_FACTOR_AUTHN_CTX);
        Map<String, List<String>> eiamAttributes = extractAttributes(credential.getAttributes(), EIAM_ISSUER_NAME);
        if (eiamAttributes.containsKey(USER_EXT_ID_ATTRIBUTE_NAME)) {
            LOGGER.info("Credential was enriched by EIAM Issuer FEDS: creating an EiamEnrichedSamlUser");
            printOutAttributes(eiamAttributes);
            return new EiamEnrichedSamlUser(nameId, eiamAttributes, authnContext);
        }

        eiamAttributes = extractAttributes(credential.getAttributes(), CH_LOGIN_ISSUER_NAME);
        if (eiamAttributes.containsKey(USER_EXT_ID_ATTRIBUTE_NAME)) {
            LOGGER.info("Credential was enriched by EIAM Issuer: CH_LOGIN: creating an SamlUser");
            printOutAttributes(eiamAttributes);
            return new SamlUser(nameId, eiamAttributes, authnContext);
        }

        throw new UnknownSamlCredentialAuthenticationException("The received SAMLCredential is nether enriched with FEDS nor CH-LOGIN attributes");
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
        if (LOGGER.isDebugEnabled()) {
            attributes.forEach((key, value) -> LOGGER.debug("SAMLCredential attribute Name: {} -> Values: {}", key, value));
        }
    }

    private static Map<String, List<String>> extractAttributes(List<Attribute> credentialAttributes, String issuerNamespace) {
        return credentialAttributes
            .stream()
            .filter(matchesIssuer(issuerNamespace))
            .collect(Collectors.toMap(Attribute::getName, attribute -> attribute.getAttributeValues()
                .stream()
                .filter(xmlObject -> xmlObject instanceof XSString)
                .map(xmlObject -> (XSString) xmlObject)
                .map(XSString::getValue)
                .collect(Collectors.toList())));
    }

    private static Predicate<Attribute> matchesIssuer(String issuerNamespace) {
        return attribute -> issuerNamespace.equals(extractOriginalIssuer(attribute));
    }

    private static String extractOriginalIssuer(Attribute attribute) {
        AttributeMap unknownAttributes = attribute.getUnknownAttributes();
        return unknownAttributes.get(ORIGINAL_ISSUED_QNAME);
    }
}

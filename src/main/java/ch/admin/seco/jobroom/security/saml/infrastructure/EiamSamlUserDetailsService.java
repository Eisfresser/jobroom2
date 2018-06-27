package ch.admin.seco.jobroom.security.saml.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.AttributeMap;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.SAMLUtil;

import ch.admin.seco.jobroom.security.saml.infrastructure.dsl.SAMLConfigurer;

/**
 * Merges the information from the SAML assertion with the ones found in the UserInfo table
 * of the Jobroom database.
 */
public class EiamSamlUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EiamSamlUserDetailsService.class);

    private static final QName ORIGINAL_ISSUED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2009/09/identity/claims", "OriginalIssuer");

    static final String USER_EXT_ID_ATTRIBUTE_NAME = EiamEnrichedSamlUser.SCHEMAS_EIAM_2013_12_PREFIX + "e-id/userExtId";

    static final String FEDS_ISSUER_NAME = "uri:eiam.admin.ch:feds";

    private final SamlBasedUserDetailsProvider samlBasedUserDetailsProvider;

    public EiamSamlUserDetailsService(SamlBasedUserDetailsProvider samlBasedUserDetailsProvider) {
        this.samlBasedUserDetailsProvider = samlBasedUserDetailsProvider;
    }

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        return this.samlBasedUserDetailsProvider.createUserDetailsFromSaml(doCreateSamlUser(credential));
    }

    private SamlUser doCreateSamlUser(SAMLCredential credential) {
        final String nameId = credential.getNameID().getValue();
        LOGGER.debug("Authenticating user having nameId: {}", nameId);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Received Saml-Assertion: {}", extractSamlAssertion(credential));
        }
        final String authnContext = getAuthnContext(credential).orElse(SAMLConfigurer.ONE_FACTOR_AUTHN_CTX);

        final Map<String, List<String>> fedsAttributes = extractAttributesForFedsIssuer(credential.getAttributes());
        if (fedsAttributes.containsKey(USER_EXT_ID_ATTRIBUTE_NAME)) {
            LOGGER.info("Credential for nameId: '{}' was enriched by EIAM Issuer FEDS: creating an EiamEnrichedSamlUser", nameId);
            printOutAttributes(fedsAttributes);
            return new EiamEnrichedSamlUser(nameId, fedsAttributes, authnContext);
        }

        Map<String, List<String>> allAttributes = extractAllAttributes(credential.getAttributes());
        LOGGER.warn("Credential for nameId: '{}' was NOT enriched by EIAM Issuer: FEDS: creating an default SamlUser", nameId);
        printOutAttributes(allAttributes);
        return new SamlUser(nameId, allAttributes, authnContext);
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

    private static Map<String, List<String>> extractAttributesForFedsIssuer(List<Attribute> credentialAttributes) {
        return credentialAttributes
            .stream()
            .filter(EiamSamlUserDetailsService::isIssuedByFeds)
            .collect(Collectors.toMap(Attribute::getName, EiamSamlUserDetailsService::extractValues));
    }

    private static boolean isIssuedByFeds(Attribute attribute) {
        return EiamSamlUserDetailsService.FEDS_ISSUER_NAME.equals(extractOriginalIssuer(attribute));
    }

    private static Map<String, List<String>> extractAllAttributes(List<Attribute> credentialAttributes) {
        Map<String, List<String>> result = new HashMap<>();
        for (Attribute credentialAttribute : credentialAttributes) {
            result.computeIfAbsent(credentialAttribute.getName(), s -> new ArrayList<>())
                .addAll(extractValues(credentialAttribute));
        }
        return result;
    }

    private static List<String> extractValues(Attribute credentialAttribute) {
        return credentialAttribute.getAttributeValues().stream()
            .filter(xmlObject -> xmlObject instanceof XSString)
            .map(xmlObject -> (XSString) xmlObject)
            .map(XSString::getValue)
            .collect(Collectors.toList());
    }

    private static String extractOriginalIssuer(Attribute attribute) {
        AttributeMap unknownAttributes = attribute.getUnknownAttributes();
        return unknownAttributes.get(ORIGINAL_ISSUED_QNAME);
    }

    private String extractSamlAssertion(SAMLCredential credential) {
        try {
            return XMLHelper.nodeToString(SAMLUtil.marshallMessage(credential.getAuthenticationAssertion()));
        } catch (MessageEncodingException e) {
            LOGGER.error("Could not extract saml-assertion", e);
            return "";
        }
    }

}

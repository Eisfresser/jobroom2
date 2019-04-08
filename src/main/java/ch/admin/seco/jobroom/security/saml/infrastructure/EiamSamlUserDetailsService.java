package ch.admin.seco.jobroom.security.saml.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.AttributeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

public class EiamSamlUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EiamSamlUserDetailsService.class);

    private static final QName ORIGINAL_ISSUED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2009/09/identity/claims", "OriginalIssuer");

    static final String FEDS_ISSUER_NAME = "uri:eiam.admin.ch:feds";

    private final SamlBasedUserDetailsProvider samlBasedUserDetailsProvider;

    private final Set<String> attributesToCopyFromNonFeds = new HashSet<>();

    public EiamSamlUserDetailsService(SamlBasedUserDetailsProvider samlBasedUserDetailsProvider) {
        this.samlBasedUserDetailsProvider = samlBasedUserDetailsProvider;
        this.attributesToCopyFromNonFeds.add(SamlUser.EMAIL_KEY);
        this.attributesToCopyFromNonFeds.add(SamlUser.SURNAME_KEY);
        this.attributesToCopyFromNonFeds.add(SamlUser.GIVEN_NAME_KEY);
        this.attributesToCopyFromNonFeds.add(EiamEnrichedSamlUser.LANGUAGE_KEY);
    }

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        return this.samlBasedUserDetailsProvider.createUserDetailsFromSaml(doCreateSamlUser(credential));
    }

    private SamlUser doCreateSamlUser(SAMLCredential credential) {
        final String nameId = credential.getNameID().getValue();
        LOGGER.debug("Authenticating user having nameId: {}", nameId);

        if (isIssuedWithFedsAttributes(credential.getAttributes())) {
            LOGGER.info("Credential for nameId: '{}' was enriched by EIAM Issuer FEDS: creating an EiamEnrichedSamlUser", nameId);
            final Map<String, List<String>> attributes = extractAttributes(credential);
            printOutAttributes(attributes);
            return new EiamEnrichedSamlUser(nameId, attributes);
        }

        Map<String, List<String>> allAttributes = extractAllAttributes(credential.getAttributes());
        LOGGER.warn("Credential for nameId: '{}' was NOT enriched by EIAM Issuer: FEDS: creating an default SamlUser", nameId);
        printOutAttributes(allAttributes);
        return new SamlUser(nameId, allAttributes);
    }

    private Map<String, List<String>> extractAttributes(SAMLCredential credential) {
        final Map<String, List<String>> resultingAttributes = new HashMap<>(extractAttributesForFedsIssuer(credential.getAttributes()));

        final Map<String, List<String>> nonFedsAttributes = extractNonFedsAttributes(credential.getAttributes());
        if (!nonFedsAttributes.isEmpty()) {
            // copy the following values from the non-feds issued attributes since they can change
            // whereas the once from feds always contain the initial values
            for (String attributesToCopyFromNonFed : this.attributesToCopyFromNonFeds) {
                replaceAttribute(attributesToCopyFromNonFed, nonFedsAttributes, resultingAttributes);
            }
        }
        return resultingAttributes;
    }

    private void replaceAttribute(String attributeKey, Map<String, List<String>> source, Map<String, List<String>> destination) {
        if (source.containsKey(attributeKey)) {
            destination.replace(attributeKey, source.get(attributeKey));
        }
    }

    private void printOutAttributes(Map<String, List<String>> attributes) {
        if (LOGGER.isTraceEnabled()) {
            attributes.forEach((key, value) -> LOGGER.trace("SAMLCredential attribute Name: {} -> Values: {}", key, value));
        }
    }

    private static boolean isIssuedWithFedsAttributes(List<Attribute> credentialAttributes) {
        return credentialAttributes
            .stream()
            .filter(EiamSamlUserDetailsService::isAttributeIssuedByFeds)
            .collect(Collectors.toMap(Attribute::getName, EiamSamlUserDetailsService::extractValues))
            .containsKey(EiamEnrichedSamlUser.USER_EXTID_KEY);
    }

    private static Map<String, List<String>> extractAttributesForFedsIssuer(List<Attribute> credentialAttributes) {
        return credentialAttributes
            .stream()
            .filter(EiamSamlUserDetailsService::isAttributeIssuedByFeds)
            .collect(Collectors.toMap(Attribute::getName, EiamSamlUserDetailsService::extractValues));
    }

    private static Map<String, List<String>> extractNonFedsAttributes(List<Attribute> credentialAttributes) {
        return credentialAttributes
            .stream()
            .filter(EiamSamlUserDetailsService::isAttributeNotIssuedByFeds)
            .collect(Collectors.toMap(Attribute::getName, EiamSamlUserDetailsService::extractValues));
    }

    private static boolean isAttributeNotIssuedByFeds(Attribute attribute) {
        return !isAttributeIssuedByFeds(attribute);
    }

    private static boolean isAttributeIssuedByFeds(Attribute attribute) {
        return extractOriginalIssuer(attribute)
            .filter(FEDS_ISSUER_NAME::equalsIgnoreCase)
            .isPresent();
    }

    private static Map<String, List<String>> extractAllAttributes(List<Attribute> credentialAttributes) {
        Map<String, List<String>> result = new HashMap<>();
        for (Attribute credentialAttribute : credentialAttributes) {
            result.computeIfAbsent(credentialAttribute.getName(), s -> new ArrayList<>())
                .addAll(extractValues(credentialAttribute));
        }
        return result;
    }

    private static List<String> extractValues(Attribute attribute) {
        return attribute.getAttributeValues().stream()
            .filter(xmlObject -> xmlObject instanceof XSString)
            .map(xmlObject -> (XSString) xmlObject)
            .map(XSString::getValue)
            .collect(Collectors.toList());
    }

    private static Optional<String> extractOriginalIssuer(Attribute attribute) {
        AttributeMap unknownAttributes = attribute.getUnknownAttributes();
        return Optional.ofNullable(unknownAttributes.get(ORIGINAL_ISSUED_QNAME));
    }

}

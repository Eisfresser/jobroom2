package ch.admin.seco.jobroom.security.saml.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.AttributeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.EiamUserPrincipal;
import ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer;

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

    private UserInfoRepository userInfoRepository;

    public EiamSamlUserDetailsService(SamlBasedUserDetailsProvider samlBasedUserDetailsProvider, UserInfoRepository userInfoRepository) {
        Assert.notNull(samlBasedUserDetailsProvider, "a user detail provider must be set");
        Assert.notNull(userInfoRepository, "a user info repository is needed");
        this.samlBasedUserDetailsProvider = samlBasedUserDetailsProvider;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    @Transactional
    public Object loadUserBySAML(SAMLCredential credential) {
        String extId = credential.getNameID().getValue();
        LOGGER.debug("Authenticating user with extId {}", extId);

        UserDetails userDetails = samlBasedUserDetailsProvider.createUserDetailsFromSaml(toSamlUser(credential));
        EiamUserPrincipal eiamUser;
        if (userDetails instanceof  EiamUserPrincipal) {
            eiamUser = (EiamUserPrincipal) userDetails;
            Optional<UserInfo> dbUser = userInfoRepository.findOneByUserExternalId(extId);
            if (dbUser.isPresent()) {   // existing user
                EiamUserPrincipal updatedPrincipal = setAdditionalDataFromDbUser(eiamUser, dbUser.get());
                eiamUser.setNeedsRegistration(false);
                // update the db user with data from eIAM
                userInfoRepository.save(updatedPrincipal.getUser());
            } else {    // new user; must first register in Jobroom
                eiamUser.setNeedsRegistration(true);
            }
        } else {
            throw new IllegalArgumentException("The UserDetails in the credential are not of type EiamUserPrincipal, but of type" + userDetails.getClass().getSimpleName());
        }
        return eiamUser;
    }

    private EiamUserPrincipal setAdditionalDataFromDbUser(EiamUserPrincipal eiamUser, UserInfo userInfo) {
        UserInfo userInfoWithDataFromSaml = eiamUser.getUser();
        userInfo.setUserExternalId(userInfoWithDataFromSaml.getUserExternalId());
        userInfo.setLastName(userInfoWithDataFromSaml.getLastName());
        userInfo.setFirstName(userInfoWithDataFromSaml.getFirstName());
        userInfo.setEmail(userInfoWithDataFromSaml.getEmail());
        userInfo.setPhone(userInfoWithDataFromSaml.getPhone());
        userInfo.setLangKey(userInfoWithDataFromSaml.getLangKey());
        eiamUser.setUser(userInfo);
        return eiamUser;
    }

    private SamlUser toSamlUser(SAMLCredential credential) {
        final Map<String, List<String>> attributes = extractAttributes(credential.getAttributes());
        if (LOGGER.isDebugEnabled()) {
            printOutAttributes(attributes);
        }
        final String nameId = credential.getNameID().getValue();
        return doCreateSamlUser(attributes, nameId, getAuthnContext(credential).orElse(SAMLConfigurer.ONE_FACTOR_AUTHN_CTX));
    }

    private SamlUser doCreateSamlUser(Map<String, List<String>> attributes, String nameId, String authnContext) {
        final SamlUser samlUser;
        if (isCredentialEnrichedByEIAM(attributes)) {
            LOGGER.debug("Credential was enriched by EIAM: creating an EiamEnrichedSamlUser");
            samlUser = new EiamEnrichedSamlUser(nameId, attributes, authnContext);
        } else {
            LOGGER.debug("Credential wasn't enriched by EIAM: creating a SamlUser");
            samlUser = new SamlUser(nameId, attributes, authnContext);
        }
        LOGGER.trace("SamlUser User: {}", samlUser);
        return samlUser;
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

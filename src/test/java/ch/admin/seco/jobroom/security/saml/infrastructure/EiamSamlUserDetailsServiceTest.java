package ch.admin.seco.jobroom.security.saml.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.schema.XSString;

import org.springframework.security.saml.SAMLCredential;

public class EiamSamlUserDetailsServiceTest {

    private static final QName ORIGINAL_ISSUED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2009/09/identity/claims", "OriginalIssuer");

    private static final String TESTING_NAME_ID = "TESTING-NAME-ID";

    private static final String CH_LOGIN_ISSUER_NAME = "urn:eiam.admin.ch:idp:e-id:CH-LOGIN";

    private XMLObjectBuilderFactory builderFactory;

    private SamlBasedUserDetailsProvider samlBasedUserDetailsProvider;

    private EiamSamlUserDetailsService eiamSamlUserDetailsService;

    @Before
    public void setUp() throws Exception {
        DefaultBootstrap.bootstrap();
        builderFactory = Configuration.getBuilderFactory();
        samlBasedUserDetailsProvider = mock(SamlBasedUserDetailsProvider.class);
        eiamSamlUserDetailsService = new EiamSamlUserDetailsService(samlBasedUserDetailsProvider);
    }

    @Test
    public void testLoadFedIssuedSamlUser() throws ConfigurationException {
        SAMLCredential samlCredential = mock(SAMLCredential.class);
        when(samlCredential.getNameID()).thenReturn(buildTestingNameId());
        when(samlCredential.getAuthenticationAssertion()).thenReturn(new AssertionBuilder().buildObject());

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(createEiamFedsAttribute(EiamSamlUserDetailsService.USER_EXT_ID_ATTRIBUTE_NAME, "EXT-ID"));
        attributes.add(createEiamFedsAttribute(SamlUser.SCHEMAS_XMLSOAP_2005_05_PREFIX + "givenname", "GIVEN-NAME-1"));
        attributes.add(createEiamFedsAttribute(SamlUser.SCHEMAS_XMLSOAP_2005_05_PREFIX + "emailaddress", "test@example.com"));
        attributes.add(createEiamFedsAttribute(EiamEnrichedSamlUser.SCHEMAS_EIAM_2013_12_PREFIX + "role", "ROLE-A", "Role-B"));

        when(samlCredential.getAttributes()).thenReturn(attributes);

        this.eiamSamlUserDetailsService.loadUserBySAML(samlCredential);

        ArgumentCaptor<SamlUser> captor = ArgumentCaptor.forClass(SamlUser.class);
        verify(this.samlBasedUserDetailsProvider).createUserDetailsFromSaml(captor.capture());

        SamlUser samlUser = captor.getValue();

        assertThat(samlUser).isInstanceOf(EiamEnrichedSamlUser.class);

        EiamEnrichedSamlUser eiamEnrichedSamlUser = (EiamEnrichedSamlUser) samlUser;
        assertThat(samlUser.getNameId()).isEqualTo(TESTING_NAME_ID);
        assertThat(samlUser.getGivenname()).contains("GIVEN-NAME-1");
        assertThat(samlUser.getEmail()).contains("test@example.com");

        assertThat(eiamEnrichedSamlUser.getRoles()).hasSize(2);
    }

    @Test
    public void testLoadNonFedIssuedSamlUser() throws ConfigurationException {
        SAMLCredential samlCredential = mock(SAMLCredential.class);
        when(samlCredential.getNameID()).thenReturn(buildTestingNameId());
        when(samlCredential.getAuthenticationAssertion()).thenReturn(new AssertionBuilder().buildObject());

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(createEiamFedsAttribute(SamlUser.SCHEMAS_XMLSOAP_2005_05_PREFIX + "givenname", "GIVEN-NAME-1"));
        attributes.add(createEiamChLoginAttribute(SamlUser.SCHEMAS_XMLSOAP_2005_05_PREFIX + "givenname", "GIVEN-NAME-2"));
        attributes.add(createEiamChLoginAttribute(SamlUser.SCHEMAS_XMLSOAP_2005_05_PREFIX + "emailaddress", "test@example.com"));
        when(samlCredential.getAttributes()).thenReturn(attributes);

        this.eiamSamlUserDetailsService.loadUserBySAML(samlCredential);

        ArgumentCaptor<SamlUser> captor = ArgumentCaptor.forClass(SamlUser.class);
        verify(this.samlBasedUserDetailsProvider).createUserDetailsFromSaml(captor.capture());

        SamlUser samlUser = captor.getValue();

        assertThat(samlUser).isNotInstanceOf(EiamEnrichedSamlUser.class);
        assertThat(samlUser.getNameId()).isEqualTo(TESTING_NAME_ID);
        assertThat(samlUser.getGivenname()).contains("GIVEN-NAME-1");
        assertThat(samlUser.getEmail()).contains("test@example.com");
    }

    private Attribute createEiamFedsAttribute(String name, String... values) throws ConfigurationException {
        return addValueToAttribute(prepareEiamFedsIssuerAttribute(), name, values);
    }

    private Attribute createEiamChLoginAttribute(String name, String... values) throws ConfigurationException {
        return addValueToAttribute(prepareEiamCHLoginIssuerAttribute(), name, values);
    }

    private Attribute addValueToAttribute(Attribute attribute, String name, String[] values) throws ConfigurationException {
        attribute.setName(name);
        for (String s : values) {
            attribute.getAttributeValues().add(buildString(s));
        }
        return attribute;
    }

    private Attribute prepareEiamFedsIssuerAttribute() {
        final AttributeBuilder attributeBuilder = new AttributeBuilder();
        final Attribute attribute = attributeBuilder.buildObject(new QName("http://schemas.xmlsoap.org/ws/2009/09/identity/claims", "Unknown"));
        attribute.getUnknownAttributes().put(ORIGINAL_ISSUED_QNAME, EiamSamlUserDetailsService.FEDS_ISSUER_NAME);
        return attribute;
    }

    private Attribute prepareEiamCHLoginIssuerAttribute() {
        final AttributeBuilder attributeBuilder = new AttributeBuilder();
        final Attribute attribute = attributeBuilder.buildObject(new QName("http://schemas.xmlsoap.org/ws/2009/09/identity/claims", "Unknown"));
        attribute.getUnknownAttributes().put(ORIGINAL_ISSUED_QNAME, CH_LOGIN_ISSUER_NAME);
        return attribute;
    }

    private XSString buildString(String value) throws ConfigurationException {
        XMLObjectBuilder stringBuilder = builderFactory.getBuilder(XSString.TYPE_NAME);
        XSString attrValueFirstName = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attrValueFirstName.setValue(value);
        return attrValueFirstName;
    }

    private NameID buildTestingNameId() {
        final NameIDBuilder nameIDBuilder = new NameIDBuilder();
        final NameID nameID = nameIDBuilder.buildObject();
        nameID.setValue(TESTING_NAME_ID);
        return nameID;
    }

}

package ch.admin.seco.jobroom.security.registration.uid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.ws.test.client.RequestMatchers.connectionTo;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

@ContextConfiguration("UidWebServiceClientTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class UidDefaultClientTest {

    private static final String VALID_UID_PREFIX = "CHE";
    private static final long VALID_UID = 115635627;
    private static final long UNKNOWN_UID = 915635627;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private UidClient client;

    private MockWebServiceServer mockServer;

    @Before
    public void createServer() {
        this.webServiceTemplate.setMarshaller(jaxb2Marshaller());
        this.webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        this.mockServer = MockWebServiceServer.createServer(this.webServiceTemplate);
    }

    @Test
    public void getCompanyByUid() throws CompanyNotFoundException, UidNotUniqueException, UidClientException {
        // Arrange
        StringSource result = new StringSource(UidTestPayloads.GET_BY_UID_VALID_PAYLOAD_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//ns2:uidOrganisationIdCategorie", getNameSpaceMapping()).evaluatesTo(VALID_UID_PREFIX)).
            andExpect(xpath("//ns2:uidOrganisationId", getNameSpaceMapping()).evaluatesTo(VALID_UID)).
            andRespond(withPayload(result));

        // Act
        FirmData company = this.client.getCompanyByUid(VALID_UID);

        // Assert
        assertNotNull(company);
        assertEquals(VALID_UID, company.getUid());
        assertEquals("mimacom ag", company.getName());
        assertNotNull(company.getAddress());
        assertEquals("Galgenfeldweg", company.getAddress().getStreet());
        assertEquals("16", company.getAddress().getBuildingNum());
        assertEquals("3006", company.getAddress().getZip());
        assertEquals("Bern", company.getAddress().getCity());

        this.mockServer.verify();
    }

    @Test (expected = CompanyNotFoundException.class)
    public void getCompanyByUidNoCompanyFound() throws CompanyNotFoundException, UidNotUniqueException, UidClientException {
        // Arrange
        StringSource result = new StringSource(UidTestPayloads.GET_BY_UID_EMPTY_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//ns2:uidOrganisationIdCategorie", getNameSpaceMapping()).evaluatesTo(VALID_UID_PREFIX)).
            andExpect(xpath("//ns2:uidOrganisationId", getNameSpaceMapping()).evaluatesTo(UNKNOWN_UID)).
            andRespond(withPayload(result));

        // Act
        this.client.getCompanyByUid(UNKNOWN_UID);

        // Assert
        this.mockServer.verify();
    }

    @Test (expected = UidNotUniqueException.class)
    public void getCompanyByUidMultipleCompaniesFound() throws CompanyNotFoundException, UidNotUniqueException, UidClientException {
        // Arrange
        StringSource result = new StringSource(UidTestPayloads.GET_BY_UID_MULTIPLE_COMPANIES_PAYLOAD_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//ns2:uidOrganisationIdCategorie", getNameSpaceMapping()).evaluatesTo(VALID_UID_PREFIX)).
            andExpect(xpath("//ns2:uidOrganisationId", getNameSpaceMapping()).evaluatesTo(UNKNOWN_UID)).
            andRespond(withPayload(result));

        // Act
        this.client.getCompanyByUid(UNKNOWN_UID);

        // Assert
        this.mockServer.verify();
    }

    @Test (expected = UidClientException.class)
    public void getCompanyByUidInvalidResponse() throws CompanyNotFoundException, UidNotUniqueException, UidClientException {
        // Arrange
        StringSource result = new StringSource(UidTestPayloads.GET_BY_UID_INVALID_PAYLOAD_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//ns2:uidOrganisationIdCategorie", getNameSpaceMapping()).evaluatesTo(VALID_UID_PREFIX)).
            andExpect(xpath("//ns2:uidOrganisationId", getNameSpaceMapping()).evaluatesTo(VALID_UID)).
                andRespond(withPayload(result));

        // Act
        this.client.getCompanyByUid(VALID_UID);

        // Assert
        this.mockServer.verify();
    }

    private Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        String[] packagesToScan = {
            "ch.admin.uid.xmlns.uid_wse",
            "ch.ech.xmlns.ech_0097_f._2",
            "ch.ech.xmlns.ech_0108_f._3",
            "org.datacontract.schemas._2004._07.ch_admin_bit_uid"
        };
        jaxb2Marshaller.setPackagesToScan(packagesToScan);
        return jaxb2Marshaller;
    }

    private Map<String, String> getNameSpaceMapping() {
        Map<String, String> nameSpaceMapping = new HashMap<>();
        nameSpaceMapping.put("ns2", "http://www.ech.ch/xmlns/eCH-0097-f/2");
        return nameSpaceMapping;
    }

}

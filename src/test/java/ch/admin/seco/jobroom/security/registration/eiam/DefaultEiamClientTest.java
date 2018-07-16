package ch.admin.seco.jobroom.security.registration.eiam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.ws.test.client.RequestMatchers.connectionTo;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.util.ArrayList;
import java.util.List;

import ch.adnovum.nevisidm.ws.services.v1.Authorization;
import ch.adnovum.nevisidm.ws.services.v1.User;
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

@ContextConfiguration ("DefaultEidWebServiceClientTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultEiamClientTest {

    private static final String DEFAULT_DETAIL_LEVEL = "1";
    private static final String CLIENT_NAME = "ALV";
    private static final String VALID_EXT_ID = "CH2102565";
    private static final String INVALID_EXT_ID = "CH4444444";
    private static final String VALID_PROFILE_EXT_ID = "176988";
    private static final String VALID_ROLE = "ROLE_PRIVATE_EMPLOYMENT_AGENT";
    private static final String INVALID_ROLE = "ROLE_NOT_EXISTING";
    private static final String VALID_APPLICATION_NAME = "ALV-jobroom";

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private DefaultEiamClient client;

    private MockWebServiceServer mockServer;

    @Before
    public void createServer() {
        this.webServiceTemplate.setMarshaller(jaxb2Marshaller());
        this.webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        this.mockServer = MockWebServiceServer.createServer(this.webServiceTemplate);
    }

    @Test
    public void getUserByExtId() throws UserNotFoundException, MultipleEiamUsersFound {
        // Arrange
        StringSource result = new StringSource(EiamTestPayloads.GET_USER_BY_EXT_ID_VALID_PAYLOAD_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//detail").evaluatesTo(DEFAULT_DETAIL_LEVEL)).
            andExpect(xpath("//clientName").evaluatesTo(CLIENT_NAME)).
            andExpect(xpath("//extIds").evaluatesTo(VALID_EXT_ID)).
            andRespond(withPayload(result));

        // Act
        User eiamUserData = this.client.getUserByExtId(VALID_EXT_ID);

        // Assert
        assertEquals(VALID_EXT_ID, eiamUserData.getExtId());
        assertEquals("CHL9100100004", eiamUserData.getLoginId());
        assertEquals("Hans", eiamUserData.getFirstName());
        assertEquals("Muster", eiamUserData.getName());
        assertEquals("hans.muster@mail.ch", eiamUserData.getEmail());
        assertEquals("DE", eiamUserData.getLanguage());
        List<String> rolesFound = new ArrayList<>(2);
        List<Authorization> authorizations = eiamUserData.getProfiles().get(0).getAuthorizations();
        rolesFound.add(authorizations.get(0).getRole().getName());
        rolesFound.add(authorizations.get(1).getRole().getName());
        assertTrue(rolesFound.contains("ROLE_REGISTRATION"));
        assertTrue(rolesFound.contains("ALLOW"));
        this.mockServer.verify();
    }

    @Test (expected = UserNotFoundException.class)
    public void getUserByExtIdNoUserFound() throws UserNotFoundException, MultipleEiamUsersFound {
        // Arrange
        StringSource result = new StringSource(EiamTestPayloads.GET_USER_BY_EXT_ID_EMPTY_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//detail").evaluatesTo(DEFAULT_DETAIL_LEVEL)).
            andExpect(xpath("//clientName").evaluatesTo(CLIENT_NAME)).
            andExpect(xpath("//extIds").evaluatesTo(INVALID_EXT_ID)).
            andRespond(withPayload(result));

        // Act
        this.client.getUserByExtId(INVALID_EXT_ID);

        // Assert
        this.mockServer.verify();
    }

    @Test (expected = MultipleEiamUsersFound.class)
    public void getUserByExtIdMultipleUsersFound() throws UserNotFoundException, MultipleEiamUsersFound {
        // Arrange
        StringSource result = new StringSource(EiamTestPayloads.GET_USER_BY_EXT_ID_MULTIPLE_USERS_PAYLOAD_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//detail").evaluatesTo(DEFAULT_DETAIL_LEVEL)).
            andExpect(xpath("//clientName").evaluatesTo(CLIENT_NAME)).
            andExpect(xpath("//extIds").evaluatesTo(INVALID_EXT_ID)).
            andRespond(withPayload(result));

        // Act
        this.client.getUserByExtId(INVALID_EXT_ID);

        // Assert
        this.mockServer.verify();
    }

    @Test
    public void addRoleToUser() throws RoleCouldNotBeAddedException {
        // Arrange
        StringSource result = new StringSource(EiamTestPayloads.ADD_ROLE_SUCCESS_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//detail").evaluatesTo(DEFAULT_DETAIL_LEVEL)).
            andExpect(xpath("//clientName").evaluatesTo(CLIENT_NAME)).
            andExpect(xpath("//extId").evaluatesTo(VALID_PROFILE_EXT_ID)).
            andExpect(xpath("//userExtId").evaluatesTo(VALID_EXT_ID)).
            andExpect(xpath("//name").evaluatesTo(VALID_ROLE)).
            andExpect(xpath("//applicationName").evaluatesTo(VALID_APPLICATION_NAME)).
            andRespond(withPayload(result));

        // Act
        this.client.addRoleToUser(VALID_EXT_ID, VALID_PROFILE_EXT_ID, VALID_ROLE);

        // Assert
        this.mockServer.verify();
    }

    @Test (expected = RoleCouldNotBeAddedException.class)
    public void addUnknownRoleToUser() throws RoleCouldNotBeAddedException {
        // Arrange
        StringSource result = new StringSource(EiamTestPayloads.ADD_ROLE_UNKNOWN_ROLE_RESPONSE);
        this.mockServer.expect(connectionTo("http://my.web.ch/service")).
            andExpect(xpath("//detail").evaluatesTo(DEFAULT_DETAIL_LEVEL)).
            andExpect(xpath("//clientName").evaluatesTo(CLIENT_NAME)).
            andExpect(xpath("//extId").evaluatesTo(VALID_PROFILE_EXT_ID)).
            andExpect(xpath("//userExtId").evaluatesTo(VALID_EXT_ID)).
            andExpect(xpath("//name").evaluatesTo(INVALID_ROLE)).
            andExpect(xpath("//applicationName").evaluatesTo(VALID_APPLICATION_NAME)).
            andRespond(withPayload(result));

        // Act
        this.client.addRoleToUser(VALID_EXT_ID, VALID_PROFILE_EXT_ID, INVALID_ROLE);

        // Assert
        this.mockServer.verify();
    }

    private Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        String[] packagesToScan = {
            "ch.adnovum.nevisidm.ws.services.v1_32",
            "ch.adnovum.nevisidm.ws.services.v1"
        };
        jaxb2Marshaller.setPackagesToScan(packagesToScan);
        return jaxb2Marshaller;
    }


}

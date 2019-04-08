package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;

public class ImprovedSamlAuthenticationProviderTest {


    @Test
    public void testExtractAuthnFailedStatusCode() {

        ImprovedSamlAuthenticationProvider improvedSamlAuthenticationProvider = new ImprovedSamlAuthenticationProvider();
        Response response = new ResponseBuilder()
            .buildObject();

        Status status = new StatusBuilder().buildObject();
        StatusCode responderStatusCode = new StatusCodeBuilder().buildObject();
        responderStatusCode.setValue(StatusCode.RESPONDER_URI);

        StatusCode failedStatusCode = new StatusCodeBuilder().buildObject();
        failedStatusCode.setValue(StatusCode.AUTHN_FAILED_URI);
        responderStatusCode.setStatusCode(failedStatusCode);

        status.setStatusCode(responderStatusCode);

        response.setStatus(status);
        List<String> statusCodes = improvedSamlAuthenticationProvider.extractStatusCodes(response);

        assertThat(statusCodes)
            .contains(StatusCode.AUTHN_FAILED_URI, StatusCode.RESPONDER_URI);

    }
}

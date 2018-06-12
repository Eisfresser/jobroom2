package ch.admin.seco.jobroom.security.registration.eiam;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.client.support.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.soap.client.core.SoapFaultMessageResolver;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Configuration
@EnableConfigurationProperties({EiamWsClientProperties.class})
@Profile("!eiam-mock")
public class EiamWsClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(EiamWsClientConfig.class);

    private final EiamWsClientProperties eiamWsClientProperties;

    @Autowired
    public EiamWsClientConfig(EiamWsClientProperties eiamWsClientProperties) {
        this.eiamWsClientProperties = eiamWsClientProperties;
        LOGGER.info("Received the eiamWsClientProperties: {}", eiamWsClientProperties);
    }

    @Bean
    public EiamClient eiamClient() throws Exception {
        return new DefaultEiamClient(eiamClientWebsericeTemplate(), eiamWsClientProperties.getClientName());
    }

    @Bean
    public EiamAdminServiceHealthIndicator eiamAdminServiceHealthIndicator() throws Exception {
        return new EiamAdminServiceHealthIndicator(eiamClient(), eiamWsClientProperties.getMonitoringUserExternalId());
    }

    @Bean
    public WebServiceTemplate eiamClientWebsericeTemplate() throws Exception {
        LOGGER.info("About to create the eiamClientWebsericeTemplate");
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        webServiceTemplate.setDefaultUri(eiamWsClientProperties.getEndpointAddress());
        webServiceTemplate.setFaultMessageResolver(new SoapFaultMessageResolver());
        webServiceTemplate.setMessageSender(new HttpComponentsMessageSender(httpClient()));
        webServiceTemplate.setInterceptors(new ClientInterceptor[] {payloadValidatingInterceptor()});
        webServiceTemplate.setMessageFactory(messageFactory());
        webServiceTemplate.afterPropertiesSet();
        LOGGER.info("created the eiamClientWebsericeTemplate");
        return webServiceTemplate;
    }

    private PayloadValidatingInterceptor payloadValidatingInterceptor() throws Exception {
        PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        payloadValidatingInterceptor.setSchema(new ClassPathResource("/eiam/wsdl/nevisidm_servicetypes_v1_32.xsd"));
        payloadValidatingInterceptor.setValidateRequest(eiamWsClientProperties.isValidationEnabled());
        payloadValidatingInterceptor.setValidateResponse(eiamWsClientProperties.isValidationEnabled());
        payloadValidatingInterceptor.afterPropertiesSet();
        return payloadValidatingInterceptor;
    }

    private HttpClient httpClient() throws Exception {

        WebServiceHttpClientBuilder httpClientBuilder = new WebServiceHttpClientBuilder();
        httpClientBuilder
            .setTimeouts(eiamWsClientProperties.getConnectTimeout(), eiamWsClientProperties.getSockedTimeout(), eiamWsClientProperties.getConnectionRequestTimeout())
            .setPoolSize(eiamWsClientProperties.getMaxConnTotal())
            .setSSLContextBuilder(prepareSSLContextBuilder());
        if (eiamWsClientProperties.isAllowAllHostnameVerifier()) {
            httpClientBuilder.allowAllHostnameVerifier();
        }
        return httpClientBuilder.build();
    }

    private WebServiceHttpClientBuilder.WebServiceSSLContextBuilder prepareSSLContextBuilder() {
        if (!eiamWsClientProperties.hasSSLProperties()) {
            LOGGER.info("No Keystore or Truststore properties have been defined for the eiam-webservice");
            return null;
        }
        WebServiceHttpClientBuilder.WebServiceSSLContextBuilder sslContextBuilder = new WebServiceHttpClientBuilder.WebServiceSSLContextBuilder();
        prepareKeystore(sslContextBuilder, eiamWsClientProperties.getKeystore());
        prepareTruststore(sslContextBuilder, eiamWsClientProperties.getTruststore());
        return sslContextBuilder;
    }

    private void prepareTruststore(WebServiceHttpClientBuilder.WebServiceSSLContextBuilder sslContextBuilder, EiamWsClientProperties.TruststoreProperties truststoreProperties) {
        if (truststoreProperties == null) {
            LOGGER.info("No Truststore has been defined for the eiam-webservice");
            return;
        }
        sslContextBuilder.setTruststore(truststoreProperties.getLocation(), truststoreProperties.getPassword());
    }

    private void prepareKeystore(WebServiceHttpClientBuilder.WebServiceSSLContextBuilder sslContextBuilder, EiamWsClientProperties.KeystoreProperties keystoreProperties) {
        if (keystoreProperties == null) {
            LOGGER.info("No Keystore has been defined for the eiam-webservice");
            return;
        }
        sslContextBuilder.setKeystore(keystoreProperties.getLocation(), keystoreProperties.getPassword(), keystoreProperties.getPrivateKeyName());
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

    private SaajSoapMessageFactory messageFactory() {
        ImprovedSaajSoapMessageFactory improvedSaajSoapMessageFactory = new ImprovedSaajSoapMessageFactory();
        improvedSaajSoapMessageFactory.afterPropertiesSet();
        return improvedSaajSoapMessageFactory;
    }

    /**
     * Diese Klasse stellt sicher, dass im Header der SoapMessage nur Accept: text/xml steht. Auf Grund eines Fehlers im Nevis-Webservice ist es
     * so, dass ein Fault-Response des Webservice, wenn im Header "text/html" enthalten ist, dieser Umgeleitet wird auf eine Web-URL. Somit kann
     * dies verhindert werden.
     */
    private static class ImprovedSaajSoapMessageFactory extends SaajSoapMessageFactory {

        @Override
        protected void postProcess(SOAPMessage soapMessage) throws SOAPException {
            super.postProcess(soapMessage);
            soapMessage.getMimeHeaders().setHeader("Accept", "text/xml");
        }
    }
}

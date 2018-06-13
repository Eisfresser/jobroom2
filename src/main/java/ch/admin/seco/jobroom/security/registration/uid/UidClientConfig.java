package ch.admin.seco.jobroom.security.registration.uid;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;

@Configuration
@EnableConfigurationProperties({UidClientProperties.class})
@Profile("!uid-mock")
public class UidClientConfig {

    private final UidClientProperties uidClientProperties;

    @Autowired
    public UidClientConfig(UidClientProperties uidClientProperties) {
        this.uidClientProperties = uidClientProperties;
    }

    @Bean
    Jaxb2Marshaller jaxb2Marshaller() {
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

    @Bean
    public WebServiceTemplate webServiceTemplate() throws Exception {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        webServiceTemplate.setDefaultUri(uidClientProperties.getEndpointAddress());
        webServiceTemplate.setMessageSender(httpComponentsMessageSender());
        return webServiceTemplate;
    }

    @Bean
    public HttpComponentsMessageSender httpComponentsMessageSender() throws Exception {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setHttpClient(httpClient());

        return httpComponentsMessageSender;
    }

    @Bean
    public UidClient uidClient() throws Exception {
        return new DefaultUidClient(webServiceTemplate());
    }

    @Bean
    public UidHealthIndicator uidHealthIndicator() throws Exception {
        return new UidHealthIndicator(this.uidClient(), this.uidClientProperties.getMonitoringUid());
    }

    private HttpClient httpClient() throws Exception {
        return HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory())
            .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor()).build();
    }

    private SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
        // NoopHostnameVerifier essentially turns hostname verification off as otherwise following error
        // is thrown: java.security.cert.CertificateException: No name matching localhost found
        return new SSLConnectionSocketFactory(sslContext(), NoopHostnameVerifier.INSTANCE);
    }

    private SSLContext sslContext() throws Exception {
        return SSLContextBuilder.create()
            .loadTrustMaterial(uidClientProperties.getTruststore().getLocation().getFile(),
                uidClientProperties.getTruststore().getPassword().toCharArray()).build();
    }
}

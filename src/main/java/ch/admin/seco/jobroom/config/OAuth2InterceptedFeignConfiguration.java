package ch.admin.seco.jobroom.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

public class OAuth2InterceptedFeignConfiguration {

    @Bean(name = "oauth2RequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Security.ClientAuthorization clientAuthorization = jHipsterProperties.getSecurity().getClientAuthorization();
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setUsername(clientAuthorization.getClientId());
        resourceDetails.setPassword(clientAuthorization.getClientSecret());
        resourceDetails.setAccessTokenUri(clientAuthorization.getAccessTokenUri());
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resourceDetails);
    }

    @Bean
    public Decoder feignDecoder() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
            .failOnUnknownProperties(false)
            .findModulesViaServiceLoader(true)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }


    @Bean
    public RequestInterceptor markerRequestInterceptor() {
        return template -> template.header("X-Requested-With", "XMLHttpRequest");
    }
}

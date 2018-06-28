package ch.admin.seco.jobroom.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RibbonClientConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;

    public RibbonClientConfiguration(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @LoadBalanced
    @Bean
    public RestTemplate ribbonClientRestTemplate() {
        return restTemplateBuilder.build();
    }

}

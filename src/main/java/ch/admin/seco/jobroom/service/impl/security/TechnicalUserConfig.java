package ch.admin.seco.jobroom.service.impl.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TechnicalUserConfig {

    @Bean
    TechnicalUserContextAspect technicalUserContextAspect() {
        return new TechnicalUserContextAspect();
    }
}

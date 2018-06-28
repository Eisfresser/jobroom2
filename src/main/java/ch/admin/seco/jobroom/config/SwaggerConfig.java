package ch.admin.seco.jobroom.config;

import static springfox.documentation.builders.PathSelectors.regex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.jhipster.config.JHipsterConstants;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableSwagger2
@Profile(JHipsterConstants.SPRING_PROFILE_SWAGGER)
class SwaggerConfig {

    @Value("${management.endpoints.web.base-path}")
    String managementContextPath;

    @Bean("swaggerSpringfoxApiDocket")
    public Docket publicApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("default")
            .select()
            .paths(regex("/api/.*"))
            .build()
            .directModelSubstitute(LocalDate.class, java.sql.Date.class)
            .directModelSubstitute(LocalDateTime.class, java.util.Date.class);
    }

    @Bean
    public Docket managementApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("manage-api")
            .select()
            .paths(regex(managementContextPath + ".*"))
            .build()
            .ignoredParameterTypes(Map.class)
            .securityContexts(Collections.singletonList(securityContext()))
            .securitySchemes(Collections.singletonList(apiKey()));
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
            = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
            new SecurityReference("JWT", authorizationScopes));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

}

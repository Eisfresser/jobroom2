package ch.admin.seco.jobroom.config;

import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.admin.seco.jobroom.gateway.accesscontrol.AccessControlFilter;
import ch.admin.seco.jobroom.gateway.ratelimiting.RateLimitingFilter;
import ch.admin.seco.jobroom.gateway.ratelimiting.RateLimitingFilterProperties;
import ch.admin.seco.jobroom.gateway.responserewriting.SwaggerBasePathRewritingFilter;

@Configuration
public class GatewayConfiguration {

    @Configuration
    public static class SwaggerBasePathRewritingConfiguration {

        @Bean
        public SwaggerBasePathRewritingFilter swaggerBasePathRewritingFilter() {
            return new SwaggerBasePathRewritingFilter();
        }
    }

    @Configuration
    public static class AccessControlFilterConfiguration {

        @Bean
        public AccessControlFilter accessControlFilter(RouteLocator routeLocator, JHipsterProperties jHipsterProperties) {
            return new AccessControlFilter(routeLocator, jHipsterProperties);
        }
    }

    /**
     * Configures the Zuul filter that limits the number of API calls per user.
     * <p>
     * This uses Bucket4J to limit the API calls, see {@link ch.admin.seco.jobroom.gateway.ratelimiting.RateLimitingFilter}.
     */
    @Configuration
    @ConditionalOnProperty(value = "gateway.rate-limiting.enabled", matchIfMissing = true)
    @EnableConfigurationProperties(RateLimitingFilterProperties.class)
    public static class RateLimitingConfiguration {

        private final RateLimitingFilterProperties rateLimitingFilterProperties;

        public RateLimitingConfiguration(RateLimitingFilterProperties rateLimitingFilterProperties) {
            this.rateLimitingFilterProperties = rateLimitingFilterProperties;
        }

        @Bean
        public RateLimitingFilter rateLimitingFilter() {
            return new RateLimitingFilter(rateLimitingFilterProperties);
        }
    }
}

package ch.admin.seco.jobroom.config.apidoc;

import java.util.ArrayList;
import java.util.List;

import io.github.jhipster.config.JHipsterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Retrieves all registered microservices Swagger resources.
 */
@Component
@Primary
@Profile(JHipsterConstants.SPRING_PROFILE_SWAGGER)
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewaySwaggerResourcesProvider.class);

    private final RouteLocator routeLocator;

    private final RestTemplate restTemplate;

    public GatewaySwaggerResourcesProvider(RouteLocator routeLocator, RestTemplateBuilder restTemplateBuilder, @Value("${server.port}") Integer localServerPort) {
        this.routeLocator = routeLocator;
        this.restTemplate = restTemplateBuilder
            .rootUri("http://localhost:" + localServerPort)
            .build();
    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        //Add the default swagger resource that correspond to the gateway's own swagger doc
        resources.add(swaggerResource("gateway-default", "/v2/api-docs"));

        //Add the registered microservices swagger docs as additional swagger resources
        List<Route> routes = routeLocator.getRoutes();
        routes.forEach(route -> {
                final String remoteSwaggerResourcesUrl = "/" + route.getLocation() + "/swagger-resources";
                RemoteSwaggerResource[] remoteSwaggerResources = resolveRemoteSwaggerResources(remoteSwaggerResourcesUrl);
                for (RemoteSwaggerResource remoteSwaggerResource : remoteSwaggerResources) {
                    resources.add(swaggerRemoteResource(route, remoteSwaggerResource));
                }
            }
        );
        return resources;
    }

    private SwaggerResource swaggerRemoteResource(Route route, RemoteSwaggerResource remoteSwaggerResource) {
        final String name = route.getId() + "-" + remoteSwaggerResource.getName();
        final String location = "/" + route.getLocation() + remoteSwaggerResource.getLocation();
        return swaggerResource(name, location);
    }

    private RemoteSwaggerResource[] resolveRemoteSwaggerResources(String remoteSwaggerResourcesUrl) {
        try {
            ResponseEntity<RemoteSwaggerResource[]> entity = restTemplate.getForEntity(remoteSwaggerResourcesUrl, RemoteSwaggerResource[].class);
            return entity.getBody();
        } catch (HttpStatusCodeException e) {
            LOGGER.warn("Could not resolve url: {}. Status code was: {}", remoteSwaggerResourcesUrl, e.getStatusCode());
        }
        return new RemoteSwaggerResource[0];
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

    static class RemoteSwaggerResource {
        String name;
        String url;
        String swaggerVersion;
        String location;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSwaggerVersion() {
            return swaggerVersion;
        }

        public void setSwaggerVersion(String swaggerVersion) {
            this.swaggerVersion = swaggerVersion;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}

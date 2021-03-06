package ch.admin.seco.jobroom.config.apidoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.jhipster.config.JHipsterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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

    private final RestTemplate ribbonClientRestTemplate;

    public GatewaySwaggerResourcesProvider(RouteLocator routeLocator, RestTemplate ribbonClientRestTemplate) {
        this.routeLocator = routeLocator;
        this.ribbonClientRestTemplate = ribbonClientRestTemplate;
    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        //Add the default swagger resource that correspond to the gateway's own swagger doc
        resources.add(swaggerResource("gateway-default", "/v2/api-docs"));
        resources.add(swaggerResource("gateway-manage-api", "/v2/api-docs?group=manage-api"));

        //Add the registered microservices swagger docs as additional swagger resources
        List<Route> routes = routeLocator.getRoutes();
        routes.stream()
            .peek(route -> LOGGER.debug("handling following route " + route))
            .filter(Objects::nonNull)
            .forEach(route -> {
                    RemoteSwaggerResource[] remoteSwaggerResources = resolveRemoteSwaggerResources(route.getId());
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

    private RemoteSwaggerResource[] resolveRemoteSwaggerResources(String serviceId) {
        try {
            ResponseEntity<RemoteSwaggerResource[]> entity = ribbonClientRestTemplate
                .getForEntity("http://" + serviceId + "/swagger-resources", RemoteSwaggerResource[].class);
            if (entity.getBody() == null) {
                LOGGER.warn("Received empty swagger-resources from service-id: " + serviceId);
                return new RemoteSwaggerResource[0];
            }
            return entity.getBody();
        } catch (Exception e) {
            LOGGER.warn("Could not resolve swagger-resources from service-id: " + serviceId, e);
            return new RemoteSwaggerResource[0];
        }
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

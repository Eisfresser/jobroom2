package ch.admin.seco.jobroom.gateway.ratelimiting;

import java.util.Optional;
import java.util.function.Supplier;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import javax.servlet.http.HttpServletRequest;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.jcache.JCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;

import ch.admin.seco.jobroom.security.SecurityUtils;

/**
 * Zuul filter for limiting the number of HTTP calls per client.
 *
 * See the Bucket4j documentation at https://github.com/vladimir-bukhtoyarov/bucket4j
 * https://github.com/vladimir-bukhtoyarov/bucket4j/blob/master/doc-pages/jcache-usage
 * .md#example-1---limiting-access-to-http-server-by-ip-address
 */
public class RateLimitingFilter extends ZuulFilter {

    private final static String GATEWAY_RATE_LIMITING_CACHE_NAME = "gateway-rate-limiting";

    private final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private javax.cache.Cache<String, GridBucketState> cache;

    private ProxyManager<String> buckets;

    private RateLimitingFilterProperties rateLimitingFilterProperties;

    public RateLimitingFilter(RateLimitingFilterProperties rateLimitingFilterProperties) {
        this.rateLimitingFilterProperties = rateLimitingFilterProperties;
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, GridBucketState> config =
            new MutableConfiguration<String, GridBucketState>()
                .setTypes(String.class, GridBucketState.class);
        this.cache = cacheManager.createCache(GATEWAY_RATE_LIMITING_CACHE_NAME, config);
        this.buckets = Bucket4j.extension(JCache.class).proxyManagerForCache(cache);
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        if (currentContext == null) {
            return false;
        }
        HttpServletRequest request = currentContext.getRequest();
        if (request == null) {
            return false;
        }
        return this.rateLimitingFilterProperties.getRateFilterOptions()
            .stream()
            .anyMatch(rateFilterOption -> rateFilterOption.matches(request));
    }

    @Override
    public Object run() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        Optional<RateLimitingFilterProperties.RateFilterOption> filterOption = findFilterOption(request);
        if (!filterOption.isPresent()) {
            return null;
        }
        String bucketId = filterOption.get().buildBucketId(determineBucketPostFix(request));
        Bucket bucket = buckets.getProxy(bucketId, bucketConfigSupplier(filterOption.get()));
        if (bucket.tryConsume(1)) {
            log.debug("API rate limit OK for: {}", bucketId);
        } else {
            log.warn("API rate limit exceeded for: {}", bucketId);
            apiLimitExceeded();
        }
        return null;
    }

    private String determineBucketPostFix(HttpServletRequest request) {
        return SecurityUtils.getCurrentUserLogin().orElse(request.getRemoteAddr());
    }

    private Supplier<BucketConfiguration> bucketConfigSupplier(RateLimitingFilterProperties.RateFilterOption rateFilterOption) {
        return () -> Bucket4j
            .configurationBuilder()
            .addLimit(Bandwidth.simple(rateFilterOption.getLimit(), rateFilterOption.getDuration()))
            .buildConfiguration();
    }

    private void apiLimitExceeded() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody("API rate limit exceeded");
            ctx.setSendZuulResponse(false);
        }
    }

    private Optional<RateLimitingFilterProperties.RateFilterOption> findFilterOption(HttpServletRequest request) {
        return this.rateLimitingFilterProperties.getRateFilterOptions().stream()
            .filter(rateFilterOption -> rateFilterOption.matches(request))
            .findFirst();
    }

}

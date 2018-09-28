package ch.admin.seco.jobroom.gateway.ratelimiting;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

@ConfigurationProperties("gateway.rate-limiting")
public class RateLimitingFilterProperties {

    private boolean enabled = true;

    @Valid
    private List<RateFilterOption> rateFilterOptions = Collections.emptyList();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<RateFilterOption> getRateFilterOptions() {
        return rateFilterOptions;
    }

    public void setRateFilterOptions(List<RateFilterOption> rateFilterOptions) {
        this.rateFilterOptions = rateFilterOptions;
    }

    static class RateFilterOption {

        @NotEmpty
        private String url = "/";

        @NotEmpty
        private String bucketPrefix;

        @Min(1)
        private int limit;

        @NotNull
        private Duration duration;

        private HttpMethod method;

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public String getBucketPrefix() {
            return bucketPrefix;
        }

        public void setBucketPrefix(String bucketPrefix) {
            this.bucketPrefix = bucketPrefix;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        String buildBucketId(String bucketPostFix) {
            return this.bucketPrefix + "-" + bucketPostFix;
        }

        public boolean matches(HttpServletRequest request) {
            if (this.getMethod() != null) {
                HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
                if (!this.getMethod().equals(httpMethod)) {
                    return false;
                }
            }
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            String requestURI = request.getRequestURI();
            return antPathMatcher.match(this.url, requestURI);
        }
    }

}



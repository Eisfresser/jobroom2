package ch.admin.seco.jobroom.gateway.ratelimiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import org.springframework.http.HttpMethod;

public class RateLimitingFilterPropertiesTest {


    @Test
    public void testRateFilterOption() {
        RateLimitingFilterProperties.RateFilterOption rateFilterOption = new RateLimitingFilterProperties.RateFilterOption();
        rateFilterOption.setMethod(HttpMethod.GET);
        rateFilterOption.setUrl("/candidateservice/api/candidates/*");

        assertThat(
            rateFilterOption.matches(createHttpServletRequest("/candidateservice/api/candidates/1234", "GET")))
            .isTrue();

        assertThat(
            rateFilterOption.matches(createHttpServletRequest("/candidateservice/api/candidates/1234", "POST")))
            .isFalse();

        assertThat(
            rateFilterOption.matches(createHttpServletRequest("/candidateservice/api/candidates/1234/profile", "GET")))
            .isFalse();
    }

    private HttpServletRequest createHttpServletRequest(String value, String method) {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getMethod()).thenReturn(method);
        when(httpServletRequest.getRequestURI()).thenReturn(value);
        return httpServletRequest;
    }

}

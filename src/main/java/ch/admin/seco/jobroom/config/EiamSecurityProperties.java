package ch.admin.seco.jobroom.config;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "security.eiam")
@Validated
public class EiamSecurityProperties {

    @NotNull
    @NotEmpty
    private Map<String, String> rolemapping;

    /**
     * Detect if the user hit the cancellation button in eiam and redirect then to the start-page
     *
     * Default: true
     */
    private boolean enableRedirectOnCancellation = true;

    public Map<String, String> getRolemapping() {
        return rolemapping;
    }

    public void setRolemapping(Map<String, String> rolemapping) {
        this.rolemapping = rolemapping;
    }

    public boolean isEnableRedirectOnCancellation() {
        return enableRedirectOnCancellation;
    }

    public void setEnableRedirectOnCancellation(boolean enableRedirectOnCancellation) {
        this.enableRedirectOnCancellation = enableRedirectOnCancellation;
    }
}

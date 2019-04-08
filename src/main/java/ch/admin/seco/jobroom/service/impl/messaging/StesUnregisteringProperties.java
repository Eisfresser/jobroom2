package ch.admin.seco.jobroom.service.impl.messaging;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jobroom.stes-unregistering")
@Validated
class StesUnregisteringProperties {

    /**
     * Whether to enable the automatic unregistering of stes or whether to send an mail
     * with instructions to do so
     *
     * default: false
     */
    private boolean autoUnregisteringEnabled = false;

    /**
     * Defines who receives the mail with instructions on how to unregister a stes
     */
    @NotBlank
    private String manualUnregisteringMailReceiver = "service-desk-mail-address@mail.ch";

    public void setAutoUnregisteringEnabled(boolean enabled) {
        this.autoUnregisteringEnabled = enabled;
    }

    public String getManualUnregisteringMailReceiver() {
        return manualUnregisteringMailReceiver;
    }

    public void setManualUnregisteringMailReceiver(String manualUnregisteringMailReceiver) {
        this.manualUnregisteringMailReceiver = manualUnregisteringMailReceiver;
    }

    public boolean isAutoUnregisteringEnabled() {
        return autoUnregisteringEnabled;
    }

    public void setCandidateUnregistrationnEnabled(boolean enabled) {
        this.autoUnregisteringEnabled = enabled;
    }
}

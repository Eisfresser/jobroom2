package ch.admin.seco.jobroom.service.impl.messaging;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jobroom.stes-unregistering")
@Valid
class StesUnregistrationProperties {

    private boolean autoUnregisteringEnabled = false;

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

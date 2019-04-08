package ch.admin.seco.jobroom.service.logging;

import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.springframework.util.Assert.hasText;

public class BusinessLogEvent {

    private String authorities;

    private String eventType;

    private String objectType;

    private String objectId;

    private Map<String, Object> additionalData = new HashMap<>();

    public static BusinessLogEvent of(BusinessLogEventType eventType) {
        return new BusinessLogEvent(eventType.name());
    }

    public BusinessLogEvent(String eventType) {
        hasText(eventType, "Event type must not be empty!");
        this.eventType = eventType;
    }

    public String getAuthorities() {
        return authorities;
    }

    public BusinessLogEvent withAuthorities(String authorities) {
        this.authorities = authorities;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public String getObjectType() {
        return objectType;
    }

    public BusinessLogEvent withObjectType(String objectType) {
        this.objectType = objectType;
        return this;
    }

    public BusinessLogEvent withObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public BusinessLogEvent withAdditionalData(String key, Object value) {
        if (isNotEmpty(key) && value != null) {
            this.additionalData.put(key, value);
        }
        return this;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }
}

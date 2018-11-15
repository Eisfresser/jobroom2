package ch.admin.seco.jobroom.service.logging;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.Assert.hasText;

public class BusinessLogData {

    private String eventType;

    private String objectType;

    private String objectId;

    private Map<String, Object> additionalData = new HashMap<>();

    public static BusinessLogData of(BusinessLogEventType eventType) {
        return new BusinessLogData(eventType.name());
    }

    public BusinessLogData(String eventType) {
        hasText(eventType, "Event type must not be empty!");
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getObjectType() {
        return objectType;
    }

    public BusinessLogData withObjectType(String objectType) {
        this.objectType = objectType;
        return this;
    }

    public BusinessLogData withObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public BusinessLogData withAdditionalData(String key, Object value) {
        this.additionalData.put(key, value);
        return this;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }
}

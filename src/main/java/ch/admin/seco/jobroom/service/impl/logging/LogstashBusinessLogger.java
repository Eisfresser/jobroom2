package ch.admin.seco.jobroom.service.impl.logging;

import ch.admin.seco.jobroom.service.logging.BusinessLogEvent;
import ch.admin.seco.jobroom.service.logging.BusinessLogger;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.entries;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class LogstashBusinessLogger implements BusinessLogger {

    private static final String AUTHORITIES_KEY = "authorities";
    private static final String EVENT_TYPE_KEY = "eventType";
    private static final String OBJECT_TYPE_KEY = "objectType";
    private static final String OBJECT_ID_KEY = "objectId";

    private final Logger log;

    private final Marker businessLogMarker;

    public LogstashBusinessLogger() {
        this.businessLogMarker = MarkerFactory.getMarker("BUSINESS_LOG");
        this.log = getLogger(LogstashBusinessLogger.class);
    }

    @Override
    public void log(BusinessLogEvent event) {
        final Map<String, Object> businessLogEntry = new HashMap<>();

        businessLogEntry.put(AUTHORITIES_KEY, event.getAuthorities());
        businessLogEntry.put(EVENT_TYPE_KEY, event.getEventType());
        businessLogEntry.put(OBJECT_TYPE_KEY, event.getObjectType());
        businessLogEntry.put(OBJECT_ID_KEY, event.getObjectId());
        businessLogEntry.putAll(event.getAdditionalData());


        log.info(this.businessLogMarker, "BusinessLogEntry: {}", entries(businessLogEntry));
    }
}

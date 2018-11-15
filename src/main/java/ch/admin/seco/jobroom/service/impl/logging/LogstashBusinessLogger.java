package ch.admin.seco.jobroom.service.impl.logging;

import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.logging.BusinessLogData;
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

    public static final String AUTHORITIES_KEY = "authorities";
    public static final String EVENT_TYPE_KEY = "eventType";
    public static final String OBJECT_TYPE_KEY = "objectType";
    public static final String OBJECT_ID_KEY = "objectId";

    private final Logger log;

    private final Marker businessLogMarker;

    private final CurrentUserService currentUserService;

    public LogstashBusinessLogger(CurrentUserService currentUserService) {
        this.businessLogMarker = MarkerFactory.getMarker("BUSINESS_LOG");
        this.log = getLogger(LogstashBusinessLogger.class);
        this.currentUserService = currentUserService;
    }

    @Override
    public void log(BusinessLogData logData) {
        Map<String, Object> entriesMap = new HashMap<String, Object>() {{
            put(AUTHORITIES_KEY, currentUserService.getPrincipal().getAuthoritiesAsString());
            put(EVENT_TYPE_KEY, logData.getEventType());
            put(OBJECT_TYPE_KEY, logData.getObjectType());
            put(OBJECT_ID_KEY, logData.getObjectId());
            putAll(logData.getAdditionalData());
        }};

        log.info(this.businessLogMarker, "BusinessLogEntry: {}", entries(entriesMap));
    }
}

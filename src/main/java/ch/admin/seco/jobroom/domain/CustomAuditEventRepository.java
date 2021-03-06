package ch.admin.seco.jobroom.domain;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.config.audit.AuditEventConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of Spring Boot's AuditEventRepository.
 */
@Repository
@Transactional
public class CustomAuditEventRepository implements AuditEventRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuditEventRepository.class);

    static final int EVENT_DATA_COLUMN_MAX_LENGTH = 255;

    private static final PageRequest DEFAULT_PAGE_REQUEST = PageRequest.of(0, 100, Sort.by("auditEventDate").descending());

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;

    private final AuditEventConverter auditEventConverter;

    public CustomAuditEventRepository(PersistenceAuditEventRepository persistenceAuditEventRepository, AuditEventConverter auditEventConverter) {
        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
        this.auditEventConverter = auditEventConverter;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void add(AuditEvent event) {
        if (Constants.ANONYMOUS_USER.equalsIgnoreCase(event.getPrincipal())) {
            return;
        }
        PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
        persistentAuditEvent.setPrincipal(event.getPrincipal());
        persistentAuditEvent.setAuditEventType(event.getType());
        persistentAuditEvent.setAuditEventDate(event.getTimestamp());
        Map<String, String> eventData = auditEventConverter.convertDataToStrings(event.getData());
        persistentAuditEvent.setData(truncate(eventData));
        persistenceAuditEventRepository.save(persistentAuditEvent);
    }

    @Override
    public List<AuditEvent> find(String principal, Instant after, String type) {
        if (principal == null) {
            Page<PersistentAuditEvent> pageResult = persistenceAuditEventRepository.findAll(DEFAULT_PAGE_REQUEST);
            return auditEventConverter.convertToAuditEvent(pageResult.getContent());
        }
        List<PersistentAuditEvent> persistentAuditEvents =
            persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfterAndAuditEventType(principal, after, type);
        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

    /*
     * Truncate event data that might exceed column length.
     */
    private Map<String, String> truncate(Map<String, String> data) {
        Map<String, String> results = new HashMap<>();

        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    int length = value.length();
                    if (length > EVENT_DATA_COLUMN_MAX_LENGTH) {
                        value = value.substring(0, EVENT_DATA_COLUMN_MAX_LENGTH);
                        LOGGER.warn("Event data for {} too long ({}) has been truncated to {}. Consider increasing column width.",
                            entry.getKey(), length, EVENT_DATA_COLUMN_MAX_LENGTH);
                    }
                }
                results.put(entry.getKey(), value);
            }
        }
        return results;
    }
}

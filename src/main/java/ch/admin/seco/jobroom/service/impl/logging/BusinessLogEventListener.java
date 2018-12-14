package ch.admin.seco.jobroom.service.impl.logging;

import static ch.admin.seco.jobroom.service.logging.BusinessLogEventType.valueOf;
import static ch.admin.seco.jobroom.service.logging.BusinessLogObjectType.CANDIDATE;
import static ch.admin.seco.jobroom.service.logging.BusinessLogObjectType.USER;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.logging.BusinessLogEvent;

@Component
public class BusinessLogEventListener {

    private final LogstashBusinessLogger businessLogger;

    private final CurrentUserService currentUserService;

    public BusinessLogEventListener(LogstashBusinessLogger businessLogger, CurrentUserService currentUserService) {
        this.businessLogger = businessLogger;
        this.currentUserService = currentUserService;
    }

    @EventListener(BusinessLogEvent.class)
    public void handleBusinessLogEvent(BusinessLogEvent event) {
        if (event.getAuthorities() == null) {
            event.withAuthorities(currentUserService.getPrincipal().getAuthoritiesAsString());
        }
        switch (valueOf(event.getEventType())) {
            case CANDIDATE_CONTACT_MESSAGE:
                event.withObjectType(CANDIDATE.typeName())
                    .withAdditionalData("userLoginId", getCurrentUserId());
                break;
            case USER_REGISTERED:
                event.withObjectType(USER.typeName())
                    .withObjectId(getCurrentUserId());
                break;
            case USER_UNREGISTERED:
                event.withObjectType(USER.typeName());
                break;
            case USER_LOGIN:
                event.withObjectType(USER.typeName());
                break;
            case USER_LOGOUT:
                event.withObjectType(USER.typeName());
                break;
        }
        businessLogger.log(event);
    }

    private String getCurrentUserId() {
        return currentUserService.getPrincipal().getId().getValue();
    }
}




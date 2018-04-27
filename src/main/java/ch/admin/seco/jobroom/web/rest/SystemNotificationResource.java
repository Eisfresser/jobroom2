package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.service.SystemNotificationService;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class SystemNotificationResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final SystemNotificationService systemNotificationService;

    public SystemNotificationResource(SystemNotificationService systemNotificationService) {
        this.systemNotificationService = systemNotificationService;
    }

    @GetMapping("/systemNotification")
    @Timed
    public List<SystemNotificationDTO> getAllSystemNotifications() {
        log.debug("REST request to get all SystemNotifications : {}");
        return systemNotificationService.getAllSystemNotifications();
    }

    @GetMapping("/systemNotification/{id}")
    @Timed
    public Optional<SystemNotificationDTO> getSystemNotificationById(@PathVariable UUID id) {
        log.debug("REST request to get SystemNotification by Id : {}", id);
        return systemNotificationService.getSystemNotificationById(id);
    }

}

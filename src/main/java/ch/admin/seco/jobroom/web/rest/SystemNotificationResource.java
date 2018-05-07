package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.repository.SystemNotificationRepository;
import ch.admin.seco.jobroom.service.SystemNotificationService;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;
import com.codahale.metrics.annotation.Timed;
import com.hazelcast.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class SystemNotificationResource {

    private final Logger log = LoggerFactory.getLogger(SystemNotificationResource.class);

    private final SystemNotificationService systemNotificationService;
    private final SystemNotificationRepository systemNotificationRepository;

    public SystemNotificationResource(SystemNotificationService systemNotificationService, SystemNotificationRepository systemNotificationRepository) {
        this.systemNotificationService = systemNotificationService;
        this.systemNotificationRepository = systemNotificationRepository;
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
        Preconditions.checkNotNull(id);
        log.debug("REST request to get SystemNotification by Id : {}", id);
        return systemNotificationService.getSystemNotificationById(id);
    }

    @PostMapping("/systemNotification")
    @Timed
    public SystemNotificationDTO createSystemNotification(@Valid @RequestBody SystemNotificationDTO systemNotificationDTO) {
        Preconditions.checkNotNull(systemNotificationDTO);
        log.debug("REST request to save SystemNotification : {}", systemNotificationDTO);
        SystemNotificationDTO newSystemNotification = systemNotificationService.createSystemNotification(systemNotificationDTO);
        return newSystemNotification;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/systemNotification/{id}")
    public void deleteSystemNotification(@PathVariable UUID id) {
        Preconditions.checkNotNull(id);
        log.debug("REST request to delete SystemNotification with id : {}", id);
        systemNotificationService.deleteSystemNotification(id);
    }

    @PatchMapping("/systemNotification")
    @Timed
    public void updateSystemNotification(@Valid @RequestBody SystemNotificationDTO systemNotificationDTO) {
        Preconditions.checkNotNull(systemNotificationDTO);
        log.debug("REST request to update SystemNotification : {}", systemNotificationDTO);
        systemNotificationService.updateSystemNotification(systemNotificationDTO);
    }
}

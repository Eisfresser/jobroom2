package ch.admin.seco.jobroom.web.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;
import com.hazelcast.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.service.SystemNotificationService;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;


@RestController
@RequestMapping("/api")
public class SystemNotificationResource {

    private final Logger log = LoggerFactory.getLogger(SystemNotificationResource.class);

    private final SystemNotificationService systemNotificationService;

    public SystemNotificationResource(SystemNotificationService systemNotificationService) {
        this.systemNotificationService = systemNotificationService;
    }

    @GetMapping("/system-notifications")
    @Timed
    public List<SystemNotificationDTO> getAllSystemNotifications() {
        log.debug("REST request to get all SystemNotifications : {}");
        return systemNotificationService.getAllSystemNotifications();
    }

    @GetMapping("/active-system-notifications")
    @Timed
    public List<SystemNotificationDTO> getActiveSystemNotifications() {
        log.debug("REST request to get active SystemNotifications : {}");
        return systemNotificationService.getActiveSystemNotifications();
    }

    @GetMapping("/system-notifications/{id}")
    @Timed
    public Optional<SystemNotificationDTO> getSystemNotificationById(@PathVariable UUID id) {
        Preconditions.checkNotNull(id);
        log.debug("REST request to get SystemNotification by Id : {}", id);
        return systemNotificationService.getSystemNotificationById(id);
    }

    @PostMapping("/system-notifications")
    @Timed
    public SystemNotificationDTO createSystemNotification(@Valid @RequestBody SystemNotificationDTO systemNotificationDTO) {
        Preconditions.checkNotNull(systemNotificationDTO);
        log.debug("REST request to save SystemNotification : {}", systemNotificationDTO);
        SystemNotificationDTO newSystemNotification = systemNotificationService.createSystemNotification(systemNotificationDTO);
        return newSystemNotification;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/system-notifications/{id}")
    public void deleteSystemNotification(@PathVariable UUID id) {
        Preconditions.checkNotNull(id);
        log.debug("REST request to delete SystemNotification with id : {}", id);
        systemNotificationService.deleteSystemNotification(id);
    }

    @PatchMapping("/system-notifications")
    @Timed
    public void updateSystemNotification(@Valid @RequestBody SystemNotificationDTO systemNotificationDTO) {
        Preconditions.checkNotNull(systemNotificationDTO);
        log.debug("REST request to update SystemNotification : {}", systemNotificationDTO);
        systemNotificationService.updateSystemNotification(systemNotificationDTO);
    }
}

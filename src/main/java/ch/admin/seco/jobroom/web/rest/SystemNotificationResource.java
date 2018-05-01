package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.domain.SystemNotification;
import ch.admin.seco.jobroom.repository.SystemNotificationRepository;
import ch.admin.seco.jobroom.service.SystemNotificationService;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;
import ch.admin.seco.jobroom.web.rest.errors.SystemNotificationIdAlreadyUsedException;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
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
        log.debug("REST request to get SystemNotification by Id : {}", id);
        return systemNotificationService.getSystemNotificationById(id);
    }

    @PostMapping("/systemNotification")
    @Timed
    public UUID createSystemNotification(@Valid @RequestBody SystemNotificationDTO systemNotificationDTO) throws URISyntaxException {
        log.debug("REST request to save SystemNotification : {}", systemNotificationDTO);
        SystemNotification newSystemNotification = systemNotificationService.createSystemNotification(systemNotificationDTO);
        return newSystemNotification.getId();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/systemNotification/{id}")
    public void deleteSystemNotification(@PathVariable UUID id) {
        systemNotificationService.deleteSystemNotification(id);
    }

    @PatchMapping("/systemNotification")
    @Timed
    public void updateSystemNotification(@Valid @RequestBody SystemNotificationDTO systemNotificationDTO) {
        log.debug("REST request to update SystemNotification : {}", systemNotificationDTO);
        Optional<SystemNotification> existingSystemNotification = systemNotificationRepository.getSystemNotificationById(systemNotificationDTO.getId());
        if (existingSystemNotification.isPresent() && (!existingSystemNotification.get().getId().equals(systemNotificationDTO.getId()))) {
            throw new SystemNotificationIdAlreadyUsedException();
        }
        systemNotificationService.updateSystemNotification(systemNotificationDTO);
    }
}

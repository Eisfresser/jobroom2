package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.domain.SystemNotification;
import ch.admin.seco.jobroom.domain.SystemNotificationRepository;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class SystemNotificationService {

    private final Logger log = LoggerFactory.getLogger(SystemNotificationService.class);

    private SystemNotificationRepository systemNotificationRepository;

    public SystemNotificationService(SystemNotificationRepository systemNotificationRepository) {
        this.systemNotificationRepository = systemNotificationRepository;
    }

    @Transactional(readOnly = true)
    public List<SystemNotificationDTO> getAllSystemNotifications() {
        return systemNotificationRepository.getAllSystemNotifications().map(SystemNotificationDTO::toDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<SystemNotificationDTO> getActiveSystemNotifications() {
        return systemNotificationRepository.getActiveSystemNotifications().map(SystemNotificationDTO::toDto).filter(
            systemNotification -> systemNotification.getStartDate().isBefore(LocalDateTime.now()) && systemNotification.getEndDate().isAfter(LocalDateTime.now())
        ).collect(toList());
    }

    @Transactional(readOnly = true)
    public Optional<SystemNotificationDTO> getSystemNotificationById(UUID id) {
        return systemNotificationRepository.getSystemNotificationById(id).map(SystemNotificationDTO::toDto);
    }

    public SystemNotificationDTO createSystemNotification(SystemNotificationDTO systemNotificationDTO) {
        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setTitle(systemNotificationDTO.getTitle());
        systemNotification.setText_de(systemNotificationDTO.getText_de());
        systemNotification.setText_fr(systemNotificationDTO.getText_fr());
        systemNotification.setText_it(systemNotificationDTO.getText_it());
        systemNotification.setText_en(systemNotificationDTO.getText_en());
        systemNotification.setType(systemNotificationDTO.getType());
        systemNotification.setActive(systemNotificationDTO.isActive());
        systemNotification.setStartDate(systemNotificationDTO.getStartDate());
        systemNotification.setEndDate(systemNotificationDTO.getEndDate());
        final UUID id = systemNotificationRepository.save(systemNotification).getId();
        log.debug("Created Information for systemNotification: {}", systemNotification);
        systemNotificationDTO.setId(id);
        return systemNotificationDTO;
    }

    public void updateSystemNotification(SystemNotificationDTO systemNotificationDTO) {
        systemNotificationRepository.getSystemNotificationById(systemNotificationDTO.getId()).ifPresent(systemNotification -> {
            systemNotification.setTitle(systemNotificationDTO.getTitle());
            systemNotification.setText_de(systemNotificationDTO.getText_de());
            systemNotification.setText_fr(systemNotificationDTO.getText_fr());
            systemNotification.setText_it(systemNotificationDTO.getText_it());
            systemNotification.setText_en(systemNotificationDTO.getText_en());
            systemNotification.setType(systemNotificationDTO.getType());
            systemNotification.setStartDate(systemNotificationDTO.getStartDate());
            systemNotification.setEndDate(systemNotificationDTO.getEndDate());
            systemNotification.setActive(systemNotificationDTO.isActive());
            systemNotificationRepository.save(systemNotification);
            log.debug("Updated SystemNotification: {}", systemNotification);
        });
    }

    public void deleteSystemNotification(UUID id) {
        systemNotificationRepository.getSystemNotificationById(id).ifPresent(systemNotification -> {
            systemNotificationRepository.delete(systemNotification);
            log.debug("Deleted SystemNotification: {}", systemNotification);
        });
    }

}

package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.domain.SystemNotification;
import ch.admin.seco.jobroom.repository.SystemNotificationRepository;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;


@Service
@Transactional
public class SystemNotificationService {

    private SystemNotificationRepository systemNotificationRepository;
    private final Logger log = LoggerFactory.getLogger(SystemNotificationService.class);

    public SystemNotificationService(SystemNotificationRepository systemNotificationRepository) {
        this.systemNotificationRepository = systemNotificationRepository;
    }

    @Transactional(readOnly = true)
    public List<SystemNotificationDTO> getAllSystemNotifications() {
        return systemNotificationRepository.getAllSystemNotifications().map(SystemNotificationDTO::toDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public Optional<SystemNotificationDTO> getSystemNotificationById(UUID id) {
        return systemNotificationRepository.getSystemNotificationById(id).map(SystemNotificationDTO::toDto);
    }

    @Transactional
    public SystemNotificationDTO createSystemNotification(SystemNotificationDTO systemNotificationDTO) {
        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setTitle(systemNotificationDTO.getTitle());
        systemNotification.setText(systemNotificationDTO.getText());
        systemNotification.setType(systemNotificationDTO.getType());
        systemNotification.setActive(systemNotificationDTO.isActive());
        systemNotification.setStartDate(systemNotificationDTO.getStartDate());
        systemNotification.setEndDate(systemNotificationDTO.getEndDate());
        systemNotificationRepository.save(systemNotification);
        log.debug("Created Information for systemNotification: {}", systemNotification);
        return systemNotificationDTO;
    }

    @Transactional
    public void updateSystemNotification(SystemNotificationDTO systemNotificationDTO) {
        systemNotificationRepository.getSystemNotificationById(systemNotificationDTO.getId()).ifPresent(systemNotification -> {
            systemNotification.setTitle(systemNotificationDTO.getTitle());
            systemNotification.setText(systemNotificationDTO.getText());
            systemNotification.setType(systemNotificationDTO.getType());
            systemNotification.setStartDate(systemNotificationDTO.getStartDate());
            systemNotification.setEndDate(systemNotificationDTO.getEndDate());
            systemNotification.setActive(systemNotificationDTO.isActive());
            systemNotificationRepository.save(systemNotification);
            log.debug("Updated SystemNotification: {}", systemNotification);
        });
    }

    @Transactional
    public void deleteSystemNotification(UUID id) {
        systemNotificationRepository.getSystemNotificationById(id).ifPresent(systemNotification -> {
            systemNotificationRepository.delete(systemNotification);
            log.debug("Deleted SystemNotification: {}", systemNotification);
        });
    }

}

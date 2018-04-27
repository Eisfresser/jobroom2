package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.repository.SystemNotificationRepository;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;
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
}

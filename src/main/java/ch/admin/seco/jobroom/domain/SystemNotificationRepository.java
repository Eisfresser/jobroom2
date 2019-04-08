package ch.admin.seco.jobroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface SystemNotificationRepository extends JpaRepository<SystemNotification, UUID> {

    @Query("select s from SystemNotification s")
    Stream<SystemNotification> getAllSystemNotifications();

    @Query("select s from SystemNotification s where s.isActive = true")
    Stream<SystemNotification> getActiveSystemNotifications();

    Optional<SystemNotification> getSystemNotificationById(UUID id);

}

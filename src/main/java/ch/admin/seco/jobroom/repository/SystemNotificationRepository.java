package ch.admin.seco.jobroom.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ch.admin.seco.jobroom.domain.SystemNotification;



public interface SystemNotificationRepository extends JpaRepository<SystemNotification, UUID> {

    @Query("select s from SystemNotification s")
    Stream<SystemNotification> getAllSystemNotifications();

    Optional<SystemNotification> getSystemNotificationById(UUID id);

}

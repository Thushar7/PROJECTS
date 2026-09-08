package nexi.cinetix.notification_service.repository;

import nexi.cinetix.notification_service.entity.Notification;
import nexi.cinetix.notification_service.enums.NotificationStatus;
import nexi.cinetix.notification_service.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);
    long countByStatus(NotificationStatus status);
}

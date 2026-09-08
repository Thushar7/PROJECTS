package nexi.cinetix.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.notification_service.dto.NotificationDTO;
import nexi.cinetix.notification_service.entity.Notification;
import nexi.cinetix.notification_service.enums.NotificationStatus;
import nexi.cinetix.notification_service.enums.NotificationType;
import nexi.cinetix.notification_service.repository.NotificationRepository;
import nexi.cinetix.notification_service.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Transactional
    public Notification handleInbound(NotificationDTO dto) {
        // map DTO to entity
        Notification notification = mapper.toEntity(dto);

        // Fallback message if producer didn't send explicit one
        String message = (dto.getMessage() == null || dto.getMessage().isBlank())
                ? dto.getEvent().getDefaultMessage()
                : dto.getMessage();
        notification.setMessage(message);

        // default 0 for non-booking events
        if (notification.getBookingId() == null) {
            notification.setBookingId(0L);
        }

        // override status to RECEIVED
        notification.setStatus(NotificationStatus.RECEIVED);

        // ensure notifiedAt present
        if (notification.getNotifiedAt() == null) {
            notification.setNotifiedAt(LocalDateTime.now());
        }

        // set createdAt
        notification.setCreatedAt(LocalDateTime.now());

        notification = repository.save(notification);
        log.info("Stored notification id={} event={} bookingId={} userId={}",
                notification.getId(), notification.getEvent(), notification.getBookingId(), notification.getUserId());
        return notification;
    }

    @Transactional
    public void markProcessed(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setStatus(NotificationStatus.PROCESSED);
            n.setProcessedAt(LocalDateTime.now());
        });
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Notification> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Notification> listAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Notification> getAdminNotifications() {
        return repository.findByTypeOrderByCreatedAtDesc(NotificationType.ADMIN);
    }
}

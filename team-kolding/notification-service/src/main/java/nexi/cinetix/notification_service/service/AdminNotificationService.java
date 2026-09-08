package nexi.cinetix.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.notification_service.dto.UserRegistrationDTO;
import nexi.cinetix.notification_service.dto.NotificationDTO;
import nexi.cinetix.notification_service.entity.Notification;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationStatus;
import nexi.cinetix.notification_service.enums.NotificationType;
import nexi.cinetix.notification_service.repository.NotificationRepository;
import nexi.cinetix.notification_service.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Service for handling admin notifications related to user events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void processUserRegistrationNotification(UserRegistrationDTO user) {
        log.info("Processing admin notification for new user registration: {}", user.getUsername());

        // Create and save admin notification to database
        Notification adminNotification = createAdminNotification(user);
        notificationRepository.save(adminNotification);

        log.info("Admin notification saved with ID: {} for user registration: {}",
                adminNotification.getId(), user.getUsername());

        // Additional processing logic
        logUserRegistrationDetails(user);
        updateRegistrationStatistics(user);
    }

    private Notification createAdminNotification(UserRegistrationDTO user) {
        String message = String.format("New user registered: %s (ID: %d, Email: %s, Role: %s)",
                user.getUsername(), user.getId(), user.getEmail(), user.getRole());

        LocalDateTime eventTime = user.getCreatedAt() != null
                ? LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC)
                : LocalDateTime.now();

        // build DTO then map
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(11L); // Always notify user with ID 11 (admin)
        dto.setBookingId(0L);
        dto.setPaymentId(null);
        dto.setEvent(NotificationEvent.USER_REGISTRATION);
        dto.setType(NotificationType.ADMIN);
        dto.setMessage(message);
        dto.setStatus(NotificationStatus.RECEIVED.name()); // desired status
        dto.setNotifiedAt(eventTime);

        Notification entity = notificationMapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now()); // ensure createdAt
        entity.setStatus(NotificationStatus.RECEIVED); // enforce status
        return entity;
    }

    private void logUserRegistrationDetails(UserRegistrationDTO user) {
        log.info("User Registration Details:");
        log.info("User ID: {}", user.getId());
        log.info("Username: {}", user.getUsername());
        log.info("Email: {}", user.getEmail());
        log.info("Role: {}", user.getRole());
        log.info("Genre Preference: {}", user.getGenrePreference());
        log.info("Language Preference: {}", user.getLanguagePreference());
        log.info("Registration Time: {}", user.getCreatedAt());
    }

    private void updateRegistrationStatistics(UserRegistrationDTO user) {
        // Implementation for updating registration statistics
        log.debug("Updating registration statistics for user: {}", user.getUsername());
    }
}

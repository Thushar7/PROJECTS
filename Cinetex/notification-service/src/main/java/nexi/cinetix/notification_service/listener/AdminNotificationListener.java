package nexi.cinetix.notification_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.notification_service.dto.UserRegistrationDTO;
import nexi.cinetix.notification_service.service.AdminNotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes user registration events from user-service and processes admin notifications
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationListener {

    private final AdminNotificationService adminNotificationService;

    @RabbitListener(queues = "${rabbitmq.user.registration.queue}")
    public void handleUserRegistrationEvent(Map<String, Object> userMap) {
        log.info("Received user registration event: {}", userMap);

        try {
            // Convert the map to UserRegistrationDTO
            UserRegistrationDTO user = convertToUserRegistrationDTO(userMap);

            // Process the user registration event
            adminNotificationService.processUserRegistrationNotification(user);
            log.info("Successfully processed user registration notification for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to process user registration notification. Error: {}", e.getMessage(), e);
        }
    }

    private UserRegistrationDTO convertToUserRegistrationDTO(Map<String, Object> userMap) {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setId(((Number) userMap.get("id")).longValue());
        dto.setUsername((String) userMap.get("username"));
        dto.setEmail((String) userMap.get("email"));
        dto.setRole((String) userMap.get("role"));
        dto.setGenrePreference((String) userMap.get("genrePreference"));
        dto.setLanguagePreference((String) userMap.get("languagePreference"));

        // Handle the createdAt timestamp
        if (userMap.get("createdAt") != null) {
            // This will handle different timestamp formats
            Object createdAt = userMap.get("createdAt");
            if (createdAt instanceof String string) {
                dto.setCreatedAt(java.time.Instant.parse((string)));
            } else if (createdAt instanceof Number number) {
                dto.setCreatedAt(java.time.Instant.ofEpochMilli(((number).longValue())));
            }
        }

        return dto;
    }
}

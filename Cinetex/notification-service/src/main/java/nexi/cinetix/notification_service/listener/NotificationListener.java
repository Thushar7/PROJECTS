package nexi.cinetix.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.notification_service.dto.NotificationDTO;
import nexi.cinetix.notification_service.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Consumes notification events published by booking-service.
 * Uses manual validation + service delegation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;
    private final Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "${rabbitmq.notification.queue:notification.queue}")
    public void onMessage(@Payload NotificationDTO payload,
                          @Header(value = AmqpHeaders.CONSUMER_QUEUE, required = false) String queue,
                          @Header(value = "x-event", required = false) String eventHeader,
                          @Header(value = "x-booking-id", required = false) Long bookingId,
                          @Header(value = "x-user-id", required = false) Long userId) {
        try {
            log.debug("Received raw notification payload={} queue={} eventHeader={} bookingId={} userId={}",
                    safeJson(payload), queue, eventHeader, bookingId, userId);

            // Basic bean validation
            Set<ConstraintViolation<NotificationDTO>> violations = validator.validate(payload);
            if (!violations.isEmpty()) {
                log.warn("Discarding invalid notification: {}", violations);
                return; // For now discard; alternatively send to DLQ.
            }

            var saved = notificationService.handleInbound(payload);
            // In a real system, additional dispatch (email/SMS/websocket) would occur here asynchronously.
            notificationService.markProcessed(saved.getId());
            log.info("Notification persisted id={} event={} bookingId={} userId={} status={}",
                    saved.getId(), saved.getEvent(), saved.getBookingId(), saved.getUserId(), saved.getStatus());
        } catch (Exception ex) {
            log.error("Failed processing notification message: {}", ex.getMessage(), ex);
            // Rely on default listener container retry / DLQ via dead-letter
            throw ex; // rethrow to allow message to be dead-lettered after retries
        }
    }

    private String safeJson(Object o) {
        try {return objectMapper.writeValueAsString(o);} catch (Exception e) {return String.valueOf(o);} }
}

package com.cinetix.booking_service.util;

import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.dtos.External.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.notification.exchange}")
    private String notificationExchange;

    @Value("${rabbitmq.notification.routingkey:notification.events}")
    private String defaultRoutingKey;

    public void publishBookingConfirmed(BookingResponseDTO booking) {
        NotificationDTO dto = NotificationUtils.bookingConfirmed(booking);
        publish(dto, dto.getEvent().routingKey());
    }


    public void publishBookingCancelled(BookingResponseDTO booking) {
        NotificationDTO dto = NotificationUtils.bookingCancelled(booking);
        publish(dto, dto.getEvent().routingKey());
    }

    public void publish(NotificationDTO dto, String routingKey) {
        try {
            String key = (routingKey == null || routingKey.isBlank()) ? defaultRoutingKey : routingKey;
            rabbitTemplate.convertAndSend(
                    notificationExchange,
                    key,
                    dto,
                    message -> {
                        var props = message.getMessageProperties();
                        props.setContentType("application/json");
                        props.setHeader("x-event", dto.getEvent().name());
                        props.setHeader("x-audience", dto.getType().name());
                        props.setHeader("x-user-id", dto.getUserId());
                        props.setHeader("x-booking-id", dto.getBookingId());
                        return message;
                    }
            );
            log.info("Published notification event={} key={} bookingId={} userId={}",
                    dto.getEvent(), key, dto.getBookingId(), dto.getUserId());
        } catch (Exception ex) {
            log.error("Failed to publish notification: {}", ex.getMessage(), ex);
        }
    }
}
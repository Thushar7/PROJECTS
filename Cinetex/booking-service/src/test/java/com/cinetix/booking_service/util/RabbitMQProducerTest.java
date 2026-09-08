package com.cinetix.booking_service.util;

import com.cinetix.booking_service.constants.BookingConstants;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.dtos.External.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.*;

class RabbitMQProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQProducer rabbitMQProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set values manually since @Value won't inject in unit tests
        rabbitMQProducer = new RabbitMQProducer(rabbitTemplate);
        // Use reflection or setter if needed to inject values
        ReflectionTestUtils.setField(rabbitMQProducer, "notificationExchange", "notification.exchange");
        ReflectionTestUtils.setField(rabbitMQProducer, "defaultRoutingKey", "notification.events");
    }

    @Test
    void testPublishBookingConfirmed() {
        BookingResponseDTO booking = new BookingResponseDTO();
        booking.setBookingId(1L);
        booking.setUserId(100L);
        booking.setPaymentId(200L);

        rabbitMQProducer.publishBookingConfirmed(booking);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("notification.exchange"),
                eq("notification.booking_confirmed"),
                any(NotificationDTO.class),
                any(MessagePostProcessor.class)
        );
    }

    @Test
    void testPublishBookingCancelled() {
        BookingResponseDTO booking = new BookingResponseDTO();
        booking.setBookingId(2L);
        booking.setUserId(101L);
        booking.setPaymentId(201L);

        rabbitMQProducer.publishBookingCancelled(booking);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("notification.exchange"),
                eq("notification.booking_cancelled"),
                any(NotificationDTO.class),
                any(MessagePostProcessor.class)
        );
    }

    @Test
    void testPublish_WithNullRoutingKey_UsesDefault() {
        NotificationDTO dto = new NotificationDTO();
        dto.setBookingId(3L);
        dto.setUserId(102L);
        dto.setEvent(BookingConstants.NotificationEvent.BOOKING_CONFIRMED);
        dto.setType(BookingConstants.NotificationType.USER);

        rabbitMQProducer.publish(dto, null);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("notification.exchange"),
                eq("notification.events"),
                eq(dto),
                any(MessagePostProcessor.class)
        );
    }
}
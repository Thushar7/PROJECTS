package nexi.cinetix.notification_service.listener;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nexi.cinetix.notification_service.dto.NotificationDTO;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationType;
import nexi.cinetix.notification_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationListenerTest {

    private NotificationService notificationService;
    private NotificationListener listener;
    private Validator validator;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        listener = new NotificationListener(notificationService, validator);
    }

    private NotificationDTO baseDto() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(1L);
        dto.setBookingId(2L);
        dto.setEvent(NotificationEvent.BOOKING_CONFIRMED);
        dto.setType(NotificationType.USER);
        return dto;
    }

    @Test
    void validMessageProcessesAndMarksProcessed() {
        var dto = baseDto();
        when(notificationService.handleInbound(any())).thenAnswer(inv -> {
            // mimic persisted entity by returning a Notification mapped object (simplified)
            var saved = nexi.cinetix.notification_service.entity.Notification.builder()
                    .id(99L)
                    .userId(dto.getUserId())
                    .bookingId(dto.getBookingId())
                    .event(dto.getEvent())
                    .type(dto.getType())
                    .message(dto.getEvent().getDefaultMessage())
                    .status(nexi.cinetix.notification_service.enums.NotificationStatus.RECEIVED)
                    .build();
            return saved;
        });

        listener.onMessage(dto, "notification.queue", dto.getEvent().name(), dto.getBookingId(), dto.getUserId());

        ArgumentCaptor<NotificationDTO> captor = ArgumentCaptor.forClass(NotificationDTO.class);
        verify(notificationService).handleInbound(captor.capture());
        verify(notificationService).markProcessed(99L);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
    }

    @Test
    void invalidMessageIsDiscarded() {
        var dto = baseDto();
        dto.setUserId(null); // invalid
        listener.onMessage(dto, "notification.queue", null, dto.getBookingId(), null);
        verify(notificationService, never()).handleInbound(any());
    }

    @Test
    void exceptionFromServiceRethrown() {
        var dto = baseDto();
        when(notificationService.handleInbound(any())).thenThrow(new RuntimeException("boom"));
        try {
            listener.onMessage(dto, "notification.queue", null, dto.getBookingId(), dto.getUserId());
        } catch (RuntimeException ex) {
            assertThat(ex).hasMessage("boom");
        }
        verify(notificationService).handleInbound(any());
    }
}


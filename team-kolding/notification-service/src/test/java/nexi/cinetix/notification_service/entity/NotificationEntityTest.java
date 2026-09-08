package nexi.cinetix.notification_service.entity;

import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationStatus;
import nexi.cinetix.notification_service.enums.NotificationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

class NotificationEntityTest {

    @Test
    void prePersistSetsCreatedAtWhenNull() {
        Notification n = Notification.builder()
                .userId(1L)
                .bookingId(2L)
                .event(NotificationEvent.BOOKING_CONFIRMED)
                .type(NotificationType.USER)
                .status(NotificationStatus.RECEIVED)
                .message("msg")
                .build();
        n.prePersist();
        assertThat(n.getCreatedAt()).isNotNull();
    }

    @Test
    void prePersistDoesNotOverrideExistingCreatedAt() {
        LocalDateTime t = LocalDateTime.now().minusDays(1);
        Notification n = Notification.builder()
                .userId(1L)
                .bookingId(2L)
                .event(NotificationEvent.BOOKING_CONFIRMED)
                .type(NotificationType.USER)
                .status(NotificationStatus.RECEIVED)
                .message("msg")
                .createdAt(t)
                .build();
        n.prePersist();
        assertThat(n.getCreatedAt()).isEqualTo(t);
    }
}


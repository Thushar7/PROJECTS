package nexi.cinetix.notification_service.repository;

import nexi.cinetix.notification_service.entity.Notification;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationStatus;
import nexi.cinetix.notification_service.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository repository;

    @Test
    void findByUserIdOrderedDesc() {
        Long userId = 77L;
        Notification older = Notification.builder()
                .userId(userId)
                .bookingId(1L)
                .event(NotificationEvent.BOOKING_CONFIRMED)
                .type(NotificationType.USER)
                .status(NotificationStatus.RECEIVED)
                .message("old")
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();
        Notification newer = Notification.builder()
                .userId(userId)
                .bookingId(2L)
                .event(NotificationEvent.BOOKING_CANCELLED)
                .type(NotificationType.USER)
                .status(NotificationStatus.RECEIVED)
                .message("new")
                .createdAt(LocalDateTime.now())
                .build();
        repository.saveAll(List.of(older, newer));

        var results = repository.findByUserIdOrderByCreatedAtDesc(userId);
        assertThat(results).hasSize(2);
        assertThat(results.getFirst().getMessage()).isEqualTo("new");
    }

    @Test
    void countByStatus() {
        Notification n = Notification.builder()
                .userId(1L)
                .bookingId(9L)
                .event(NotificationEvent.BOOKING_CONFIRMED)
                .type(NotificationType.USER)
                .status(NotificationStatus.RECEIVED)
                .message("msg")
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(n);
        long count = repository.countByStatus(NotificationStatus.RECEIVED);
        assertThat(count).isEqualTo(1);
    }
}


package nexi.cinetix.notification_service.service;

import nexi.cinetix.notification_service.dto.NotificationDTO;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationType;
import nexi.cinetix.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(NotificationService.class)
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository repository;

    private NotificationDTO base;

    @BeforeEach
    void setUp() {
        base = new NotificationDTO();
        base.setUserId(10L);
        base.setBookingId(55L);
        base.setEvent(NotificationEvent.BOOKING_CONFIRMED);
        base.setType(NotificationType.USER);
    }

    @Test
    void fallbacksToDefaultMessageWhenMissing() {
        var saved = notificationService.handleInbound(base);
        assertThat(saved.getMessage()).isEqualTo(NotificationEvent.BOOKING_CONFIRMED.getDefaultMessage());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void preservesCustomMessageWhenProvided() {
        base.setMessage("Custom message");
        var saved = notificationService.handleInbound(base);
        assertThat(saved.getMessage()).isEqualTo("Custom message");
    }

    @Test
    void markProcessedSetsStatusAndTimestamp() {
        var saved = notificationService.handleInbound(base);
        notificationService.markProcessed(saved.getId());
        var updated = repository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getProcessedAt()).isNotNull();
        // status should be PROCESSED
        assertThat(updated.getStatus().name()).isEqualTo("PROCESSED");
    }
}

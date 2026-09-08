package nexi.cinetix.notification_service.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEventTest {

    @Test
    void routingKeyPatternAndDefaultMessage() {
        for (NotificationEvent e : NotificationEvent.values()) {
            String key = e.routingKey();
            assertThat(key).startsWith("notification.");
            assertThat(e.getDefaultMessage()).isNotBlank();
            assertThat(key).contains(e.name().toLowerCase());
        }
    }
}


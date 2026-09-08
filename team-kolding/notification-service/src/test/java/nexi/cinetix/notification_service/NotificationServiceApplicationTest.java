package nexi.cinetix.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceApplicationTest {

    @Test
    void contextLoads() {
        // Just verifies the Spring context starts with current configuration
    }
}

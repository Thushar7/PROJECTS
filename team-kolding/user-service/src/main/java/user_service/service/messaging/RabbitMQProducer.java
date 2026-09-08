package user_service.service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import user_service.service.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.user.registration.exchange}")
    private String userRegistrationExchange;

    @Value("${rabbitmq.user.registration.routing-key}")
    private String userRegistrationRoutingKey;

    public void publishUserRegistrationEvent(User user) {
        try {
            log.info("Publishing user registration event for user: {}", user.getUsername());
            rabbitTemplate.convertAndSend(userRegistrationExchange, userRegistrationRoutingKey, user);
            log.info("Successfully published user registration event for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to publish user registration event for user: {}. Error: {}",
                     user.getUsername(), e.getMessage(), e);
        }
    }
}

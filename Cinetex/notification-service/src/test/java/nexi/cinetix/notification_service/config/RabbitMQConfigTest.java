package nexi.cinetix.notification_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit test of configuration logic without starting Spring context or requiring broker.
 */
class RabbitMQConfigTest {

    @Test
    void buildsBeansWithProvidedProperties() {
        RabbitMQConfig config = new RabbitMQConfig();
        // Use reflection to set @Value fields
        org.springframework.test.util.ReflectionTestUtils.setField(config, "notificationExchangeName", "unit.exchange");
        org.springframework.test.util.ReflectionTestUtils.setField(config, "notificationQueueName", "unit.queue");

        var exchange = config.notificationExchange();
        var queue = config.notificationQueue();

        assertThat(exchange.getName()).isEqualTo("unit.exchange");
        assertThat(queue.getName()).isEqualTo("unit.queue");

        ConnectionFactory cf = new CachingConnectionFactory();
        var template = config.rabbitTemplate(cf);
        assertThat(template.getMessageConverter()).isNotNull();
    }
}

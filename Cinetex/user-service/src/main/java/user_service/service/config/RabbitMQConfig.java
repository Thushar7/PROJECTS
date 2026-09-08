package user_service.service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.user.registration.exchange}")
    private String userRegistrationExchange;

    @Value("${rabbitmq.user.registration.queue}")
    private String userRegistrationQueue;

    @Value("${rabbitmq.user.registration.routing-key}")
    private String userRegistrationRoutingKey;

    @Bean
    public DirectExchange userRegistrationExchange() {
        return new DirectExchange(userRegistrationExchange);
    }

    @Bean
    public Queue userRegistrationQueue() {
        return new Queue(userRegistrationQueue, true); // durable queue
    }

    @Bean
    public Binding userRegistrationBinding() {
        return BindingBuilder
                .bind(userRegistrationQueue())
                .to(userRegistrationExchange())
                .with(userRegistrationRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

package nexi.cinetix.notification_service.config;

import org.springframework.amqp.core.*;
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

    @Value("${rabbitmq.movie.exchange}")
    private String movieExchange;

    @Value("${rabbitmq.movie.queue}")
    private String movieAddedQueue;

    @Value("${rabbitmq.movie.routingkey}")
    private String movieRoutingKey;

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
    public DirectExchange movieExchange() {
        return new DirectExchange(movieExchange);
    }

    @Bean
    public Queue movieQueue() {
        return new Queue(movieAddedQueue);
    }

    @Bean
    public Binding movieBinding() {
        return BindingBuilder
                .bind(movieQueue())
                .to(movieExchange())
                .with(movieRoutingKey);
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

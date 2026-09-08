package nexi.cinetix.movie_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Single consolidated RabbitMQ configuration for movie-service.
 * Responsibilities:
 *  - Declare durable topic exchange for movie events
 *  - Declare durable queue for movie.added events (so messages persist if consumer is down)
 *  - Bind queue with routing key
 *  - Provide JSON message converter + customized RabbitTemplate (publisher confirms & returns logging)
 */
@Configuration
@Slf4j
public class MovieRabbitConfig {

    @Value("${rabbitmq.movie.exchange:movie.exchange}")
    private String movieExchangeName;

    @Value("${rabbitmq.movie.queue:movie.added.queue}")
    private String movieQueueName;

    @Value("${rabbitmq.movie.routingkey:movie.added}")
    private String movieRoutingKey;

    // --- Topology Beans ---
    @Bean
    public TopicExchange movieExchange() {
        return new TopicExchange(movieExchangeName, true, false); // durable, not auto-delete
    }

    @Bean
    public Queue movieAddedQueue() {
        return QueueBuilder.durable(movieQueueName).build();
    }

    @Bean
    public Binding movieAddedBinding(Queue movieAddedQueue, TopicExchange movieExchange) {
        return BindingBuilder.bind(movieAddedQueue).to(movieExchange).with(movieRoutingKey);
    }

    // --- Infrastructure Beans ---
    @Bean
    public MessageConverter movieMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate movieRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(movieMessageConverter());
        // Enable mandatory to allow returns callback when unroutable
        template.setMandatory(true);
        // Publisher confirm & return callbacks (basic logging)
        template.setReturnsCallback(returned ->
            log.error("[RabbitMQ][Return] exchange=%s routingKey=%s replyCode=%d replyText=%s message=%s%n",
                    returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(), returned.getReplyText(), returned.getMessage())
        );
        template.setConfirmCallback((correlation, ack, cause) -> {
            if (!ack) {
                log.error("[RabbitMQ][NACK] correlation=%s cause=%s%n", correlation, cause);
            }
        });
        return template;
    }
}

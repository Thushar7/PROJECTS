package nexi.cinetix.movie_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MovieRabbitConfigTest {

    private MovieRabbitConfig config;

    @BeforeEach
    void setUp() {
        config = new MovieRabbitConfig();
        ReflectionTestUtils.setField(config, "movieExchangeName", "movie.exchange");
        ReflectionTestUtils.setField(config, "movieQueueName", "movie.added.queue");
        ReflectionTestUtils.setField(config, "movieRoutingKey", "movie.added");
    }

    @Test
    void movieExchange_shouldCreateDurableTopicExchange() {
        // Act
        TopicExchange exchange = config.movieExchange();

        // Assert
        assertNotNull(exchange);
        assertEquals("movie.exchange", exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    void movieAddedQueue_shouldCreateDurableQueue() {
        // Act
        Queue queue = config.movieAddedQueue();

        // Assert
        assertNotNull(queue);
        assertEquals("movie.added.queue", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void movieAddedBinding_shouldBindQueueToExchangeWithRoutingKey() {
        // Arrange
        Queue queue = new Queue("movie.added.queue");
        TopicExchange exchange = new TopicExchange("movie.exchange");

        // Act
        Binding binding = config.movieAddedBinding(queue, exchange);

        // Assert
        assertNotNull(binding);
        assertEquals("movie.added.queue", binding.getDestination());
        assertEquals("movie.exchange", binding.getExchange());
        assertEquals("movie.added", binding.getRoutingKey());
    }

    @Test
    void movieMessageConverter_shouldReturnJacksonConverter() {
        // Act
        MessageConverter converter = config.movieMessageConverter();

        // Assert
        assertNotNull(converter);
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter);
    }

    @Test
    void movieRabbitTemplate_shouldConfigureTemplateCorrectly() {
        // Arrange
        ConnectionFactory factory = mock(ConnectionFactory.class);

        // Act
        RabbitTemplate template = config.movieRabbitTemplate(factory);

        // Assert
        assertNotNull(template);
        assertEquals(config.movieMessageConverter().getClass(), template.getMessageConverter().getClass());
    }
}
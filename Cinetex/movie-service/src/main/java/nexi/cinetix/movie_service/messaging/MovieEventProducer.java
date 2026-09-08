package nexi.cinetix.movie_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieEventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.movie.exchange:movie.exchange}")
    private String movieExchange;

    @Value("${rabbitmq.movie.routingkey:movie.added}")
    private String movieAddedRoutingKey;

    public void publishMovieAdded(MovieAddedEvent event) {
        try {
            rabbitTemplate.convertAndSend(movieExchange, movieAddedRoutingKey, event, m -> {
                m.getMessageProperties().setContentType("application/json");
                m.getMessageProperties().setHeader("x-event", "MOVIE_ADDED");
                m.getMessageProperties().setHeader("x-movie-id", event.getMovieId());
                return m;
            });
            log.info("Published MOVIE_ADDED event movieId={} title={}", event.getMovieId(), event.getTitle());
        } catch (Exception ex) {
            log.error("Failed to publish MOVIE_ADDED event: {}", ex.getMessage(), ex);
        }
    }

    public record MovieAddedEvent(Integer movieId, String title, String description) {
        public Integer getMovieId() {return movieId;}
        public String getTitle() {return title;}
        public String getDescription() {return description;}
    }
}

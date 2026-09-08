package nexi.cinetix.notification_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationType;
import nexi.cinetix.notification_service.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieAddedListener {

    private final NotificationService notificationService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${user.service.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

    @RabbitListener(queues = "${rabbitmq.movie.queue:movie.added.queue}")
    public void onMovieAdded(MovieAddedEvent event) {
        log.info("Received MOVIE_ADDED event movieId={} title={}", event.movieId(), event.title());
        try {
            Long[] userIds = restTemplate.getForObject(userServiceBaseUrl + "/user/internal/ids", Long[].class);
            if (userIds == null || userIds.length == 0) {
                log.warn("No users found to broadcast MOVIE_ADDED notification");
                return;
            }
            log.info("Broadcasting movie added notification to {} users", userIds.length);
            for (Long userId : userIds) {
                var dto = new nexi.cinetix.notification_service.dto.NotificationDTO();
                dto.setUserId(userId);
                dto.setBookingId(0L); // not applicable
                dto.setEvent(NotificationEvent.MOVIE_ADDED);
                dto.setType(NotificationType.USER);
                dto.setMessage("New movie added: " + event.title());
                dto.setNotifiedAt(LocalDateTime.now());
                var saved = notificationService.handleInbound(dto);
                notificationService.markProcessed(saved.getId()); // keep status lifecycle consistent
            }
        } catch (Exception ex) {
            log.error("Failed to broadcast MOVIE_ADDED notifications: {}", ex.getMessage(), ex);
        }
    }

    public record MovieAddedEvent(Integer movieId, String title, String description) {}
}

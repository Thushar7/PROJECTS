package nexi.cinetix.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationType;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;

    @NotNull
    @Positive
    private Long userId;

    private String message; // optional, will fallback to event default if null/blank

    // Booking id may be 0 for non-booking domain events (e.g., MOVIE_ADDED broadcast)
    @PositiveOrZero
    private Long bookingId;

    // Relax validation: paymentId is optional and some producers may send 0 when not yet assigned.
    // Accept null or zero; enforce non-negative only.
    @PositiveOrZero
    private Long paymentId; // optional depending on flow

    private LocalDateTime notifiedAt; // will be set by consumer

    private String status; // status in persistence layer

    @NotNull
    private NotificationEvent event;

    @NotNull
    private NotificationType type;
}

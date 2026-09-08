package nexi.cinetix.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationResponseDTO {

    Long id;
    Long userId;
    Long bookingId;
    Long paymentId;
    String event;
    String type;
    String status;
    String message;
    java.time.LocalDateTime createdAt;
    java.time.LocalDateTime processedAt;
}

package nexi.cinetix.notification_service.entity;

import jakarta.persistence.*;
import lombok.*;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationStatus;
import nexi.cinetix.notification_service.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long bookingId;

    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status;

    @Column(nullable = false, length = 500)
    private String message;

    // NEW: original event occurrence time coming from producer (or set locally)
    @Column(nullable = false)
    private LocalDateTime notifiedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (notifiedAt == null) notifiedAt = createdAt; // ensure non-null to satisfy NOT NULL db column
    }
}

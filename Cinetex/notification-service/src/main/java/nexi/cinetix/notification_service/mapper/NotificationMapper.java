package nexi.cinetix.notification_service.mapper;

import nexi.cinetix.notification_service.dto.NotificationDTO;
import nexi.cinetix.notification_service.dto.NotificationResponseDTO;
import nexi.cinetix.notification_service.entity.Notification;
import nexi.cinetix.notification_service.enums.NotificationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public NotificationDTO toDto(Notification entity) {
        if (entity == null) return null;

        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setBookingId(entity.getBookingId());
        dto.setPaymentId(entity.getPaymentId());
        dto.setMessage(entity.getMessage());
        dto.setNotifiedAt(entity.getNotifiedAt());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setEvent(entity.getEvent());
        dto.setType(entity.getType());

        return dto;
    }

    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) return null;

        NotificationStatus status = null;
        if (dto.getStatus() != null) {
            try {
                status = NotificationStatus.valueOf(dto.getStatus());
            } catch (IllegalArgumentException e) {
                status = NotificationStatus.NEW; // fallback to default
            }
        } else {
            status = NotificationStatus.NEW;
        }

        return Notification.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .bookingId(dto.getBookingId())
                .paymentId(dto.getPaymentId())
                .message(dto.getMessage())
                .notifiedAt(dto.getNotifiedAt() != null ? dto.getNotifiedAt() : LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .status(status)
                .event(dto.getEvent())
                .type(dto.getType())
                .build();
    }

    public NotificationResponseDTO toResponseDto(Notification entity) {
        if (entity == null) return null;

        return NotificationResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .bookingId(entity.getBookingId())
                .paymentId(entity.getPaymentId())
                .event(entity.getEvent().name())
                .type(entity.getType().name())
                .status(entity.getStatus().name())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .processedAt(entity.getProcessedAt())
                .build();
    }
}

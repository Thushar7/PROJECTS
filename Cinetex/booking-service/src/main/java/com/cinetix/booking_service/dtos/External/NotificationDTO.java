package com.cinetix.booking_service.dtos.External;

import com.cinetix.booking_service.constants.BookingConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;

    @NotNull(message = "userId is required")
    @Positive(message = "userId must be positive")
    private Long userId;

    // Message becomes optional (can be set from event default)
    private String message;

    @NotNull(message = "bookingId is required")
    @Positive(message = "bookingId must be positive")
    private Long bookingId;

    // Optional depending on flow
    @Positive(message = "paymentId must be positive")
    private Long paymentId;

    private LocalDateTime notifiedAt;

    private String status;

    /**
     * NEW: the event that occurred, e.g., BOOKING_CONFIRMED.
     */
    @NotNull(message = "event is required")
    private BookingConstants.NotificationEvent event;

    /**
     * Existing meaning preserved: audience (ADMIN/USER).
     * Default is usually USER in most product flows.
     */
    @NotNull(message = "type (audience) is required")
    private BookingConstants.NotificationType type;
}
package com.cinetix.booking_service.util;

import com.cinetix.booking_service.constants.BookingConstants;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.dtos.External.NotificationDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class NotificationUtils {

    private NotificationUtils() {}

    public static NotificationDTO bookingConfirmed(BookingResponseDTO booking) {
        return buildBase(booking,
                BookingConstants.NotificationEvent.BOOKING_CONFIRMED,
                /* audience */ BookingConstants.NotificationType.USER,
                /* overrideMessage */ null);
    }



    public static NotificationDTO bookingCancelled(BookingResponseDTO booking) {
        return buildBase(booking,
                BookingConstants.NotificationEvent.BOOKING_CANCELLED,
                BookingConstants.NotificationType.USER,
                null);
    }

    public static NotificationDTO paymentSuccess(BookingResponseDTO booking) {
        return buildBase(booking,
                BookingConstants.NotificationEvent.PAYMENT_SUCCESS,
                BookingConstants.NotificationType.USER,
                null);
    }



    private static NotificationDTO buildBase(BookingResponseDTO booking,
                                             BookingConstants.NotificationEvent event,
                                             BookingConstants.NotificationType audience,
                                             String overrideMessage) {
        requireBooking(booking);
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(booking.getUserId());
        dto.setBookingId(booking.getBookingId());
        dto.setPaymentId(booking.getPaymentId());
        dto.setEvent(event);
        dto.setType(audience);
        dto.setMessage(overrideMessage != null ? overrideMessage : event.getDefaultMessage());
        dto.setStatus(BookingConstants.STATUS_NEW);
        dto.setNotifiedAt(LocalDateTime.now());
        return dto;
    }

    private static void requireBooking(BookingResponseDTO booking) {
        Objects.requireNonNull(booking, "booking must not be null");
        Objects.requireNonNull(booking.getUserId(), "booking.userId must not be null");
        Objects.requireNonNull(booking.getBookingId(), "booking.bookingId must not be null");
    }
}

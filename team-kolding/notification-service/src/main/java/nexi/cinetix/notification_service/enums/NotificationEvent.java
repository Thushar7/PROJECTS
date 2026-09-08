package nexi.cinetix.notification_service.enums;

import java.util.Locale;

/**
 * Enumeration of notification events published by booking-service.
 * Keep names in sync with producer to ensure Jackson deserialization works.
 */
public enum NotificationEvent {
    BOOKING_CONFIRMED("Booking confirmed successfully."),
    BOOKING_CANCELLED("Your booking is cancelled successfully."),
    PAYMENT_SUCCESS("Payment successful for your booking."),
    MOVIE_ADDED("A new movie has been added."),
    USER_REGISTRATION("New user has registered.");

    private final String defaultMessage;

    NotificationEvent(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String routingKey() {
        return "notification." + name().toLowerCase(Locale.ROOT);
    }
}

package com.cinetix.booking_service.constants;

import java.util.Locale;

public class BookingConstants {

    // --- User-facing Messages (single source of truth) ---
    public static final String BOOKING_NOT_FOUND = "Booking not found. ";
    public static final String BOOKING_CONFIRMED = "Booking confirmed successfully.";
    public static final String BOOKING_FAILED = "Booking failed.";
    public static final String BOOKING_CANCELLED = "Your booking is cancelled successfully.";
    public static final String PAYMENT_SUCCESS = "Payment successful for your booking.";

    // --- Optional: Notification Statuses ---
    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_FAILED = "FAILED";

    /**
     * Audience / recipient of the notification.
     * Keeps your existing meaning intact.
     */
    public enum NotificationType {
        USER
    }

    /**
     * What kind of event triggered the notification.
     * Each event carries a default message (from constants above),
     * so no other class needs to hardcode strings.
     */
    public enum NotificationEvent {
        BOOKING_CONFIRMED(BookingConstants.BOOKING_CONFIRMED),
        BOOKING_CANCELLED(BookingConstants.BOOKING_CANCELLED),
        PAYMENT_SUCCESS(BookingConstants.PAYMENT_SUCCESS);

        private final String defaultMessage;

        NotificationEvent(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }

        /**
         * Standard topic-style routing key, e.g., "notification.booking_confirmed".
         */
        public String routingKey() {
            return "notification." + name().toLowerCase(Locale.ROOT);
        }
    }
}

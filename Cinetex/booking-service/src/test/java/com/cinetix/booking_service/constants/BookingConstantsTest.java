package com.cinetix.booking_service.constants;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingConstantsTest {

    @Test
    void constants_haveExpectedValues() {
        assertEquals("Booking not found. ", BookingConstants.BOOKING_NOT_FOUND);
        assertEquals("Booking confirmed successfully.", BookingConstants.BOOKING_CONFIRMED);
        assertEquals("Booking failed.", BookingConstants.BOOKING_FAILED);
        assertEquals("Your booking is cancelled successfully.", BookingConstants.BOOKING_CANCELLED);
        assertEquals("Payment successful for your booking.", BookingConstants.PAYMENT_SUCCESS);

        assertEquals("NEW", BookingConstants.STATUS_NEW);
        assertEquals("SENT", BookingConstants.STATUS_SENT);
        assertEquals("FAILED", BookingConstants.STATUS_FAILED);
    }

    @Test
    void notificationEvent_defaultMessages_matchConstants() {
        assertEquals(BookingConstants.BOOKING_CONFIRMED,
                BookingConstants.NotificationEvent.BOOKING_CONFIRMED.getDefaultMessage());
        assertEquals(BookingConstants.BOOKING_CANCELLED,
                BookingConstants.NotificationEvent.BOOKING_CANCELLED.getDefaultMessage());
        assertEquals(BookingConstants.PAYMENT_SUCCESS,
                BookingConstants.NotificationEvent.PAYMENT_SUCCESS.getDefaultMessage());
    }

    @Test
    void notificationEvent_routingKeys_areLowercasePrefixed() {
        assertEquals("notification.booking_confirmed",
                BookingConstants.NotificationEvent.BOOKING_CONFIRMED.routingKey());
        assertEquals("notification.booking_cancelled",
                BookingConstants.NotificationEvent.BOOKING_CANCELLED.routingKey());
        assertEquals("notification.payment_success",
                BookingConstants.NotificationEvent.PAYMENT_SUCCESS.routingKey());
    }

    @Test
    void notificationType_enumContainsAdminAndUser() {
        Set<BookingConstants.NotificationType> types =
                EnumSet.allOf(BookingConstants.NotificationType.class);
        assertTrue(types.contains(BookingConstants.NotificationType.USER));
        assertEquals(1, types.size());
    }

    @Test
    void canInstantiateConstantsClass() {
        BookingConstants instance = new BookingConstants();
        assertNotNull(instance);
    }
}

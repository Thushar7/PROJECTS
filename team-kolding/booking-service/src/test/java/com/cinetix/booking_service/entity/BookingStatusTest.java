package com.cinetix.booking_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    void bookingStatus_allValues() {
        BookingStatus[] statuses = BookingStatus.values();
        assertEquals(4, statuses.length);

        assertEquals(BookingStatus.PENDING, statuses[0]);
        assertEquals(BookingStatus.CONFIRMED, statuses[1]);
        assertEquals(BookingStatus.CANCELLED, statuses[2]);
        assertEquals(BookingStatus.FAILED, statuses[3]);
    }

    @Test
    void bookingStatus_valueOf() {
        assertEquals(BookingStatus.PENDING, BookingStatus.valueOf("PENDING"));
        assertEquals(BookingStatus.CONFIRMED, BookingStatus.valueOf("CONFIRMED"));
        assertEquals(BookingStatus.CANCELLED, BookingStatus.valueOf("CANCELLED"));
        assertEquals(BookingStatus.FAILED, BookingStatus.valueOf("FAILED"));
    }

    @Test
    void bookingStatus_toString() {
        assertEquals("PENDING", BookingStatus.PENDING.toString());
        assertEquals("CONFIRMED", BookingStatus.CONFIRMED.toString());
        assertEquals("CANCELLED", BookingStatus.CANCELLED.toString());
        assertEquals("FAILED", BookingStatus.FAILED.toString());
    }

    @Test
    void bookingStatus_ordinal() {
        assertEquals(0, BookingStatus.PENDING.ordinal());
        assertEquals(1, BookingStatus.CONFIRMED.ordinal());
        assertEquals(2, BookingStatus.CANCELLED.ordinal());
        assertEquals(3, BookingStatus.FAILED.ordinal());
    }

    @Test
    void bookingStatus_name() {
        assertEquals("PENDING", BookingStatus.PENDING.name());
        assertEquals("CONFIRMED", BookingStatus.CONFIRMED.name());
        assertEquals("CANCELLED", BookingStatus.CANCELLED.name());
        assertEquals("FAILED", BookingStatus.FAILED.name());
    }

    @Test
    void bookingStatus_invalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            BookingStatus.valueOf("INVALID_STATUS");
        });
    }
}

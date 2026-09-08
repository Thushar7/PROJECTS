package com.cinetix.booking_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatAlreadyBookedExceptionTest {

    @Test
    void seatAlreadyBookedException_withMessage() {
        String message = "Seat A1 is already booked for this showtime";
        SeatAlreadyBookedException exception = new SeatAlreadyBookedException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void seatAlreadyBookedException_inheritanceFromRuntimeException() {
        SeatAlreadyBookedException exception = new SeatAlreadyBookedException("Test message");
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void seatAlreadyBookedException_nullMessage() {
        SeatAlreadyBookedException exception = new SeatAlreadyBookedException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void seatAlreadyBookedException_emptyMessage() {
        SeatAlreadyBookedException exception = new SeatAlreadyBookedException("");
        assertEquals("", exception.getMessage());
    }
}

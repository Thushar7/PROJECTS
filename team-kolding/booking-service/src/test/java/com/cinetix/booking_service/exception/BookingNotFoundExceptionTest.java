package com.cinetix.booking_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingNotFoundExceptionTest {

    @Test
    void bookingNotFoundException_withMessage() {
        String message = "Booking with id 123 not found";
        BookingNotFoundException exception = new BookingNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void bookingNotFoundException_inheritanceFromRuntimeException() {
        BookingNotFoundException exception = new BookingNotFoundException("Test message");
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void bookingNotFoundException_nullMessage() {
        BookingNotFoundException exception = new BookingNotFoundException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void bookingNotFoundException_emptyMessage() {
        BookingNotFoundException exception = new BookingNotFoundException("");
        assertEquals("", exception.getMessage());
    }
}

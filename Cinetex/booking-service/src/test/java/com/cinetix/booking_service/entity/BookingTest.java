package com.cinetix.booking_service.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void booking_builderPattern() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .bookingId(1L)
                .userId(10L)
                .movieId(20L)
                .theatreId(30L)
                .showtimeId(40L)
                .totalAmount(BigDecimal.valueOf(150.50))
                .paymentId(100L)
                .seatCount(2L)
                .bookingStatus(BookingStatus.PENDING)
                .createdAt(now)
                .build();

        assertEquals(1L, booking.getBookingId());
        assertEquals(10L, booking.getUserId());
        assertEquals(20L, booking.getMovieId());
        assertEquals(30L, booking.getTheatreId());
        assertEquals(40L, booking.getShowtimeId());
        assertEquals(BigDecimal.valueOf(150.50), booking.getTotalAmount());
        assertEquals(100L, booking.getPaymentId());
        assertEquals(2L, booking.getSeatCount());
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
        assertEquals(now, booking.getCreatedAt());
    }

    @Test
    void booking_settersAndGetters() {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();

        booking.setBookingId(5L);
        booking.setUserId(15L);
        booking.setMovieId(25L);
        booking.setTheatreId(35L);
        booking.setShowtimeId(45L);
        booking.setTotalAmount(BigDecimal.valueOf(99.99));
        booking.setPaymentId(200L);
        booking.setSeatCount(3L);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(now);

        assertEquals(5L, booking.getBookingId());
        assertEquals(15L, booking.getUserId());
        assertEquals(25L, booking.getMovieId());
        assertEquals(35L, booking.getTheatreId());
        assertEquals(45L, booking.getShowtimeId());
        assertEquals(BigDecimal.valueOf(99.99), booking.getTotalAmount());
        assertEquals(200L, booking.getPaymentId());
        assertEquals(3L, booking.getSeatCount());
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
        assertEquals(now, booking.getCreatedAt());
    }

    @Test
    void booking_addSeats() {
        Booking booking = new Booking();
        ReservedSeat seat1 = ReservedSeat.builder()
                .seatLabel("A1")
                .showtimeId(100L)
                .build();
        ReservedSeat seat2 = ReservedSeat.builder()
                .seatLabel("A2")
                .showtimeId(100L)
                .build();

        booking.addSeats(List.of(seat1, seat2));

        assertEquals(2, booking.getSeats().size());
        assertEquals(booking, seat1.getBooking());
        assertEquals(booking, seat2.getBooking());
    }

    @Test
    void booking_equalsAndHashCode() {
        Booking booking1 = Booking.builder()
                .bookingId(1L)
                .userId(10L)
                .build();

        Booking booking2 = Booking.builder()
                .bookingId(1L)
                .userId(10L)
                .build();

        Booking booking3 = Booking.builder()
                .bookingId(2L)
                .userId(10L)
                .build();

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    void booking_toString() {
        Booking booking = Booking.builder()
                .bookingId(1L)
                .userId(10L)
                .build();

        String toString = booking.toString();
        assertTrue(toString.contains("bookingId"));
        assertTrue(toString.contains("userId"));
    }
}

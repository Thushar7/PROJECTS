package com.cinetix.booking_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservedSeatTest {

    @Test
    void reservedSeat_builderPattern() {
        ReservedSeat seat = ReservedSeat.builder()
                .id(1L)
                .seatLabel("A1")
                .showtimeId(100L)
                .build();

        assertEquals(1L, seat.getId());
        assertEquals("A1", seat.getSeatLabel());
        assertEquals(100L, seat.getShowtimeId());
    }

    @Test
    void reservedSeat_settersAndGetters() {
        ReservedSeat seat = new ReservedSeat();
        Booking booking = new Booking();

        seat.setId(5L);
        seat.setSeatLabel("B5");
        seat.setShowtimeId(200L);
        seat.setBooking(booking);

        assertEquals(5L, seat.getId());
        assertEquals("B5", seat.getSeatLabel());
        assertEquals(200L, seat.getShowtimeId());
        assertEquals(booking, seat.getBooking());
    }

    @Test
    void reservedSeat_equalsAndHashCode() {
        ReservedSeat seat1 = ReservedSeat.builder()
                .id(1L)
                .seatLabel("A1")
                .showtimeId(100L)
                .build();

        ReservedSeat seat2 = ReservedSeat.builder()
                .id(1L)
                .seatLabel("A1")
                .showtimeId(100L)
                .build();

        ReservedSeat seat3 = ReservedSeat.builder()
                .id(2L)
                .seatLabel("A2")
                .showtimeId(100L)
                .build();

        assertEquals(seat1, seat2);
        assertNotEquals(seat1, seat3);
        assertEquals(seat1.hashCode(), seat2.hashCode());
    }

    @Test
    void reservedSeat_toString() {
        ReservedSeat seat = ReservedSeat.builder()
                .id(1L)
                .seatLabel("A1")
                .showtimeId(100L)
                .build();

        String toString = seat.toString();
        assertTrue(toString.contains("seatLabel"));
        assertTrue(toString.contains("showtimeId"));
    }
}

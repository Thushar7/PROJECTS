package com.cinetix.booking_service.dtos;

import com.cinetix.booking_service.entity.BookingStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestDTOTest {

    @Test
    void bookingRequestDTO_allArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<String> seats = List.of("A1", "A2");

        BookingRequestDTO dto = new BookingRequestDTO(
                1L, 10L, 20L, 30L, 40L,
                BigDecimal.valueOf(150.0), 100L,
                BookingStatus.PENDING, now, 2L, seats
        );

        assertEquals(1L, dto.getBookingId());
        assertEquals(10L, dto.getUserId());
        assertEquals(20L, dto.getMovieId());
        assertEquals(30L, dto.getTheatreId());
        assertEquals(40L, dto.getShowtimeId());
        assertEquals(BigDecimal.valueOf(150.0), dto.getTotalAmount());
        assertEquals(100L, dto.getPaymentId());
        assertEquals(BookingStatus.PENDING, dto.getBookingStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(2L, dto.getSeatCount());
        assertEquals(seats, dto.getSeatNumbers());
    }

    @Test
    void bookingRequestDTO_settersAndGetters() {
        BookingRequestDTO dto = new BookingRequestDTO();
        LocalDateTime now = LocalDateTime.now();
        List<String> seats = List.of("B1", "B2", "B3");

        dto.setBookingId(5L);
        dto.setUserId(15L);
        dto.setMovieId(25L);
        dto.setTheatreId(35L);
        dto.setShowtimeId(45L);
        dto.setTotalAmount(BigDecimal.valueOf(200.0));
        dto.setPaymentId(200L);
        dto.setBookingStatus(BookingStatus.CONFIRMED);
        dto.setCreatedAt(now);
        dto.setSeatCount(3L);
        dto.setSeatNumbers(seats);

        assertEquals(5L, dto.getBookingId());
        assertEquals(15L, dto.getUserId());
        assertEquals(25L, dto.getMovieId());
        assertEquals(35L, dto.getTheatreId());
        assertEquals(45L, dto.getShowtimeId());
        assertEquals(BigDecimal.valueOf(200.0), dto.getTotalAmount());
        assertEquals(200L, dto.getPaymentId());
        assertEquals(BookingStatus.CONFIRMED, dto.getBookingStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(3L, dto.getSeatCount());
        assertEquals(seats, dto.getSeatNumbers());
    }

    @Test
    void bookingRequestDTO_equalsAndHashCode() {
        BookingRequestDTO dto1 = new BookingRequestDTO();
        dto1.setBookingId(1L);
        dto1.setUserId(10L);

        BookingRequestDTO dto2 = new BookingRequestDTO();
        dto2.setBookingId(1L);
        dto2.setUserId(10L);

        BookingRequestDTO dto3 = new BookingRequestDTO();
        dto3.setBookingId(2L);
        dto3.setUserId(10L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void bookingRequestDTO_toString() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setBookingId(1L);
        dto.setUserId(10L);

        String toString = dto.toString();
        assertTrue(toString.contains("bookingId"));
        assertTrue(toString.contains("userId"));
    }
}

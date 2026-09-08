package com.cinetix.booking_service.dtos;

import com.cinetix.booking_service.entity.BookingStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingResponseDTOTest {

    @Test
    void bookingResponseDTO_builderPattern() {
        LocalDateTime now = LocalDateTime.now();
        List<String> seats = List.of("A1", "A2");

        BookingResponseDTO dto = BookingResponseDTO.builder()
                .bookingId(1L)
                .userId(10L)
                .movieId(20L)
                .theatreId(30L)
                .showtimeId("40")
                .seatCount(2L)
                .totalAmount(BigDecimal.valueOf(150.0))
                .bookingStatus(BookingStatus.CONFIRMED)
                .bookingTime(now)
                .paymentId(100L)
                .createdAt(now)
                .seatNumbers(seats)
                .build();

        assertEquals(1L, dto.getBookingId());
        assertEquals(10L, dto.getUserId());
        assertEquals(20L, dto.getMovieId());
        assertEquals(30L, dto.getTheatreId());
        assertEquals("40", dto.getShowtimeId());
        assertEquals(2L, dto.getSeatCount());
        assertEquals(BigDecimal.valueOf(150.0), dto.getTotalAmount());
        assertEquals(BookingStatus.CONFIRMED, dto.getBookingStatus());
        assertEquals(now, dto.getBookingTime());
        assertEquals(100L, dto.getPaymentId());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(seats, dto.getSeatNumbers());
    }

    @Test
    void bookingResponseDTO_settersAndGetters() {
        BookingResponseDTO dto = new BookingResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        List<String> seats = List.of("B1", "B2");

        dto.setBookingId(5L);
        dto.setUserId(15L);
        dto.setMovieId(25L);
        dto.setTheatreId(35L);
        dto.setShowtimeId("45");
        dto.setSeatCount(2L);
        dto.setTotalAmount(BigDecimal.valueOf(200.0));
        dto.setBookingStatus(BookingStatus.PENDING);
        dto.setBookingTime(now);
        dto.setPaymentId(200L);
        dto.setCreatedAt(now);
        dto.setSeatNumbers(seats);

        assertEquals(5L, dto.getBookingId());
        assertEquals(15L, dto.getUserId());
        assertEquals(25L, dto.getMovieId());
        assertEquals(35L, dto.getTheatreId());
        assertEquals("45", dto.getShowtimeId());
        assertEquals(2L, dto.getSeatCount());
        assertEquals(BigDecimal.valueOf(200.0), dto.getTotalAmount());
        assertEquals(BookingStatus.PENDING, dto.getBookingStatus());
        assertEquals(now, dto.getBookingTime());
        assertEquals(200L, dto.getPaymentId());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(seats, dto.getSeatNumbers());
    }

    @Test
    void bookingResponseDTO_equalsAndHashCode() {
        BookingResponseDTO dto1 = new BookingResponseDTO();
        dto1.setBookingId(1L);
        dto1.setUserId(10L);

        BookingResponseDTO dto2 = new BookingResponseDTO();
        dto2.setBookingId(1L);
        dto2.setUserId(10L);

        BookingResponseDTO dto3 = new BookingResponseDTO();
        dto3.setBookingId(2L);
        dto3.setUserId(10L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void bookingResponseDTO_toString() {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(1L);
        dto.setUserId(10L);

        String toString = dto.toString();
        assertTrue(toString.contains("bookingId"));
        assertTrue(toString.contains("userId"));
    }

    @Test
    void bookingResponseDTO_noArgsConstructor() {
        BookingResponseDTO dto = new BookingResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getBookingId());
        assertNull(dto.getUserId());
    }

    @Test
    void bookingResponseDTO_allArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<String> seats = List.of("C1", "C2");

        BookingResponseDTO dto = new BookingResponseDTO(
                1L, 10L, 20L, 30L, "40", 2L,
                BigDecimal.valueOf(150.0), BookingStatus.CONFIRMED,
                now, 100L, now, seats
        );

        assertEquals(1L, dto.getBookingId());
        assertEquals(10L, dto.getUserId());
        assertEquals(seats, dto.getSeatNumbers());
    }
}

package com.cinetix.booking_service.mapper;

import com.cinetix.booking_service.dtos.BookingRequestDTO;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.entity.Booking;
import com.cinetix.booking_service.entity.BookingStatus;
import com.cinetix.booking_service.entity.ReservedSeat;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    BookingMapper mapper = new BookingMapper();

    @Test
    void toEntity_and_back() {
        BookingRequestDTO req = new BookingRequestDTO(null, 1L,2L,3L,4L, BigDecimal.ONE,5L, BookingStatus.PENDING, LocalDateTime.now(),2L,List.of("A1","A2"));
        Booking entity = mapper.toEntity(req);
        assertEquals(2, entity.getSeats().size());
        BookingResponseDTO dto = mapper.toDTO(entity);
        assertEquals(List.of("A1","A2"), dto.getSeatNumbers());
        assertEquals("4", dto.getShowtimeId()); // showtime converted to String
    }

    @Test
    void toDTO_nullSeats() {
        Booking booking = Booking.builder().bookingId(9L).userId(1L).movieId(2L).theatreId(3L).showtimeId(4L)
                .totalAmount(BigDecimal.TEN).paymentId(7L).seatCount(0L).bookingStatus(BookingStatus.PENDING).createdAt(LocalDateTime.now()).build();
        BookingResponseDTO dto = mapper.toDTO(booking);
        // Implementation returns empty list (null check triggers only when seats collection itself is null)
        assertTrue(dto.getSeatNumbers() == null || dto.getSeatNumbers().isEmpty());
    }

    @Test
    void toDTO_withSeats() {
        Booking booking = Booking.builder().bookingId(9L).userId(1L).movieId(2L).theatreId(3L).showtimeId(4L)
                .totalAmount(BigDecimal.TEN).paymentId(7L).seatCount(2L).bookingStatus(BookingStatus.PENDING).createdAt(LocalDateTime.now()).build();
        booking.addSeat(ReservedSeat.builder().seatLabel("B1").showtimeId(4L).build());
        booking.addSeat(ReservedSeat.builder().seatLabel("B2").showtimeId(4L).build());
        BookingResponseDTO dto = mapper.toDTO(booking);
        assertEquals(2, dto.getSeatNumbers().size());
    }
}

package com.cinetix.booking_service.mapper;

import com.cinetix.booking_service.dtos.BookingRequestDTO;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.entity.Booking;
import com.cinetix.booking_service.entity.ReservedSeat;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDTO bookingRequestDTO) {
        Booking booking = Booking.builder()
                .userId(bookingRequestDTO.getUserId())
                .bookingId(bookingRequestDTO.getBookingId())
                .movieId(bookingRequestDTO.getMovieId())
                .theatreId(bookingRequestDTO.getTheatreId())
                .showtimeId(bookingRequestDTO.getShowtimeId())
                .totalAmount(bookingRequestDTO.getTotalAmount())
                .seatCount(bookingRequestDTO.getSeatCount())
                .paymentId(bookingRequestDTO.getPaymentId())
                .bookingStatus(bookingRequestDTO.getBookingStatus())
                .createdAt(bookingRequestDTO.getCreatedAt())
                .build();

        if (bookingRequestDTO.getSeatNumbers() != null) {
            List<ReservedSeat> seats = bookingRequestDTO.getSeatNumbers().stream()
                    .map(label -> ReservedSeat.builder()
                            .seatLabel(label)
                            .showtimeId(bookingRequestDTO.getShowtimeId())
                            .build())
                    .toList();
            booking.addSeats(seats);
        }
        return booking;
    }

    public BookingResponseDTO toDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .userId(booking.getUserId())
                .bookingId(booking.getBookingId())
                .movieId(booking.getMovieId())
                .theatreId(booking.getTheatreId())
                .showtimeId(String.valueOf(booking.getShowtimeId()))
                .totalAmount(booking.getTotalAmount())
                .seatCount(booking.getSeatCount())
                .paymentId(booking.getPaymentId())
                .bookingStatus(booking.getBookingStatus())
                .createdAt(booking.getCreatedAt())
                .seatNumbers(booking.getSeats() == null ? null : booking.getSeats().stream()
                        .map(ReservedSeat::getSeatLabel)
                        .toList())
                .build();
    }
}

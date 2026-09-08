package com.cinetix.booking_service.service;

import com.cinetix.booking_service.dtos.BookingRequestDTO;
import com.cinetix.booking_service.dtos.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO);
    BookingResponseDTO getBookingById(Long bookingId);
    List<BookingResponseDTO> getBookingsByUser(Long userId);
    List<BookingResponseDTO> getAllBookings();
    BookingResponseDTO cancelBooking(Long userId, Long bookingId);
    List<String> getReservedSeats(Long showtimeId);
}

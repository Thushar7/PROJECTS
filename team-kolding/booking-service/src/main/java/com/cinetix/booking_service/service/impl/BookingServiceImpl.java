package com.cinetix.booking_service.service.impl;

import com.cinetix.booking_service.constants.BookingConstants;
import com.cinetix.booking_service.dtos.BookingRequestDTO;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.dtos.External.NotificationDTO;
import com.cinetix.booking_service.entity.Booking;
import com.cinetix.booking_service.entity.BookingStatus;
import com.cinetix.booking_service.exception.BookingNotFoundException;
import com.cinetix.booking_service.exception.SeatAlreadyBookedException;
import com.cinetix.booking_service.mapper.BookingMapper;
import com.cinetix.booking_service.repository.BookingRepository;
import com.cinetix.booking_service.repository.ReservedSeatRepository;
import com.cinetix.booking_service.service.BookingService;
import com.cinetix.booking_service.util.NotificationUtils;
import com.cinetix.booking_service.util.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ReservedSeatRepository reservedSeatRepository;

    private final RabbitMQProducer rabbitMQProducer;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        log.info("Creating booking with userId={}, movieId={}, showtimeId={}",
                bookingRequestDTO.getUserId(), bookingRequestDTO.getMovieId(), bookingRequestDTO.getShowtimeId());

        // Validate seat numbers
        if (bookingRequestDTO.getSeatNumbers() == null || bookingRequestDTO.getSeatNumbers().isEmpty()) {
            throw new IllegalArgumentException("seatNumbers must be provided");
        }
        if (bookingRequestDTO.getSeatCount() == null || bookingRequestDTO.getSeatCount() != bookingRequestDTO.getSeatNumbers().size()) {
            throw new IllegalArgumentException("seatCount must equal number of seatNumbers provided");
        }
        // Check duplicates in request
        Set<String> unique = new HashSet<>(bookingRequestDTO.getSeatNumbers());
        if (unique.size() != bookingRequestDTO.getSeatNumbers().size()) {
            throw new IllegalArgumentException("Duplicate seat labels in request");
        }
        // Check already booked seats
        List<String> alreadyBooked = reservedSeatRepository.findBookedSeatLabels(
                bookingRequestDTO.getShowtimeId(), bookingRequestDTO.getSeatNumbers());
        if (!alreadyBooked.isEmpty()) {
            throw new SeatAlreadyBookedException("Seats already booked: " + String.join(",", alreadyBooked));
        }

        Booking booking = bookingMapper.toEntity(bookingRequestDTO);
        // Ensure defaults if mapper does not set them
        if (booking.getBookingStatus() == null) {
            booking.setBookingStatus(BookingStatus.PENDING);
        }
        if (booking.getTotalAmount() == null) {
            booking.setTotalAmount(BigDecimal.ZERO);
        }
        if (booking.getPaymentId() == null) {
            booking.setPaymentId(0L);
        }

        try {
            Booking saved = bookingRepository.save(booking);
            log.info("Booking created id={} status={}", saved.getBookingId(), saved.getBookingStatus());

            BookingResponseDTO responseDTO = bookingMapper.toDTO(saved);

            //Publish booking confirmation notification
            rabbitMQProducer.publishBookingConfirmed(responseDTO);

            return responseDTO;

        } catch (DataIntegrityViolationException ex) {
            // Possible race condition on unique constraint (other transaction booked concurrently)
            log.warn("Integrity violation booking seats userId={} showtimeId={} seats={}", bookingRequestDTO.getUserId(), bookingRequestDTO.getShowtimeId(), bookingRequestDTO.getSeatNumbers(), ex);
            throw new SeatAlreadyBookedException("One or more seats just got booked. Please retry with different seats.");
        }
    }

    @Override
    public BookingResponseDTO getBookingById(Long bookingId) {
        log.debug("Fetching booking id={}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(BookingConstants.BOOKING_NOT_FOUND + bookingId));
        log.info("Booking fetched successfully: {}", bookingId);
        return bookingMapper.toDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getBookingsByUser(Long userId) {
        log.debug("Fetching bookings for userId={}", userId);
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toDTO)
                .toList();
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        log.debug("Fetching all bookings");
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long userId, Long bookingId) {
        log.info("Cancelling booking with booking id={} userId={}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(BookingConstants.BOOKING_NOT_FOUND + bookingId));

        if (!booking.getUserId().equals(userId)) {
            // Conceal existence if user mismatch
            throw new BookingNotFoundException(BookingConstants.BOOKING_NOT_FOUND + bookingId);
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            log.info("Booking id={} already cancelled", bookingId);
            return bookingMapper.toDTO(booking);
        }

        // Release seats: orphanRemoval=true ensures deletion of reserved_seats rows
        if (booking.getSeats() != null && !booking.getSeats().isEmpty()) {
            int seatSize = booking.getSeats().size(); // force load
            booking.getSeats().clear();
            log.info("Released {} seats for booking id={}", seatSize, bookingId);
        } else {
            log.info("No seats associated with booking id={} to release", bookingId);
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking cancelled = bookingRepository.save(booking);
        BookingResponseDTO responseDTO = bookingMapper.toDTO(cancelled);

        //Publish cancellation notification
        rabbitMQProducer.publishBookingCancelled(responseDTO);

        log.info("Booking id={} cancelled (seats released)", bookingId);
        return responseDTO;
    }

    @Override
    public List<String> getReservedSeats(Long showtimeId) {
        return reservedSeatRepository.findByShowtimeId(showtimeId).stream()
                .map(rs -> rs.getSeatLabel())
                .toList();
    }
}

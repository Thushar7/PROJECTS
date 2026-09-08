package com.cinetix.booking_service.controller;

import com.cinetix.booking_service.dtos.BookingRequestDTO;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking Controller", description = "APIs for managing bookings")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Create a new booking")
    @ApiResponse(responseCode = "201", description = "Booking created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @PostMapping("/create")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        log.info("Received request to create booking for userId={}, movieId={}, showtimeId={}",
                bookingRequestDTO.getUserId(), bookingRequestDTO.getMovieId(), bookingRequestDTO.getShowtimeId());
        BookingResponseDTO response = bookingService.createBooking(bookingRequestDTO);
        log.info("Booking created with id={}", response.getBookingId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get booking by ID")
    @ApiResponse(responseCode = "200", description = "Booking found")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long bookingId) {
        log.info("Received request to get booking with id={}", bookingId);
        BookingResponseDTO response = bookingService.getBookingById(bookingId);
        log.info("Fetched booking with id={}", bookingId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get bookings for a user")
    @ApiResponse(responseCode = "200", description = "List of bookings for the user")
    @GetMapping("/get-all/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUser(@PathVariable Long userId) {
        log.info("Received request to get bookings for userId={}", userId);
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUser(userId);
        log.info("Fetched {} bookings for userId={}", bookings.size(), userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get all bookings")
    @ApiResponse(responseCode = "200", description = "List of bookings")
    @GetMapping("/get-all")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        log.info("Received request to get all bookings");
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        log.info("Fetched {} bookings", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Cancel a booking")
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long bookingId,
                                                            @RequestParam Long userId) {
        log.info("Received request to cancel booking with bookingId={}, userId={}", bookingId, userId);
        BookingResponseDTO response = bookingService.cancelBooking(userId, bookingId);
        log.info("Cancelled booking with id={} for userId={}", bookingId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get reserved seats for a showtime")
    @GetMapping("/showtimes/{showtimeId}/reserved-seats")
    public ResponseEntity<List<String>> getReservedSeats(@PathVariable Long showtimeId) {
        log.info("Received request to get reserved seats for showtimeId={}", showtimeId);
        List<String> seats = bookingService.getReservedSeats(showtimeId);
        return ResponseEntity.ok(seats);
    }

}

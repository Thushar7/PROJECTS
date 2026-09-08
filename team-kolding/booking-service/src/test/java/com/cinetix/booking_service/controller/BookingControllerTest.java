package com.cinetix.booking_service.controller;

import com.cinetix.booking_service.config.TestConfig;
import com.cinetix.booking_service.dtos.BookingRequestDTO;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.entity.BookingStatus;
import com.cinetix.booking_service.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(TestConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBooking_success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO(null, 1L, 2L, 3L, 4L,
            BigDecimal.TEN, 5L, BookingStatus.PENDING, LocalDateTime.now(), 2L, List.of("A1", "A2"));

        BookingResponseDTO response = BookingResponseDTO.builder()
            .bookingId(100L).userId(1L).movieId(2L).theatreId(3L)
            .totalAmount(BigDecimal.TEN).bookingStatus(BookingStatus.PENDING)
            .seatNumbers(List.of("A1", "A2")).build();

        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(100))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.seatNumbers[0]").value("A1"));
    }

    @Test
    void getBookingById_found() throws Exception {
        BookingResponseDTO response = BookingResponseDTO.builder()
            .bookingId(10L).userId(1L).movieId(2L).theatreId(3L)
            .bookingStatus(BookingStatus.CONFIRMED).build();

        when(bookingService.getBookingById(10L)).thenReturn(response);

        mockMvc.perform(get("/bookings/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(10))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    void getBookingsByUser_success() throws Exception {
        List<BookingResponseDTO> bookings = List.of(
            BookingResponseDTO.builder().bookingId(1L).userId(10L).movieId(2L).build(),
            BookingResponseDTO.builder().bookingId(2L).userId(10L).movieId(3L).build()
        );

        when(bookingService.getBookingsByUser(10L)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/get-all/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].bookingId").value(1))
                .andExpect(jsonPath("$[1].bookingId").value(2));
    }

    @Test
    void getBookingsByUser_emptyList() throws Exception {
        when(bookingService.getBookingsByUser(999L)).thenReturn(List.of());

        mockMvc.perform(get("/bookings/get-all/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllBookings_success() throws Exception {
        List<BookingResponseDTO> bookings = List.of(
            BookingResponseDTO.builder().bookingId(1L).userId(10L).movieId(2L).build(),
            BookingResponseDTO.builder().bookingId(2L).userId(20L).movieId(3L).build(),
            BookingResponseDTO.builder().bookingId(3L).userId(30L).movieId(2L).build()
        );

        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/bookings/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].bookingId").value(1))
                .andExpect(jsonPath("$[1].userId").value(20))
                .andExpect(jsonPath("$[2].movieId").value(2));
    }

    @Test
    void cancelBooking_success() throws Exception {
        BookingResponseDTO cancelledBooking = BookingResponseDTO.builder()
            .bookingId(5L)
            .userId(10L)
            .bookingStatus(BookingStatus.CANCELLED)
            .build();

        when(bookingService.cancelBooking(10L, 5L)).thenReturn(cancelledBooking);

        mockMvc.perform(put("/bookings/5/cancel")
                .param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(5))
                .andExpect(jsonPath("$.bookingStatus").value("CANCELLED"));
    }


    @Test
    void getReservedSeats_success() throws Exception {
        List<String> reservedSeats = Arrays.asList("A1", "A2", "B3", "B4");

        when(bookingService.getReservedSeats(5L)).thenReturn(reservedSeats);

        mockMvc.perform(get("/bookings/showtimes/5/reserved-seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0]").value("A1"))
                .andExpect(jsonPath("$[1]").value("A2"))
                .andExpect(jsonPath("$[2]").value("B3"))
                .andExpect(jsonPath("$[3]").value("B4"));
    }

    @Test
    void getReservedSeats_noReservations() throws Exception {
        when(bookingService.getReservedSeats(999L)).thenReturn(List.of());

        mockMvc.perform(get("/bookings/showtimes/999/reserved-seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

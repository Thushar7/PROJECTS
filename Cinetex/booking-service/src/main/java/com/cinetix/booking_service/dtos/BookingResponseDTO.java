package com.cinetix.booking_service.dtos;

import com.cinetix.booking_service.entity.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDTO {

    private Long bookingId;
    private Long userId;
    private Long movieId;
    private Long theatreId;
    private String showtimeId;
    private Long seatCount;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingTime;
    private Long paymentId;
    private LocalDateTime createdAt;
    // New: list of seat labels reserved in this booking
    private List<String> seatNumbers;
}

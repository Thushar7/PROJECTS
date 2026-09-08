package com.cinetix.booking_service.dtos;

import com.cinetix.booking_service.entity.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    // BookingId should normally be null on create; remove @NotNull to allow creation without specifying id.
    private Long bookingId;

    @NotNull
    private Long userId;

    @NotNull
    private Long movieId;

    @NotNull
    private Long theatreId;

    @NotNull
    private Long showtimeId;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private Long paymentId;

    @NotNull
    private BookingStatus bookingStatus;

    private LocalDateTime createdAt;

    @NotNull
    @Min(value = 1, message = "Seat count must be at least 1")
    private Long seatCount;

    // New: Explicit seat labels chosen by the user (e.g., A1, A2). Must match seatCount.
    @NotEmpty(message = "At least one seat must be selected")
    private List<@NotBlank String> seatNumbers;
}

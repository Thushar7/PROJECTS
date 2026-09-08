package com.cinetix.booking_service.dtos.External;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequestDTO {

    private Long userId;
    private Long bookingId;
    private Double amount;
    private String paymentMethod; // e.g., "CREDIT_CARD", "PAYPAL"
}

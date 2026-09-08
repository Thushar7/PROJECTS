package com.cinetix.booking_service.dtos.External;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentResponseDTOTest {

    @Test
    void paymentResponseDTO_builderPattern() {
        PaymentResponseDTO dto = PaymentResponseDTO.builder()
                .userId(1L)
                .status(true)
                .paymentId(500L)
                .message("Payment successful")
                .build();

        assertEquals(1L, dto.getUserId());
        assertTrue(dto.isStatus());
        assertEquals(500L, dto.getPaymentId());
        assertEquals("Payment successful", dto.getMessage());
    }

    @Test
    void paymentResponseDTO_settersAndGetters() {
        PaymentResponseDTO dto = new PaymentResponseDTO();

        dto.setUserId(10L);
        dto.setStatus(false);
        dto.setPaymentId(600L);
        dto.setMessage("Payment failed");

        assertEquals(10L, dto.getUserId());
        assertFalse(dto.isStatus());
        assertEquals(600L, dto.getPaymentId());
        assertEquals("Payment failed", dto.getMessage());
    }

    @Test
    void paymentResponseDTO_equalsAndHashCode() {
        PaymentResponseDTO dto1 = new PaymentResponseDTO();
        dto1.setUserId(1L);
        dto1.setPaymentId(500L);

        PaymentResponseDTO dto2 = new PaymentResponseDTO();
        dto2.setUserId(1L);
        dto2.setPaymentId(500L);

        PaymentResponseDTO dto3 = new PaymentResponseDTO();
        dto3.setUserId(2L);
        dto3.setPaymentId(500L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void paymentResponseDTO_toString() {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setUserId(1L);
        dto.setStatus(true);

        String toString = dto.toString();
        assertTrue(toString.contains("userId"));
        assertTrue(toString.contains("status"));
    }

    @Test
    void paymentResponseDTO_noArgsConstructor() {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertFalse(dto.isStatus()); // boolean defaults to false
        assertNull(dto.getPaymentId());
        assertNull(dto.getMessage());
    }

    @Test
    void paymentResponseDTO_allArgsConstructor() {
        PaymentResponseDTO dto = new PaymentResponseDTO(1L, true, 500L, "Success");

        assertEquals(1L, dto.getUserId());
        assertTrue(dto.isStatus());
        assertEquals(500L, dto.getPaymentId());
        assertEquals("Success", dto.getMessage());
    }
}

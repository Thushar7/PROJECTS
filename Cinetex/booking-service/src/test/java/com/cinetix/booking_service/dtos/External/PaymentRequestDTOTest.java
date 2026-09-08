package com.cinetix.booking_service.dtos.External;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestDTOTest {

    @Test
    void paymentRequestDTO_builderPattern() {
        PaymentRequestDTO dto = PaymentRequestDTO.builder()
                .userId(1L)
                .bookingId(100L)
                .amount(150.0)
                .paymentMethod("CREDIT_CARD")
                .build();

        assertEquals(1L, dto.getUserId());
        assertEquals(100L, dto.getBookingId());
        assertEquals(150.0, dto.getAmount());
        assertEquals("CREDIT_CARD", dto.getPaymentMethod());
    }

    @Test
    void paymentRequestDTO_settersAndGetters() {
        PaymentRequestDTO dto = new PaymentRequestDTO();

        dto.setUserId(5L);
        dto.setBookingId(200L);
        dto.setAmount(99.99);
        dto.setPaymentMethod("PAYPAL");

        assertEquals(5L, dto.getUserId());
        assertEquals(200L, dto.getBookingId());
        assertEquals(99.99, dto.getAmount());
        assertEquals("PAYPAL", dto.getPaymentMethod());
    }

    @Test
    void paymentRequestDTO_equalsAndHashCode() {
        PaymentRequestDTO dto1 = new PaymentRequestDTO();
        dto1.setUserId(1L);
        dto1.setBookingId(100L);

        PaymentRequestDTO dto2 = new PaymentRequestDTO();
        dto2.setUserId(1L);
        dto2.setBookingId(100L);

        PaymentRequestDTO dto3 = new PaymentRequestDTO();
        dto3.setUserId(2L);
        dto3.setBookingId(100L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void paymentRequestDTO_toString() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUserId(1L);
        dto.setAmount(150.0);

        String toString = dto.toString();
        assertTrue(toString.contains("userId"));
        assertTrue(toString.contains("amount"));
    }

    @Test
    void paymentRequestDTO_noArgsConstructor() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getBookingId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void paymentRequestDTO_allArgsConstructor() {
        PaymentRequestDTO dto = new PaymentRequestDTO(1L, 100L, 150.0, "CREDIT_CARD");

        assertEquals(1L, dto.getUserId());
        assertEquals(100L, dto.getBookingId());
        assertEquals(150.0, dto.getAmount());
        assertEquals("CREDIT_CARD", dto.getPaymentMethod());
    }
}

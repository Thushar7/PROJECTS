package com.cinetix.booking_service.util;

import com.cinetix.booking_service.constants.BookingConstants;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.dtos.External.NotificationDTO;
import com.cinetix.booking_service.util.NotificationUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationUtilsTest {

    private BookingResponseDTO buildBooking(Long userId, Long bookingId, Long paymentId) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setUserId(userId);
        dto.setBookingId(bookingId);
        dto.setPaymentId(paymentId);
        return dto;
    }

    @Test
    void bookingConfirmed_buildsExpectedNotification() {
        BookingResponseDTO booking = buildBooking(1L, 11L, 111L);
        LocalDateTime before = LocalDateTime.now();
        NotificationDTO dto = NotificationUtils.bookingConfirmed(booking);
        LocalDateTime after = LocalDateTime.now();

        assertEquals(1L, dto.getUserId());
        assertEquals(11L, dto.getBookingId());
        assertEquals(111L, dto.getPaymentId());
        assertEquals(BookingConstants.NotificationEvent.BOOKING_CONFIRMED, dto.getEvent());
        assertEquals(BookingConstants.NotificationType.USER, dto.getType());
        assertEquals(BookingConstants.NotificationEvent.BOOKING_CONFIRMED.getDefaultMessage(), dto.getMessage());
        assertEquals(BookingConstants.STATUS_NEW, dto.getStatus());
        assertNotNull(dto.getNotifiedAt());
        assertFalse(dto.getNotifiedAt().isBefore(before));
        assertFalse(dto.getNotifiedAt().isAfter(after.plusSeconds(1)));
    }

    @Test
    void bookingCancelled_buildsExpectedNotification() {
        BookingResponseDTO booking = buildBooking(2L, 22L, 222L);
        NotificationDTO dto = NotificationUtils.bookingCancelled(booking);

        assertEquals(BookingConstants.NotificationEvent.BOOKING_CANCELLED, dto.getEvent());
        assertEquals(BookingConstants.NotificationType.USER, dto.getType());
        assertEquals(BookingConstants.NotificationEvent.BOOKING_CANCELLED.getDefaultMessage(), dto.getMessage());
    }

    @Test
    void paymentSuccess_buildsExpectedNotification() {
        BookingResponseDTO booking = buildBooking(3L, 33L, 333L);
        NotificationDTO dto = NotificationUtils.paymentSuccess(booking);

        assertEquals(BookingConstants.NotificationEvent.PAYMENT_SUCCESS, dto.getEvent());
        assertEquals(BookingConstants.NotificationType.USER, dto.getType());
        assertEquals(BookingConstants.NotificationEvent.PAYMENT_SUCCESS.getDefaultMessage(), dto.getMessage());
    }

    @Test
    void nullBooking_throws() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> NotificationUtils.bookingConfirmed(null));
        assertEquals("booking must not be null", ex.getMessage());
    }

    @Test
    void nullUserId_throws() {
        BookingResponseDTO booking = buildBooking(null, 123L, 456L);
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> NotificationUtils.bookingCancelled(booking));
        assertEquals("booking.userId must not be null", ex.getMessage());
    }

    @Test
    void nullBookingId_throws() {
        BookingResponseDTO booking = buildBooking(789L, null, 456L);
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> NotificationUtils.paymentSuccess(booking));
        assertEquals("booking.bookingId must not be null", ex.getMessage());
    }

    @Test
    void privateConstructor_forCoverage() throws Exception {
        Constructor<NotificationUtils> ctor = NotificationUtils.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertNotNull(ctor.newInstance());
    }
}

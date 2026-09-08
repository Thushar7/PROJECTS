package com.cinetix.booking_service.service;

import com.cinetix.booking_service.dtos.BookingRequestDTO;
import com.cinetix.booking_service.dtos.BookingResponseDTO;
import com.cinetix.booking_service.entity.Booking;
import com.cinetix.booking_service.entity.BookingStatus;
import com.cinetix.booking_service.entity.ReservedSeat;
import com.cinetix.booking_service.mapper.BookingMapper;
import com.cinetix.booking_service.repository.BookingRepository;
import com.cinetix.booking_service.repository.ReservedSeatRepository;
import com.cinetix.booking_service.service.impl.BookingServiceImpl;
import com.cinetix.booking_service.util.RabbitMQProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ReservedSeatRepository reservedSeatRepository;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_Success() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setUserId(1L);
        request.setMovieId(2L);
        request.setShowtimeId(3L);
        request.setSeatCount(2L);
        request.setSeatNumbers(Arrays.asList("A1", "A2"));

        Booking booking = new Booking();
        booking.setBookingId(100L);
        booking.setUserId(1L);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setTotalAmount(BigDecimal.ZERO);
        booking.setPaymentId(0L);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(100L);
        responseDTO.setUserId(1L);

        when(reservedSeatRepository.findBookedSeatLabels(anyLong(), anyList())).thenReturn(Collections.emptyList());
        when(bookingMapper.toEntity(any())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDTO(any())).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(100L, result.getBookingId());
        verify(rabbitMQProducer, times(1)).publishBookingConfirmed(any());
    }

    @Test
    void testCancelBooking_Success() {
        Booking booking = new Booking();
        booking.setBookingId(101L);
        booking.setUserId(1L);
        booking.setBookingStatus(BookingStatus.PENDING);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(101L);
        responseDTO.setUserId(1L);

        when(bookingRepository.findById(101L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDTO(any())).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.cancelBooking(1L, 101L);

        assertNotNull(result);
        assertEquals(101L, result.getBookingId());
        verify(rabbitMQProducer, times(1)).publishBookingCancelled(any());
    }

    @Test
    void testGetBookingById_Success() {
        Booking booking = new Booking();
        booking.setBookingId(102L);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(102L);

        when(bookingRepository.findById(102L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(any())).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.getBookingById(102L);

        assertNotNull(result);
        assertEquals(102L, result.getBookingId());
    }

    @Test
    void testGetAllBookings() {
        Booking booking = new Booking();
        booking.setBookingId(103L);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(103L);

        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toDTO(any())).thenReturn(responseDTO);

        List<BookingResponseDTO> results = bookingService.getAllBookings();

        assertEquals(1, results.size());
        assertEquals(103L, results.get(0).getBookingId());
    }

    @Test
    void testGetBookingsByUser() {
        Booking booking = new Booking();
        booking.setBookingId(104L);
        booking.setUserId(1L);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(104L);
        responseDTO.setUserId(1L);

        when(bookingRepository.findByUserId(1L)).thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toDTO(any())).thenReturn(responseDTO);

        List<BookingResponseDTO> results = bookingService.getBookingsByUser(1L);

        assertEquals(1, results.size());
        assertEquals(104L, results.get(0).getBookingId());
    }

    @Test
    void testGetReservedSeats() {
        ReservedSeat seat1 = new ReservedSeat();
        seat1.setSeatLabel("A1");
        ReservedSeat seat2 = new ReservedSeat();
        seat2.setSeatLabel("A2");
        when(reservedSeatRepository.findByShowtimeId(10L))
                .thenReturn(Arrays.asList(seat1, seat2));

        List<String> seats = bookingService.getReservedSeats(10L);

        assertEquals(2, seats.size());
        assertTrue(seats.contains("A1"));
        assertTrue(seats.contains("A2"));
    }
}
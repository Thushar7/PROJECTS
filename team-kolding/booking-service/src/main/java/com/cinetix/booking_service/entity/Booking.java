package com.cinetix.booking_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "bookings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "theatre_id", nullable = false)
    private Long theatreId;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "total_amt", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "seat_count", nullable = false)
    private Long seatCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", length = 32, nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // New: reserved seats associated with this booking
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReservedSeat> seats = new ArrayList<>();

    public void addSeat(ReservedSeat seat) {
        seats.add(seat);
        seat.setBooking(this);
    }

    public void addSeats(List<ReservedSeat> seatList) {
        seatList.forEach(this::addSeat);
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    } // e.g., BOOKED, CANCELLED
}

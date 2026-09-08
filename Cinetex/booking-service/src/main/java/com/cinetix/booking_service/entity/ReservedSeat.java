package com.cinetix.booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a single seat reserved as part of a booking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reserved_seats", uniqueConstraints = {
        @UniqueConstraint(name = "uk_showtime_seat", columnNames = {"showtime_id", "seat_label"})
})
public class ReservedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserved_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservedseat_booking"))
    private Booking booking;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "seat_label", length = 16, nullable = false)
    private String seatLabel;

    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt;

    @PrePersist
    void onCreate() {
        if (reservedAt == null) {
            reservedAt = LocalDateTime.now();
        }
    }
}

-- Flyway initial schema for booking-service
-- Version: 1

CREATE TABLE IF NOT EXISTS bookings (
    booking_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT      NOT NULL,
    movie_id     BIGINT      NOT NULL,
    theatre_id   BIGINT      NOT NULL,
    showtime_id  BIGINT      NOT NULL,
    total_amt    DECIMAL(10,2) NOT NULL,
    payment_id   BIGINT      NOT NULL,
    seat_count   BIGINT      NOT NULL,
    booking_status VARCHAR(32) NOT NULL,
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_booking_status CHECK (booking_status IN ('PENDING','CONFIRMED','CANCELLED','FAILED'))
) ENGINE=InnoDB;

CREATE INDEX idx_booking_user_id ON bookings(user_id);
CREATE INDEX idx_booking_showtime_id ON bookings(showtime_id);
CREATE INDEX idx_booking_status ON bookings(booking_status);

CREATE TABLE IF NOT EXISTS reserved_seats (
    reserved_seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id  BIGINT      NOT NULL,
    showtime_id BIGINT      NOT NULL,
    seat_label  VARCHAR(16) NOT NULL,
    reserved_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservedseat_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    CONSTRAINT uk_showtime_seat UNIQUE (showtime_id, seat_label)
) ENGINE=InnoDB;

CREATE INDEX idx_reservedseat_booking ON reserved_seats(booking_id);


-- Flyway initial schema for notification-service
-- Version: 1
-- NOTE: Updated to align with current enum values:
--  NotificationEvent: BOOKING_CONFIRMED, BOOKING_CANCELLED, PAYMENT_SUCCESS, MOVIE_ADDED
--  NotificationType: USER
--  NotificationStatus: NEW, RECEIVED, PROCESSED, FAILED

CREATE TABLE IF NOT EXISTS notifications (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    booking_id   BIGINT       NOT NULL,
    payment_id   BIGINT       NULL,
    event        VARCHAR(40)  NOT NULL,
    type         VARCHAR(20)  NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    message      VARCHAR(500) NOT NULL,
    notified_at  DATETIME     NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME     NULL,
    CONSTRAINT chk_notification_event CHECK (event IN ('BOOKING_CONFIRMED','BOOKING_CANCELLED','PAYMENT_SUCCESS','MOVIE_ADDED')),
    CONSTRAINT chk_notification_type CHECK (type IN ('USER')),
    CONSTRAINT chk_notification_status CHECK (status IN ('NEW','RECEIVED','PROCESSED','FAILED'))
) ENGINE=InnoDB;

CREATE INDEX idx_notification_user_id     ON notifications(user_id);
CREATE INDEX idx_notification_booking_id  ON notifications(booking_id);
CREATE INDEX idx_notification_event       ON notifications(event);
CREATE INDEX idx_notification_status      ON notifications(status);

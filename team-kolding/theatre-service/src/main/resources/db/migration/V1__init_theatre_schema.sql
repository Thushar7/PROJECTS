-- Flyway initial schema for theatre-service
-- Version: 1

CREATE TABLE IF NOT EXISTS theatres (
    theatre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    city       VARCHAR(100) NOT NULL,
    state      VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS showtimes (
    showtime_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_date   DATE NOT NULL,
    start_time  DATETIME NOT NULL,
    end_time    DATETIME NOT NULL,
    movie_id    BIGINT NOT NULL,
    theatre_id  BIGINT NOT NULL,
    CONSTRAINT fk_showtime_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(theatre_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Indexes (as per entity annotations)
CREATE INDEX idx_showtime_movie_id   ON showtimes(movie_id);
CREATE INDEX idx_showtime_theatre_id ON showtimes(theatre_id);
CREATE INDEX idx_showtime_show_date  ON showtimes(show_date);


-- Flyway initial schema for user-service
-- Version: 1

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL,
    email    VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(30)  NOT NULL DEFAULT 'USER',
    genre_preference     VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    language_preference  VARCHAR(30) NOT NULL DEFAULT 'English',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

-- Optional seed admin user (commented out for security best practices)
-- INSERT INTO users (username, email, password, role, genre_preference, language_preference)
-- VALUES ('admin', 'admin@example.com', '{bcrypt}$2a$10$replace_me_with_real_hash', 'ADMIN', 'GENERAL', 'English');


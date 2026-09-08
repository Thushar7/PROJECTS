-- Flyway initial schema for movie-service
-- Version: 1

-- Table: languages
CREATE TABLE IF NOT EXISTS languages (
    language_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- Table: genres
CREATE TABLE IF NOT EXISTS genres (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- Table: movies
CREATE TABLE IF NOT EXISTS movies (
    movie_id            INT AUTO_INCREMENT PRIMARY KEY,
    title               VARCHAR(255) NOT NULL,
    duration            INT,
    release_date        DATE,
    description         TEXT,
    poster_url          LONGBLOB,
    poster_content_type VARCHAR(255)
) ENGINE=InnoDB;

-- Table: movie_genres (join table with its own surrogate key as in entity)
CREATE TABLE IF NOT EXISTS movie_genres (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    genre_id INT NOT NULL,
    CONSTRAINT fk_movie_genres_movie FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_genres_genre FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE,
    CONSTRAINT uq_movie_genres UNIQUE (movie_id, genre_id)
) ENGINE=InnoDB;

CREATE INDEX idx_movie_genres_movie ON movie_genres(movie_id);
CREATE INDEX idx_movie_genres_genre ON movie_genres(genre_id);

-- Table: movie_languages (join table with its own surrogate key as in entity)
CREATE TABLE IF NOT EXISTS movie_languages (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    movie_id    INT NOT NULL,
    language_id INT NOT NULL,
    CONSTRAINT fk_movie_languages_movie FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_languages_language FOREIGN KEY (language_id) REFERENCES languages(language_id) ON DELETE CASCADE,
    CONSTRAINT uq_movie_languages UNIQUE (movie_id, language_id)
) ENGINE=InnoDB;

CREATE INDEX idx_movie_languages_movie ON movie_languages(movie_id);
CREATE INDEX idx_movie_languages_language ON movie_languages(language_id);

-- Seed reference data (executed only once on fresh schema)
INSERT INTO genres (name) VALUES ('Action');
INSERT INTO genres (name) VALUES ('Drama');
INSERT INTO languages (name) VALUES ('English');
INSERT INTO languages (name) VALUES ('Hindi');

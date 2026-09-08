package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Genre;
import nexi.cinetix.movie_service.entity.Movie;
import nexi.cinetix.movie_service.entity.MovieGenre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MovieGenreRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @Test
    void movieGenreRepository_findByMovieMovieId() {
        // Given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test description")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Genre genre1 = Genre.builder().name("Action").build();
        Genre genre2 = Genre.builder().name("Drama").build();
        Genre savedGenre1 = entityManager.persistAndFlush(genre1);
        Genre savedGenre2 = entityManager.persistAndFlush(genre2);

        MovieGenre movieGenre1 = MovieGenre.builder()
                .movie(savedMovie)
                .genre(savedGenre1)
                .build();
        MovieGenre movieGenre2 = MovieGenre.builder()
                .movie(savedMovie)
                .genre(savedGenre2)
                .build();

        entityManager.persistAndFlush(movieGenre1);
        entityManager.persistAndFlush(movieGenre2);

        // When
        List<MovieGenre> result = movieGenreRepository.findByMovie_MovieId(savedMovie.getMovieId());

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(mg -> mg.getGenre().getName().equals("Action")));
        assertTrue(result.stream().anyMatch(mg -> mg.getGenre().getName().equals("Drama")));
    }

    @Test
    void movieGenreRepository_findByGenreGenreId() {
        // Given
        Genre genre = Genre.builder().name("Comedy").build();
        Genre savedGenre = entityManager.persistAndFlush(genre);

        Movie movie1 = Movie.builder()
                .title("Comedy Movie 1")
                .duration(90)
                .releaseDate(LocalDate.now())
                .description("First comedy")
                .build();
        Movie movie2 = Movie.builder()
                .title("Comedy Movie 2")
                .duration(100)
                .releaseDate(LocalDate.now())
                .description("Second comedy")
                .build();
        Movie savedMovie1 = entityManager.persistAndFlush(movie1);
        Movie savedMovie2 = entityManager.persistAndFlush(movie2);

        MovieGenre movieGenre1 = MovieGenre.builder()
                .movie(savedMovie1)
                .genre(savedGenre)
                .build();
        MovieGenre movieGenre2 = MovieGenre.builder()
                .movie(savedMovie2)
                .genre(savedGenre)
                .build();

        entityManager.persistAndFlush(movieGenre1);
        entityManager.persistAndFlush(movieGenre2);

        // When
        List<MovieGenre> result = movieGenreRepository.findByGenre_GenreId(savedGenre.getGenreId());

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(mg -> mg.getMovie().getTitle().equals("Comedy Movie 1")));
        assertTrue(result.stream().anyMatch(mg -> mg.getMovie().getTitle().equals("Comedy Movie 2")));
    }

    @Test
    void movieGenreRepository_existsByMovieMovieIdAndGenreGenreId() {
        // Given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test description")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Genre genre = Genre.builder().name("Thriller").build();
        Genre savedGenre = entityManager.persistAndFlush(genre);

        MovieGenre movieGenre = MovieGenre.builder()
                .movie(savedMovie)
                .genre(savedGenre)
                .build();
        entityManager.persistAndFlush(movieGenre);

        // When & Then
        assertTrue(movieGenreRepository.existsByMovie_MovieIdAndGenre_GenreId(
                savedMovie.getMovieId(), savedGenre.getGenreId()));
        assertFalse(movieGenreRepository.existsByMovie_MovieIdAndGenre_GenreId(
                savedMovie.getMovieId(), 999));
        assertFalse(movieGenreRepository.existsByMovie_MovieIdAndGenre_GenreId(
                999, savedGenre.getGenreId()));
    }

    @Test
    void movieGenreRepository_deleteByMovieMovieIdAndGenreGenreId() {
        // Given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test description")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Genre genre = Genre.builder().name("Horror").build();
        Genre savedGenre = entityManager.persistAndFlush(genre);

        MovieGenre movieGenre = MovieGenre.builder()
                .movie(savedMovie)
                .genre(savedGenre)
                .build();
        entityManager.persistAndFlush(movieGenre);

        // Verify it exists
        assertTrue(movieGenreRepository.existsByMovie_MovieIdAndGenre_GenreId(
                savedMovie.getMovieId(), savedGenre.getGenreId()));

        // When
        movieGenreRepository.deleteByMovie_MovieIdAndGenre_GenreId(
                savedMovie.getMovieId(), savedGenre.getGenreId());
        entityManager.flush();

        // Then
        assertFalse(movieGenreRepository.existsByMovie_MovieIdAndGenre_GenreId(
                savedMovie.getMovieId(), savedGenre.getGenreId()));
    }

    @Test
    void movieGenreRepository_save() {
        // Given
        Movie movie = Movie.builder()
                .title("New Movie")
                .duration(130)
                .releaseDate(LocalDate.now())
                .description("New movie description")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Genre genre = Genre.builder().name("Fantasy").build();
        Genre savedGenre = entityManager.persistAndFlush(genre);

        MovieGenre movieGenre = MovieGenre.builder()
                .movie(savedMovie)
                .genre(savedGenre)
                .build();

        // When
        MovieGenre savedMovieGenre = movieGenreRepository.save(movieGenre);

        // Then
        assertNotNull(savedMovieGenre.getId());
        assertEquals(savedMovie.getMovieId(), savedMovieGenre.getMovie().getMovieId());
        assertEquals(savedGenre.getGenreId(), savedMovieGenre.getGenre().getGenreId());
    }
}

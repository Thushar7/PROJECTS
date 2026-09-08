package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Language;
import nexi.cinetix.movie_service.entity.Movie;
import nexi.cinetix.movie_service.entity.MovieLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MovieLanguageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieLanguageRepository movieLanguageRepository;

    @Test
    void movieLanguageRepository_findByMovieMovieId() {
        // Given
        Movie movie = Movie.builder()
                .title("Multilingual Movie")
                .duration(140)
                .releaseDate(LocalDate.now())
                .description("Movie with multiple languages")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Language language1 = Language.builder().name("English").build();
        Language language2 = Language.builder().name("Spanish").build();
        Language savedLanguage1 = entityManager.persistAndFlush(language1);
        Language savedLanguage2 = entityManager.persistAndFlush(language2);

        MovieLanguage movieLanguage1 = MovieLanguage.builder()
                .movie(savedMovie)
                .language(savedLanguage1)
                .build();
        MovieLanguage movieLanguage2 = MovieLanguage.builder()
                .movie(savedMovie)
                .language(savedLanguage2)
                .build();

        entityManager.persistAndFlush(movieLanguage1);
        entityManager.persistAndFlush(movieLanguage2);

        // When
        List<MovieLanguage> result = movieLanguageRepository.findByMovie_MovieId(savedMovie.getMovieId());

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(ml -> ml.getLanguage().getName().equals("English")));
        assertTrue(result.stream().anyMatch(ml -> ml.getLanguage().getName().equals("Spanish")));
    }

    @Test
    void movieLanguageRepository_findByLanguageLanguageId() {
        // Given
        Language language = Language.builder().name("French").build();
        Language savedLanguage = entityManager.persistAndFlush(language);

        Movie movie1 = Movie.builder()
                .title("French Movie 1")
                .duration(110)
                .releaseDate(LocalDate.now())
                .description("First French movie")
                .build();
        Movie movie2 = Movie.builder()
                .title("French Movie 2")
                .duration(95)
                .releaseDate(LocalDate.now())
                .description("Second French movie")
                .build();
        Movie savedMovie1 = entityManager.persistAndFlush(movie1);
        Movie savedMovie2 = entityManager.persistAndFlush(movie2);

        MovieLanguage movieLanguage1 = MovieLanguage.builder()
                .movie(savedMovie1)
                .language(savedLanguage)
                .build();
        MovieLanguage movieLanguage2 = MovieLanguage.builder()
                .movie(savedMovie2)
                .language(savedLanguage)
                .build();

        entityManager.persistAndFlush(movieLanguage1);
        entityManager.persistAndFlush(movieLanguage2);

        // When
        List<MovieLanguage> result = movieLanguageRepository.findByLanguage_LanguageId(savedLanguage.getLanguageId());

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(ml -> ml.getMovie().getTitle().equals("French Movie 1")));
        assertTrue(result.stream().anyMatch(ml -> ml.getMovie().getTitle().equals("French Movie 2")));
    }

    @Test
    void movieLanguageRepository_existsByMovieMovieIdAndLanguageLanguageId() {
        // Given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test description")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Language language = Language.builder().name("German").build();
        Language savedLanguage = entityManager.persistAndFlush(language);

        MovieLanguage movieLanguage = MovieLanguage.builder()
                .movie(savedMovie)
                .language(savedLanguage)
                .build();
        entityManager.persistAndFlush(movieLanguage);

        // When & Then
        assertTrue(movieLanguageRepository.existsByMovie_MovieIdAndLanguage_LanguageId(
                savedMovie.getMovieId(), savedLanguage.getLanguageId()));
        assertFalse(movieLanguageRepository.existsByMovie_MovieIdAndLanguage_LanguageId(
                savedMovie.getMovieId(), 999));
        assertFalse(movieLanguageRepository.existsByMovie_MovieIdAndLanguage_LanguageId(
                999, savedLanguage.getLanguageId()));
    }

    @Test
    void movieLanguageRepository_deleteByMovieMovieIdAndLanguageLanguageId() {
        // Given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test description")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Language language = Language.builder().name("Italian").build();
        Language savedLanguage = entityManager.persistAndFlush(language);

        MovieLanguage movieLanguage = MovieLanguage.builder()
                .movie(savedMovie)
                .language(savedLanguage)
                .build();
        entityManager.persistAndFlush(movieLanguage);

        // Verify it exists
        assertTrue(movieLanguageRepository.existsByMovie_MovieIdAndLanguage_LanguageId(
                savedMovie.getMovieId(), savedLanguage.getLanguageId()));

        // When
        movieLanguageRepository.deleteByMovie_MovieIdAndLanguage_LanguageId(
                savedMovie.getMovieId(), savedLanguage.getLanguageId());
        entityManager.flush();

        // Then
        assertFalse(movieLanguageRepository.existsByMovie_MovieIdAndLanguage_LanguageId(
                savedMovie.getMovieId(), savedLanguage.getLanguageId()));
    }

    @Test
    void movieLanguageRepository_save() {
        // Given
        Movie movie = Movie.builder()
                .title("New Movie")
                .duration(105)
                .releaseDate(LocalDate.now())
                .description("New movie description")
                .build();
        Movie savedMovie = entityManager.persistAndFlush(movie);

        Language language = Language.builder().name("Portuguese").build();
        Language savedLanguage = entityManager.persistAndFlush(language);

        MovieLanguage movieLanguage = MovieLanguage.builder()
                .movie(savedMovie)
                .language(savedLanguage)
                .build();

        // When
        MovieLanguage savedMovieLanguage = movieLanguageRepository.save(movieLanguage);

        // Then
        assertNotNull(savedMovieLanguage.getId());
        assertEquals(savedMovie.getMovieId(), savedMovieLanguage.getMovie().getMovieId());
        assertEquals(savedLanguage.getLanguageId(), savedMovieLanguage.getLanguage().getLanguageId());
    }
}

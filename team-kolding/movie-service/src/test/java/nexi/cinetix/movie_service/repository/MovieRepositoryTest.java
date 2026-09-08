package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void movieRepository_findByTitleContainingIgnoreCase() {
        // Given
        Movie movie1 = Movie.builder()
                .title("The Dark Knight")
                .duration(152)
                .releaseDate(LocalDate.of(2008, 7, 18))
                .description("Batman fights crime")
                .build();

        Movie movie2 = Movie.builder()
                .title("Dark Phoenix")
                .duration(113)
                .releaseDate(LocalDate.of(2019, 6, 7))
                .description("X-Men movie")
                .build();

        Movie movie3 = Movie.builder()
                .title("The Light")
                .duration(90)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .description("Light story")
                .build();

        entityManager.persistAndFlush(movie1);
        entityManager.persistAndFlush(movie2);
        entityManager.persistAndFlush(movie3);

        // When
        List<Movie> result = movieRepository.findByTitleContainingIgnoreCase("dark");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(m -> m.getTitle().equals("The Dark Knight")));
        assertTrue(result.stream().anyMatch(m -> m.getTitle().equals("Dark Phoenix")));
    }

    @Test
    void movieRepository_findByTitleContainingIgnoreCaseWithDifferentCase() {
        // Given
        Movie movie = Movie.builder()
                .title("UPPERCASE MOVIE")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test movie")
                .build();

        entityManager.persistAndFlush(movie);

        // When
        List<Movie> result = movieRepository.findByTitleContainingIgnoreCase("uppercase");

        // Then
        assertEquals(1, result.size());
        assertEquals("UPPERCASE MOVIE", result.get(0).getTitle());
    }

    @Test
    void movieRepository_findByTitleContainingIgnoreCaseNoMatch() {
        // Given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test movie")
                .build();

        entityManager.persistAndFlush(movie);

        // When
        List<Movie> result = movieRepository.findByTitleContainingIgnoreCase("nonexistent");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void movieRepository_findAll() {
        // Given
        Movie movie1 = Movie.builder()
                .title("Movie 1")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("First movie")
                .build();

        Movie movie2 = Movie.builder()
                .title("Movie 2")
                .duration(90)
                .releaseDate(LocalDate.now())
                .description("Second movie")
                .build();

        entityManager.persistAndFlush(movie1);
        entityManager.persistAndFlush(movie2);

        // When
        List<Movie> result = movieRepository.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void movieRepository_findById() {
        // Given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Test movie")
                .build();

        Movie savedMovie = entityManager.persistAndFlush(movie);

        // When
        Optional<Movie> result = movieRepository.findById(savedMovie.getMovieId());

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Movie", result.get().getTitle());
    }

    @Test
    void movieRepository_save() {
        // Given
        Movie movie = Movie.builder()
                .title("New Movie")
                .duration(100)
                .releaseDate(LocalDate.of(2024, 1, 1))
                .description("New movie description")
                .build();

        // When
        Movie savedMovie = movieRepository.save(movie);

        // Then
        assertNotNull(savedMovie.getMovieId());
        assertEquals("New Movie", savedMovie.getTitle());
        assertEquals(100, savedMovie.getDuration());
    }

    @Test
    void movieRepository_deleteById() {
        // Given
        Movie movie = Movie.builder()
                .title("To Delete")
                .duration(120)
                .releaseDate(LocalDate.now())
                .description("Will be deleted")
                .build();

        Movie savedMovie = entityManager.persistAndFlush(movie);
        Integer movieId = savedMovie.getMovieId();

        // When
        movieRepository.deleteById(movieId);

        // Then
        Optional<Movie> result = movieRepository.findById(movieId);
        assertFalse(result.isPresent());
    }

    @Test
    void movieRepository_findAllWithPagination() {
        // Given
        for (int i = 1; i <= 5; i++) {
            Movie movie = Movie.builder()
                    .title("Movie " + i)
                    .duration(120)
                    .releaseDate(LocalDate.now())
                    .description("Movie " + i + " description")
                    .build();
            entityManager.persistAndFlush(movie);
        }

        // When
        Pageable pageable = PageRequest.of(0, 3);
        Page<Movie> result = movieRepository.findAll(pageable);

        // Then
        assertEquals(3, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }
}

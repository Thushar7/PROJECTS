package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void genreRepository_findByNameIgnoreCase() {
        // Given
        Genre genre = Genre.builder()
                .name("Action")
                .build();

        entityManager.persistAndFlush(genre);

        // When
        Optional<Genre> result = genreRepository.findByNameIgnoreCase("action");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Action", result.get().getName());
    }

    @Test
    void genreRepository_findByNameIgnoreCaseUpperCase() {
        // Given
        Genre genre = Genre.builder()
                .name("comedy")
                .build();

        entityManager.persistAndFlush(genre);

        // When
        Optional<Genre> result = genreRepository.findByNameIgnoreCase("COMEDY");

        // Then
        assertTrue(result.isPresent());
        assertEquals("comedy", result.get().getName());
    }

    @Test
    void genreRepository_findByNameIgnoreCaseNotFound() {
        // Given
        Genre genre = Genre.builder()
                .name("Drama")
                .build();

        entityManager.persistAndFlush(genre);

        // When
        Optional<Genre> result = genreRepository.findByNameIgnoreCase("Horror");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void genreRepository_existsByNameIgnoreCase() {
        // Given
        Genre genre = Genre.builder()
                .name("Thriller")
                .build();

        entityManager.persistAndFlush(genre);

        // When & Then
        assertTrue(genreRepository.existsByNameIgnoreCase("thriller"));
        assertTrue(genreRepository.existsByNameIgnoreCase("THRILLER"));
        assertTrue(genreRepository.existsByNameIgnoreCase("Thriller"));
        assertFalse(genreRepository.existsByNameIgnoreCase("Romance"));
    }

    @Test
    void genreRepository_findAll() {
        // Given
        Genre genre1 = Genre.builder().name("Action").build();
        Genre genre2 = Genre.builder().name("Comedy").build();

        entityManager.persistAndFlush(genre1);
        entityManager.persistAndFlush(genre2);

        // When
        List<Genre> result = genreRepository.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void genreRepository_save() {
        // Given
        Genre genre = Genre.builder()
                .name("Sci-Fi")
                .build();

        // When
        Genre savedGenre = genreRepository.save(genre);

        // Then
        assertNotNull(savedGenre.getGenreId());
        assertEquals("Sci-Fi", savedGenre.getName());
    }

    @Test
    void genreRepository_deleteById() {
        // Given
        Genre genre = Genre.builder()
                .name("Horror")
                .build();

        Genre savedGenre = entityManager.persistAndFlush(genre);
        Integer genreId = savedGenre.getGenreId();

        // When
        genreRepository.deleteById(genreId);

        // Then
        Optional<Genre> result = genreRepository.findById(genreId);
        assertFalse(result.isPresent());
    }
}

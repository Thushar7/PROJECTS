package nexi.cinetix.movie_service.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void genre_builderPattern() {
        Genre genre = Genre.builder()
                .genreId(1)
                .name("Action")
                .build();

        assertEquals(1, genre.getGenreId());
        assertEquals("Action", genre.getName());
        assertNotNull(genre.getMovieGenres());
    }

    @Test
    void genre_settersAndGetters() {
        Genre genre = new Genre();

        genre.setGenreId(5);
        genre.setName("Comedy");

        assertEquals(5, genre.getGenreId());
        assertEquals("Comedy", genre.getName());
    }

    @Test
    void genre_defaultCollections() {
        Genre genre = Genre.builder().build();

        assertNotNull(genre.getMovieGenres());
        assertTrue(genre.getMovieGenres().isEmpty());
    }

    @Test
    void genre_noArgsConstructor() {
        Genre genre = new Genre();
        assertNotNull(genre);
        assertNull(genre.getGenreId());
        assertNull(genre.getName());
    }

    @Test
    void genre_allArgsConstructor() {
        Genre genre = new Genre(10, "Thriller", new HashSet<>());

        assertEquals(10, genre.getGenreId());
        assertEquals("Thriller", genre.getName());
        assertNotNull(genre.getMovieGenres());
    }
}

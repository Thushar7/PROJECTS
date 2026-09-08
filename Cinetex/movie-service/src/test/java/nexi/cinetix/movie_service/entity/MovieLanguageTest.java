package nexi.cinetix.movie_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieLanguageTest {

    @Test
    void movieLanguage_builderPattern() {
        Movie movie = new Movie();
        movie.setMovieId(1);

        Language language = new Language();
        language.setLanguageId(1);

        MovieLanguage movieLanguage = MovieLanguage.builder()
                .id(1)
                .movie(movie)
                .language(language)
                .build();

        assertEquals(1, movieLanguage.getId());
        assertEquals(movie, movieLanguage.getMovie());
        assertEquals(language, movieLanguage.getLanguage());
    }

    @Test
    void movieLanguage_settersAndGetters() {
        MovieLanguage movieLanguage = new MovieLanguage();
        Movie movie = new Movie();
        movie.setMovieId(5);

        Language language = new Language();
        language.setLanguageId(3);

        movieLanguage.setId(10);
        movieLanguage.setMovie(movie);
        movieLanguage.setLanguage(language);

        assertEquals(10, movieLanguage.getId());
        assertEquals(movie, movieLanguage.getMovie());
        assertEquals(language, movieLanguage.getLanguage());
    }

    @Test
    void movieLanguage_equalsAndHashCode() {
        MovieLanguage movieLanguage1 = MovieLanguage.builder()
                .id(1)
                .build();

        MovieLanguage movieLanguage2 = MovieLanguage.builder()
                .id(1)
                .build();

        MovieLanguage movieLanguage3 = MovieLanguage.builder()
                .id(2)
                .build();

        assertEquals(movieLanguage1, movieLanguage2);
        assertNotEquals(movieLanguage1, movieLanguage3);
        assertEquals(movieLanguage1.hashCode(), movieLanguage2.hashCode());
    }

    @Test
    void movieLanguage_toString() {
        MovieLanguage movieLanguage = MovieLanguage.builder()
                .id(1)
                .build();

        String toString = movieLanguage.toString();
        assertTrue(toString.contains("id"));
        // Should exclude movie and language as per @ToString(exclude = {"movie", "language"})
        assertFalse(toString.contains("movie"));
        assertFalse(toString.contains("language"));
    }

    @Test
    void movieLanguage_noArgsConstructor() {
        MovieLanguage movieLanguage = new MovieLanguage();
        assertNotNull(movieLanguage);
        assertNull(movieLanguage.getId());
        assertNull(movieLanguage.getMovie());
        assertNull(movieLanguage.getLanguage());
    }

    @Test
    void movieLanguage_allArgsConstructor() {
        Movie movie = new Movie();
        Language language = new Language();

        MovieLanguage movieLanguage = new MovieLanguage(100, movie, language);

        assertEquals(100, movieLanguage.getId());
        assertEquals(movie, movieLanguage.getMovie());
        assertEquals(language, movieLanguage.getLanguage());
    }
}

package nexi.cinetix.movie_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieGenreTest {

    @Test
    void movieGenre_builderPattern() {
        Movie movie = new Movie();
        movie.setMovieId(1);

        Genre genre = new Genre();
        genre.setGenreId(1);

        MovieGenre movieGenre = MovieGenre.builder()
                .id(1)
                .movie(movie)
                .genre(genre)
                .build();

        assertEquals(1, movieGenre.getId());
        assertEquals(movie, movieGenre.getMovie());
        assertEquals(genre, movieGenre.getGenre());
    }

    @Test
    void movieGenre_settersAndGetters() {
        MovieGenre movieGenre = new MovieGenre();
        Movie movie = new Movie();
        movie.setMovieId(5);

        Genre genre = new Genre();
        genre.setGenreId(3);

        movieGenre.setId(10);
        movieGenre.setMovie(movie);
        movieGenre.setGenre(genre);

        assertEquals(10, movieGenre.getId());
        assertEquals(movie, movieGenre.getMovie());
        assertEquals(genre, movieGenre.getGenre());
    }

    @Test
    void movieGenre_equalsAndHashCode() {
        MovieGenre movieGenre1 = MovieGenre.builder()
                .id(1)
                .build();

        MovieGenre movieGenre2 = MovieGenre.builder()
                .id(1)
                .build();

        MovieGenre movieGenre3 = MovieGenre.builder()
                .id(2)
                .build();

        assertEquals(movieGenre1, movieGenre2);
        assertNotEquals(movieGenre1, movieGenre3);
        assertEquals(movieGenre1.hashCode(), movieGenre2.hashCode());
    }

    @Test
    void movieGenre_toString() {
        MovieGenre movieGenre = MovieGenre.builder()
                .id(1)
                .build();

        String toString = movieGenre.toString();
        assertTrue(toString.contains("id"));
        // Should exclude movie and genre as per @ToString(exclude = {"movie", "genre"})
        assertFalse(toString.contains("movie"));
        assertFalse(toString.contains("genre"));
    }

    @Test
    void movieGenre_noArgsConstructor() {
        MovieGenre movieGenre = new MovieGenre();
        assertNotNull(movieGenre);
        assertNull(movieGenre.getId());
        assertNull(movieGenre.getMovie());
        assertNull(movieGenre.getGenre());
    }

    @Test
    void movieGenre_allArgsConstructor() {
        Movie movie = new Movie();
        Genre genre = new Genre();

        MovieGenre movieGenre = new MovieGenre(100, movie, genre);

        assertEquals(100, movieGenre.getId());
        assertEquals(movie, movieGenre.getMovie());
        assertEquals(genre, movieGenre.getGenre());
    }
}

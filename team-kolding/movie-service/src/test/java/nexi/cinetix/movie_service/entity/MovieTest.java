package nexi.cinetix.movie_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    @Test
    void movie_builderPattern() {
        LocalDate releaseDate = LocalDate.of(2024, 5, 15);

        Movie movie = Movie.builder()
                .movieId(1)
                .title("Test Movie")
                .duration(120)
                .releaseDate(releaseDate)
                .description("A great movie")
                .posterContentType("image/jpeg")
                .build();

        assertEquals(1, movie.getMovieId());
        assertEquals("Test Movie", movie.getTitle());
        assertEquals(120, movie.getDuration());
        assertEquals(releaseDate, movie.getReleaseDate());
        assertEquals("A great movie", movie.getDescription());
        assertEquals("image/jpeg", movie.getPosterContentType());
        assertNotNull(movie.getMovieGenres());
        assertNotNull(movie.getMovieLanguages());
    }

    @Test
    void movie_settersAndGetters() {
        Movie movie = new Movie();
        LocalDate releaseDate = LocalDate.of(2023, 8, 20);
        byte[] posterData = "test image data".getBytes();

        movie.setMovieId(5);
        movie.setTitle("Another Movie");
        movie.setDuration(90);
        movie.setReleaseDate(releaseDate);
        movie.setDescription("Another description");
        movie.setPosterData(posterData);
        movie.setPosterContentType("image/png");

        assertEquals(5, movie.getMovieId());
        assertEquals("Another Movie", movie.getTitle());
        assertEquals(90, movie.getDuration());
        assertEquals(releaseDate, movie.getReleaseDate());
        assertEquals("Another description", movie.getDescription());
        assertArrayEquals(posterData, movie.getPosterData());
        assertEquals("image/png", movie.getPosterContentType());
    }

    @Test
    void movie_addGenre() {
        Movie movie = new Movie();
        Genre genre = new Genre();
        genre.setGenreId(1);
        genre.setName("Action");

        movie.addGenre(genre);

        assertEquals(1, movie.getMovieGenres().size());
        MovieGenre movieGenre = movie.getMovieGenres().iterator().next();
        assertEquals(movie, movieGenre.getMovie());
        assertEquals(genre, movieGenre.getGenre());
    }

    @Test
    void movie_addLanguage() {
        Movie movie = new Movie();
        Language language = new Language();
        language.setLanguageId(1);
        language.setName("English");

        movie.addLanguage(language);

        assertEquals(1, movie.getMovieLanguages().size());
        MovieLanguage movieLanguage = movie.getMovieLanguages().iterator().next();
        assertEquals(movie, movieLanguage.getMovie());
        assertEquals(language, movieLanguage.getLanguage());
    }

    @Test
    void movie_defaultCollections() {
        Movie movie = Movie.builder().build();

        assertNotNull(movie.getMovieGenres());
        assertNotNull(movie.getMovieLanguages());
        assertTrue(movie.getMovieGenres().isEmpty());
        assertTrue(movie.getMovieLanguages().isEmpty());
    }

    @Test
    void movie_noArgsConstructor() {
        Movie movie = new Movie();
        assertNotNull(movie);
        assertNull(movie.getMovieId());
        assertNull(movie.getTitle());
    }

    @Test
    void movie_allArgsConstructor() {
        LocalDate releaseDate = LocalDate.of(2024, 1, 1);
        byte[] posterData = "poster".getBytes();

        Movie movie = new Movie(
                10, "Constructor Movie", 110, releaseDate,
                "Constructor description", posterData, "image/jpeg",
                new HashSet<>(), new HashSet<>()
        );

        assertEquals(10, movie.getMovieId());
        assertEquals("Constructor Movie", movie.getTitle());
        assertEquals(110, movie.getDuration());
        assertEquals(releaseDate, movie.getReleaseDate());
        assertEquals("Constructor description", movie.getDescription());
        assertArrayEquals(posterData, movie.getPosterData());
        assertEquals("image/jpeg", movie.getPosterContentType());
        assertNotNull(movie.getMovieGenres());
        assertNotNull(movie.getMovieLanguages());
    }
}

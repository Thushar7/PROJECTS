package nexi.cinetix.movie_service.mapper;

import nexi.cinetix.movie_service.dto.MovieDto;
import nexi.cinetix.movie_service.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MovieMapperTest {

    private final MovieMapper mapper = new MovieMapper();

    @Test
    void toDto_shouldMapAllFieldsCorrectly() {
        // Arrange
        Genre genre = Genre.builder().genreId(1).name("Action").build();
        Language language = Language.builder().languageId(2).name("English").build();

        MovieGenre movieGenre = MovieGenre.builder().genre(genre).build();
        MovieLanguage movieLanguage = MovieLanguage.builder().language(language).build();

        byte[] posterData = "sample-image".getBytes();
        String contentType = "image/jpeg";

        Movie movie = Movie.builder()
                .movieId(100)
                .title("Test Movie")
                .duration(120)
                .releaseDate(LocalDate.of(2025, 5, 20))
                .description("A test movie description")
                .posterData(posterData)
                .posterContentType(contentType)
                .movieGenres(Set.of(movieGenre))
                .movieLanguages(Set.of(movieLanguage))
                .build();

        // Act
        MovieDto dto = mapper.toDto(movie);

        // Assert
        assertNotNull(dto);
        assertEquals(100, dto.getMovieId());
        assertEquals("Test Movie", dto.getTitle());
        assertEquals(120, dto.getDuration());
        assertEquals(LocalDate.of(2025, 5, 20), dto.getReleaseDate());
        assertEquals("A test movie description", dto.getDescription());
        assertEquals(contentType, dto.getPosterContentType());
        assertEquals(Base64.getEncoder().encodeToString(posterData), dto.getPosterBase64());
        assertEquals(List.of(1), dto.getGenreIds());
        assertEquals(List.of(2), dto.getLanguageIds());
    }

    @Test
    void toDto_shouldHandleNullPosterDataGracefully() {
        // Arrange
        Movie movie = Movie.builder()
                .movieId(101)
                .title("No Poster Movie")
                .duration(90)
                .releaseDate(LocalDate.of(2025, 6, 15))
                .description("No poster available")
                .posterData(null)
                .posterContentType(null)
                .movieGenres(Set.of())
                .movieLanguages(Set.of())
                .build();

        // Act
        MovieDto dto = mapper.toDto(movie);

        // Assert
        assertNotNull(dto);
        assertEquals(101, dto.getMovieId());
        assertEquals("No Poster Movie", dto.getTitle());
        assertEquals(90, dto.getDuration());
        assertEquals(LocalDate.of(2025, 6, 15), dto.getReleaseDate());
        assertEquals("No poster available", dto.getDescription());
        assertNull(dto.getPosterBase64());
        assertNull(dto.getPosterContentType());
        assertTrue(dto.getGenreIds().isEmpty());
        assertTrue(dto.getLanguageIds().isEmpty());
    }
}

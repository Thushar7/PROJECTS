package nexi.cinetix.movie_service.mapper;

import nexi.cinetix.movie_service.dto.GenreDto;
import nexi.cinetix.movie_service.entity.Genre;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreMapperTest {

    private final GenreMapper mapper = new GenreMapper();

    @Test
    void toDto_shouldMapAllFields() {
        Genre genre = Genre.builder()
                .genreId(5)
                .name("Action")
                .build();

        GenreDto dto = mapper.toDto(genre);

        assertNotNull(dto);
        assertEquals(5, dto.getGenreId());
        assertEquals("Action", dto.getName());
    }

    @Test
    void toDto_shouldHandleSpecialCharacters() {
        Genre genre = Genre.builder()
                .genreId(6)
                .name("Sci-Fi & Fantasy")
                .build();

        GenreDto dto = mapper.toDto(genre);

        assertEquals(6, dto.getGenreId());
        assertEquals("Sci-Fi & Fantasy", dto.getName());
    }

    @Test
    void toDto_nullInput_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.toDto(null));
    }
}

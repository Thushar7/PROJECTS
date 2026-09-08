package nexi.cinetix.movie_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreDtoTest {

    @Test
    void genreDto_builderPattern() {
        GenreDto dto = GenreDto.builder()
                .genreId(1)
                .name("Action")
                .build();

        assertEquals(1, dto.getGenreId());
        assertEquals("Action", dto.getName());
    }

    @Test
    void genreDto_settersAndGetters() {
        GenreDto dto = new GenreDto();

        dto.setGenreId(5);
        dto.setName("Comedy");

        assertEquals(5, dto.getGenreId());
        assertEquals("Comedy", dto.getName());
    }

    @Test
    void genreDto_equalsAndHashCode() {
        GenreDto dto1 = GenreDto.builder()
                .genreId(1)
                .name("Drama")
                .build();

        GenreDto dto2 = GenreDto.builder()
                .genreId(1)
                .name("Drama")
                .build();

        GenreDto dto3 = GenreDto.builder()
                .genreId(2)
                .name("Horror")
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void genreDto_toString() {
        GenreDto dto = GenreDto.builder()
                .genreId(1)
                .name("Thriller")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("genreId"));
        assertTrue(toString.contains("name"));
    }

    @Test
    void genreDto_noArgsConstructor() {
        GenreDto dto = new GenreDto();
        assertNotNull(dto);
        assertNull(dto.getGenreId());
        assertNull(dto.getName());
    }

    @Test
    void genreDto_allArgsConstructor() {
        GenreDto dto = new GenreDto(10, "Sci-Fi");

        assertEquals(10, dto.getGenreId());
        assertEquals("Sci-Fi", dto.getName());
    }
}

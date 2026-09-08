package nexi.cinetix.movie_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieLanguageDtoTest {

    @Test
    void movieLanguageDto_builderPattern() {
        MovieLanguageDto dto = MovieLanguageDto.builder()
                .id(1)
                .movieId(10)
                .languageId(5)
                .build();

        assertEquals(1, dto.getId());
        assertEquals(10, dto.getMovieId());
        assertEquals(5, dto.getLanguageId());
    }

    @Test
    void movieLanguageDto_settersAndGetters() {
        MovieLanguageDto dto = new MovieLanguageDto();

        dto.setId(15);
        dto.setMovieId(25);
        dto.setLanguageId(8);

        assertEquals(15, dto.getId());
        assertEquals(25, dto.getMovieId());
        assertEquals(8, dto.getLanguageId());
    }

    @Test
    void movieLanguageDto_equalsAndHashCode() {
        MovieLanguageDto dto1 = MovieLanguageDto.builder()
                .id(1)
                .movieId(10)
                .languageId(5)
                .build();

        MovieLanguageDto dto2 = MovieLanguageDto.builder()
                .id(1)
                .movieId(10)
                .languageId(5)
                .build();

        MovieLanguageDto dto3 = MovieLanguageDto.builder()
                .id(2)
                .movieId(10)
                .languageId(5)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void movieLanguageDto_toString() {
        MovieLanguageDto dto = MovieLanguageDto.builder()
                .id(1)
                .movieId(10)
                .languageId(5)
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("id"));
        assertTrue(toString.contains("movieId"));
        assertTrue(toString.contains("languageId"));
    }

    @Test
    void movieLanguageDto_noArgsConstructor() {
        MovieLanguageDto dto = new MovieLanguageDto();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getMovieId());
        assertNull(dto.getLanguageId());
    }

    @Test
    void movieLanguageDto_allArgsConstructor() {
        MovieLanguageDto dto = new MovieLanguageDto(100, 200, 50);

        assertEquals(100, dto.getId());
        assertEquals(200, dto.getMovieId());
        assertEquals(50, dto.getLanguageId());
    }
}

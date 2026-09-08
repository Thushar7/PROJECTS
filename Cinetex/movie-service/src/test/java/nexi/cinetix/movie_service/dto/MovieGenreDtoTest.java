package nexi.cinetix.movie_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieGenreDtoTest {

    @Test
    void movieGenreDto_builderPattern() {
        MovieGenreDto dto = MovieGenreDto.builder()
                .id(1)
                .movieId(10)
                .genreId(5)
                .build();

        assertEquals(1, dto.getId());
        assertEquals(10, dto.getMovieId());
        assertEquals(5, dto.getGenreId());
    }

    @Test
    void movieGenreDto_settersAndGetters() {
        MovieGenreDto dto = new MovieGenreDto();

        dto.setId(15);
        dto.setMovieId(25);
        dto.setGenreId(8);

        assertEquals(15, dto.getId());
        assertEquals(25, dto.getMovieId());
        assertEquals(8, dto.getGenreId());
    }

    @Test
    void movieGenreDto_equalsAndHashCode() {
        MovieGenreDto dto1 = MovieGenreDto.builder()
                .id(1)
                .movieId(10)
                .genreId(5)
                .build();

        MovieGenreDto dto2 = MovieGenreDto.builder()
                .id(1)
                .movieId(10)
                .genreId(5)
                .build();

        MovieGenreDto dto3 = MovieGenreDto.builder()
                .id(2)
                .movieId(10)
                .genreId(5)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void movieGenreDto_toString() {
        MovieGenreDto dto = MovieGenreDto.builder()
                .id(1)
                .movieId(10)
                .genreId(5)
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("id"));
        assertTrue(toString.contains("movieId"));
        assertTrue(toString.contains("genreId"));
    }

    @Test
    void movieGenreDto_noArgsConstructor() {
        MovieGenreDto dto = new MovieGenreDto();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getMovieId());
        assertNull(dto.getGenreId());
    }

    @Test
    void movieGenreDto_allArgsConstructor() {
        MovieGenreDto dto = new MovieGenreDto(100, 200, 50);

        assertEquals(100, dto.getId());
        assertEquals(200, dto.getMovieId());
        assertEquals(50, dto.getGenreId());
    }
}

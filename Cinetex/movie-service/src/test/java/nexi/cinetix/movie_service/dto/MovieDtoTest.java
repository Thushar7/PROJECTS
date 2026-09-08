package nexi.cinetix.movie_service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class MovieDtoTest {

    @Test
    void movieDto_equalsAndHashCode_shouldWork() {
        MovieDto movie1 = MovieDto.builder().movieId(1).title("Test").build();
        MovieDto movie2 = MovieDto.builder().movieId(1).title("Test").build();
        MovieDto movie3 = MovieDto.builder().movieId(2).title("Test").build();

        assertThat(movie1).isEqualTo(movie2);
        assertThat(movie1).isNotEqualTo(movie3);
        assertThat(movie1.hashCode()).isEqualTo(movie2.hashCode());
    }

    @Test
    void movieDto_toString_shouldContainFields() {
        MovieDto movie = MovieDto.builder()
                .movieId(1)
                .title("Test Movie")
                .description("Test Description")
                .build();

        String toString = movie.toString();

        assertThat(toString).contains("Test Movie");
        assertThat(toString).contains("Test Description");
        assertThat(toString).contains("1");
    }
}

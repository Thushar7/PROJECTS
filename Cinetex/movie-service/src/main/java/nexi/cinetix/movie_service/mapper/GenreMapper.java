package nexi.cinetix.movie_service.mapper;

import nexi.cinetix.movie_service.dto.GenreDto;
import nexi.cinetix.movie_service.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        return GenreDto.builder()
                .genreId(genre.getGenreId())
                .name(genre.getName())
                .build();
    }
}
package nexi.cinetix.movie_service.mapper;

import nexi.cinetix.movie_service.dto.MovieDto;
import nexi.cinetix.movie_service.entity.Movie;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class MovieMapper {

    public MovieDto toDto(Movie movie) {
        return MovieDto.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                .description(movie.getDescription())
                .posterContentType(movie.getPosterContentType())
                .posterBase64(movie.getPosterData() != null ?
                        Base64.getEncoder().encodeToString(movie.getPosterData()) : null)
                .genreIds(movie.getMovieGenres().stream()
                        .map(mg -> mg.getGenre().getGenreId())
                        .toList())
                .languageIds(movie.getMovieLanguages().stream()
                        .map(ml -> ml.getLanguage().getLanguageId())
                        .toList())
                .build();
    }
}
package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Integer> {
    List<MovieGenre> findByMovie_MovieId(Integer movieId);
    List<MovieGenre> findByGenre_GenreId(Integer genreId);
    void deleteByMovie_MovieIdAndGenre_GenreId(Integer movieId, Integer genreId);
    boolean existsByMovie_MovieIdAndGenre_GenreId(Integer movieId, Integer genreId);
}


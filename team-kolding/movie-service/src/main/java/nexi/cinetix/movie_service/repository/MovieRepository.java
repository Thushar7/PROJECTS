package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer>, JpaSpecificationExecutor<Movie> {
    List<Movie> findByTitleContainingIgnoreCase(String titlePart);
}

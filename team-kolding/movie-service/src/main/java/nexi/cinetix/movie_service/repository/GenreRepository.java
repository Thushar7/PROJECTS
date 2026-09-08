package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Optional<Genre> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}


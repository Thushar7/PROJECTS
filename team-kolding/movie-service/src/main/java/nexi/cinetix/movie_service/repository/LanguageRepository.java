package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Optional<Language> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}


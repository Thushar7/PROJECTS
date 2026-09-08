package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.MovieLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieLanguageRepository extends JpaRepository<MovieLanguage, Integer> {
    List<MovieLanguage> findByMovie_MovieId(Integer movieId);
    List<MovieLanguage> findByLanguage_LanguageId(Integer languageId);
    void deleteByMovie_MovieIdAndLanguage_LanguageId(Integer movieId, Integer languageId);
    boolean existsByMovie_MovieIdAndLanguage_LanguageId(Integer movieId, Integer languageId);
}


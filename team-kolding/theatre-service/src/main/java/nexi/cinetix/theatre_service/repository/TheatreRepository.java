package nexi.cinetix.theatre_service.repository;

import nexi.cinetix.theatre_service.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    // Filter helpers
    List<Theatre> findByStateIgnoreCase(String state);
    List<Theatre> findByCityIgnoreCase(String city);
    List<Theatre> findByStateIgnoreCaseAndCityIgnoreCase(String state, String city);
}

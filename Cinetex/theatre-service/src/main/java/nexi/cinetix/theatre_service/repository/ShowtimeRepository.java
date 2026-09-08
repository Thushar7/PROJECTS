package nexi.cinetix.theatre_service.repository;

import nexi.cinetix.theatre_service.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByTheatre_TheatreId(Long theatreId);
    List<Showtime> findByMovieId(Long movieId);
    List<Showtime> findByTheatre_TheatreIdAndStartTimeBetween(Long theatreId, LocalDateTime start, LocalDateTime end);

    // New date-based helpers
    List<Showtime> findByShowDate(LocalDate showDate);
    List<Showtime> findByMovieIdAndShowDate(Long movieId, LocalDate showDate);
    List<Showtime> findByTheatre_TheatreIdAndShowDate(Long theatreId, LocalDate showDate);
    List<Showtime> findByMovieIdAndTheatre_TheatreIdAndShowDate(Long movieId, Long theatreId, LocalDate showDate);

    // New combined (no date)
    List<Showtime> findByMovieIdAndTheatre_TheatreId(Long movieId, Long theatreId);
}

package nexi.cinetix.theatre_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Showtime entity representing a scheduled screening of a movie in a theatre.
 * movieId references a Movie aggregate in the movie-service (kept as a simple FK value here).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "showtimes", indexes = {
        @Index(name = "idx_showtime_movie_id", columnList = "movie_id"),
        @Index(name = "idx_showtime_theatre_id", columnList = "theatre_id"),
        @Index(name = "idx_showtime_show_date", columnList = "show_date")
})
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Long showtimeId;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false, foreignKey = @ForeignKey(name = "fk_showtime_theatre"))
    private Theatre theatre;
}

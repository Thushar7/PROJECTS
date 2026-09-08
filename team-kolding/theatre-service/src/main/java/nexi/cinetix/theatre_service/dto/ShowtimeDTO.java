package nexi.cinetix.theatre_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import nexi.cinetix.theatre_service.entity.Showtime;
import nexi.cinetix.theatre_service.entity.Theatre;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mutable DTO for Showtime. Holds theatreId instead of full Theatre for lightweight transport.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeDTO {
    private Long showtimeId; // null on create

    @NotNull
    private LocalDate showDate;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Long movieId;

    @NotNull
    private Long theatreId;

    public static ShowtimeDTO fromEntity(Showtime entity) {
        if (entity == null) return null;
        return ShowtimeDTO.builder()
                .showtimeId(entity.getShowtimeId())
                .showDate(entity.getShowDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .movieId(entity.getMovieId())
                .theatreId(entity.getTheatre() != null ? entity.getTheatre().getTheatreId() : null)
                .build();
    }

    public Showtime toEntity(Theatre theatre) {
        return Showtime.builder()
                .showtimeId(showtimeId)
                .showDate(showDate)
                .startTime(startTime)
                .endTime(endTime)
                .movieId(movieId)
                .theatre(theatre)
                .build();
    }
}

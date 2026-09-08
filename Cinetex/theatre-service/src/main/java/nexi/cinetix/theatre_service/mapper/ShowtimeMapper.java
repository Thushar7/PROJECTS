package nexi.cinetix.theatre_service.mapper;

import nexi.cinetix.theatre_service.dto.ShowtimeDTO;
import nexi.cinetix.theatre_service.entity.Showtime;
import nexi.cinetix.theatre_service.entity.Theatre;
import org.springframework.stereotype.Component;

/**
 * Mapper utility for converting between Showtime entity and ShowtimeDTO.
 * Assumptions:
 *  - Theatre entity exposes a getter for its ID (getTheatreId). If it instead uses getId, adjust the code accordingly.
 *  - ShowtimeDTO carries theatreId, while entity holds a Theatre reference (lazy loaded).
 */
@Component
public final class ShowtimeMapper {

    private ShowtimeMapper() { /* utility class */ }

    /**
     * Convert Showtime entity to DTO.
     * @param entity Showtime JPA entity
     * @return mapped DTO or null if entity null
     */
    public static ShowtimeDTO toDto(Showtime entity) {
        if (entity == null) {
            return null;
        }
        // Assumes Theatre has getTheatreId(); if it differs (e.g. getId()), adjust here.
        Long theatreId = entity.getTheatre() != null ? entity.getTheatre().getTheatreId() : null;
        return ShowtimeDTO.builder()
                .showtimeId(entity.getShowtimeId())
                .showDate(entity.getShowDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .movieId(entity.getMovieId())
                .theatreId(theatreId)
                .build();
    }

    /**
     * Convert DTO to new Showtime entity. Theatre must be resolved beforehand (e.g., via repository).
     * @param dto incoming DTO
     * @param theatre resolved Theatre entity corresponding to dto.theatreId
     * @return new Showtime entity or null if dto null
     */
    public static Showtime toEntity(ShowtimeDTO dto, Theatre theatre) {
        if (dto == null) {
            return null;
        }
        return Showtime.builder()
                .showtimeId(dto.getShowtimeId()) // null for create
                .showDate(dto.getShowDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .movieId(dto.getMovieId())
                .theatre(theatre)
                .build();
    }

    /**
     * Apply updates from DTO to existing entity (for partial/full updates).
     * Theatre can be replaced if non-null.
     */
    public static void updateEntity(Showtime entity, ShowtimeDTO dto, Theatre theatre) {
        if (entity == null || dto == null) {
            return;
        }
        entity.setShowDate(dto.getShowDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setMovieId(dto.getMovieId());
        if (theatre != null) {
            entity.setTheatre(theatre);
        }
    }

    /**
     * Basic invariant validation helper (optional usage before persisting).
     * Returns true if temporal constraints hold; false otherwise.
     */
    public static boolean isValidTemporalRange(ShowtimeDTO dto) {
        if (dto == null || dto.getStartTime() == null || dto.getEndTime() == null) {
            return false;
        }
        // Ensure start < end and dates align (startTime date equals showDate)
        return dto.getStartTime().isBefore(dto.getEndTime()) &&
                (dto.getShowDate() == null || dto.getStartTime().toLocalDate().equals(dto.getShowDate()));
    }
}

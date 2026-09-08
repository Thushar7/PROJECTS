package nexi.cinetix.theatre_service.dto;

import nexi.cinetix.theatre_service.entity.Showtime;
import nexi.cinetix.theatre_service.entity.Theatre;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ShowtimeDTOTest {

    @Test
    void fromEntity_null_returnsNull() {
        assertNull(ShowtimeDTO.fromEntity(null));
    }

    @Test
    void fromEntity_mapsAllFields_withTheatre() {
        Theatre theatre = Theatre.builder()
                .theatreId(200L)
                .name("Main Hall")
                .build();

        Showtime entity = Showtime.builder()
                .showtimeId(10L)
                .showDate(LocalDate.of(2025, 5, 20))
                .startTime(LocalDateTime.of(2025, 5, 20, 14, 0))
                .endTime(LocalDateTime.of(2025, 5, 20, 16, 0))
                .movieId(300L)
                .theatre(theatre)
                .build();

        ShowtimeDTO dto = ShowtimeDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(10L, dto.getShowtimeId());
        assertEquals(LocalDate.of(2025, 5, 20), dto.getShowDate());
        assertEquals(LocalDateTime.of(2025, 5, 20, 14, 0), dto.getStartTime());
        assertEquals(LocalDateTime.of(2025, 5, 20, 16, 0), dto.getEndTime());
        assertEquals(300L, dto.getMovieId());
        assertEquals(200L, dto.getTheatreId());
    }

    @Test
    void fromEntity_handlesNullTheatre() {
        Showtime entity = Showtime.builder()
                .showtimeId(11L)
                .showDate(LocalDate.of(2025, 6, 1))
                .startTime(LocalDateTime.of(2025, 6, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 6, 1, 12, 0))
                .movieId(400L)
                .theatre(null)
                .build();

        ShowtimeDTO dto = ShowtimeDTO.fromEntity(entity);

        assertNotNull(dto);
        assertNull(dto.getTheatreId());
    }

    @Test
    void toEntity_mapsAllFields() {
        ShowtimeDTO dto = ShowtimeDTO.builder()
                .showtimeId(15L)
                .showDate(LocalDate.of(2025, 7, 4))
                .startTime(LocalDateTime.of(2025, 7, 4, 18, 30))
                .endTime(LocalDateTime.of(2025, 7, 4, 20, 45))
                .movieId(500L)
                .theatreId(900L)
                .build();

        Theatre theatre = Theatre.builder()
                .theatreId(900L)
                .name("Room 1")
                .build();

        Showtime entity = dto.toEntity(theatre);

        assertNotNull(entity);
        assertEquals(15L, entity.getShowtimeId());
        assertEquals(LocalDate.of(2025, 7, 4), entity.getShowDate());
        assertEquals(LocalDateTime.of(2025, 7, 4, 18, 30), entity.getStartTime());
        assertEquals(LocalDateTime.of(2025, 7, 4, 20, 45), entity.getEndTime());
        assertEquals(500L, entity.getMovieId());
        assertNotNull(entity.getTheatre());
        assertEquals(900L, entity.getTheatre().getTheatreId());
    }

    @Test
    void toEntity_ignoresDtoTheatreIdMismatch_usesProvidedTheatre() {
        ShowtimeDTO dto = ShowtimeDTO.builder()
                .showtimeId(20L)
                .showDate(LocalDate.of(2025, 8, 10))
                .startTime(LocalDateTime.of(2025, 8, 10, 9, 0))
                .endTime(LocalDateTime.of(2025, 8, 10, 11, 0))
                .movieId(600L)
                .theatreId(111L) // differs from provided theatre
                .build();

        Theatre supplied = Theatre.builder()
                .theatreId(222L)
                .name("Different Theatre")
                .build();

        Showtime entity = dto.toEntity(supplied);

        assertEquals(222L, entity.getTheatre().getTheatreId());
    }

    @Test
    void roundTrip_entityToDtoToEntity_preservesCoreFields() {
        Theatre theatre = Theatre.builder()
                .theatreId(321L)
                .name("RoundTrip")
                .build();

        Showtime original = Showtime.builder()
                .showtimeId(77L)
                .showDate(LocalDate.of(2025, 9, 1))
                .startTime(LocalDateTime.of(2025, 9, 1, 13, 15))
                .endTime(LocalDateTime.of(2025, 9, 1, 15, 30))
                .movieId(999L)
                .theatre(theatre)
                .build();

        ShowtimeDTO dto = ShowtimeDTO.fromEntity(original);
        Showtime rebuilt = dto.toEntity(theatre);

        assertEquals(original.getShowtimeId(), rebuilt.getShowtimeId());
        assertEquals(original.getShowDate(), rebuilt.getShowDate());
        assertEquals(original.getStartTime(), rebuilt.getStartTime());
        assertEquals(original.getEndTime(), rebuilt.getEndTime());
        assertEquals(original.getMovieId(), rebuilt.getMovieId());
        assertEquals(original.getTheatre().getTheatreId(), rebuilt.getTheatre().getTheatreId());
    }
}

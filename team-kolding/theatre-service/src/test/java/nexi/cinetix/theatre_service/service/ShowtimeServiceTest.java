package nexi.cinetix.theatre_service.service;

import nexi.cinetix.theatre_service.dto.ShowtimeDTO;
import nexi.cinetix.theatre_service.entity.Showtime;
import nexi.cinetix.theatre_service.entity.Theatre;
import nexi.cinetix.theatre_service.exception.ShowtimeNotFoundException;
import nexi.cinetix.theatre_service.exception.TheatreNotFoundException;
import nexi.cinetix.theatre_service.repository.ShowtimeRepository;
import nexi.cinetix.theatre_service.repository.TheatreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private TheatreRepository theatreRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Theatre sampleTheatre;
    private Showtime sampleShowtime;
    private ShowtimeDTO sampleShowtimeDTO;

    @BeforeEach
    void setUp() {
        sampleTheatre = Theatre.builder()
                .theatreId(1L)
                .name("Test Theatre")
                .city("Test City")
                .state("Test State")
                .build();

        sampleShowtime = Showtime.builder()
                .showtimeId(1L)
                .movieId(10L)
                .theatre(sampleTheatre)
                .startTime(LocalDateTime.of(2023, 6, 15, 18, 0))
                .endTime(LocalDateTime.of(2023, 6, 15, 20, 0))
                .showDate(LocalDate.of(2023, 6, 15))
                .build();

        sampleShowtimeDTO = ShowtimeDTO.builder()
                .showtimeId(1L)
                .movieId(10L)
                .theatreId(1L)
                .startTime(LocalDateTime.of(2023, 6, 15, 18, 0))
                .endTime(LocalDateTime.of(2023, 6, 15, 20, 0))
                .showDate(LocalDate.of(2023, 6, 15))
                .build();
    }

    @Test
    void list_allFilters() {
        when(showtimeRepository.findByMovieIdAndTheatre_TheatreIdAndShowDate(10L, 1L, LocalDate.of(2023, 6, 15)))
                .thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(10L, 1L, LocalDate.of(2023, 6, 15));

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getMovieId());
    }

    @Test
    void list_movieAndDate() {
        when(showtimeRepository.findByMovieIdAndShowDate(10L, LocalDate.of(2023, 6, 15)))
                .thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(10L, null, LocalDate.of(2023, 6, 15));

        assertEquals(1, result.size());
        verify(showtimeRepository).findByMovieIdAndShowDate(10L, LocalDate.of(2023, 6, 15));
    }

    @Test
    void list_theatreAndDate() {
        when(showtimeRepository.findByTheatre_TheatreIdAndShowDate(1L, LocalDate.of(2023, 6, 15)))
                .thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(null, 1L, LocalDate.of(2023, 6, 15));

        assertEquals(1, result.size());
        verify(showtimeRepository).findByTheatre_TheatreIdAndShowDate(1L, LocalDate.of(2023, 6, 15));
    }

    @Test
    void list_dateOnly() {
        when(showtimeRepository.findByShowDate(LocalDate.of(2023, 6, 15)))
                .thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(null, null, LocalDate.of(2023, 6, 15));

        assertEquals(1, result.size());
        verify(showtimeRepository).findByShowDate(LocalDate.of(2023, 6, 15));
    }

    @Test
    void list_movieAndTheatre() {
        when(showtimeRepository.findByMovieIdAndTheatre_TheatreId(10L, 1L))
                .thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(10L, 1L, null);

        assertEquals(1, result.size());
        verify(showtimeRepository).findByMovieIdAndTheatre_TheatreId(10L, 1L);
    }

    @Test
    void list_movieOnly() {
        when(showtimeRepository.findByMovieId(10L))
                .thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(10L, null, null);

        assertEquals(1, result.size());
        verify(showtimeRepository).findByMovieId(10L);
    }

    @Test
    void list_theatreOnly() {
        when(showtimeRepository.findByTheatre_TheatreId(1L))
                .thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(null, 1L, null);

        assertEquals(1, result.size());
        verify(showtimeRepository).findByTheatre_TheatreId(1L);
    }

    @Test
    void list_noFilters() {
        when(showtimeRepository.findAll()).thenReturn(List.of(sampleShowtime));

        List<ShowtimeDTO> result = showtimeService.list(null, null, null);

        assertEquals(1, result.size());
        verify(showtimeRepository).findAll();
    }

    @Test
    void get_found() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(sampleShowtime));

        ShowtimeDTO result = showtimeService.get(1L);

        assertEquals(1L, result.getShowtimeId());
        assertEquals(10L, result.getMovieId());
    }

    @Test
    void get_notFound() {
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ShowtimeNotFoundException.class, () -> showtimeService.get(999L));
    }

    @Test
    void create_success() {
        ShowtimeDTO createDto = ShowtimeDTO.builder()
                .movieId(20L)
                .theatreId(1L)
                .startTime(LocalDateTime.of(2023, 6, 20, 19, 0))
                .endTime(LocalDateTime.of(2023, 6, 20, 21, 0))
                .build();

        Showtime savedShowtime = Showtime.builder()
                .showtimeId(2L)
                .movieId(20L)
                .theatre(sampleTheatre)
                .startTime(LocalDateTime.of(2023, 6, 20, 19, 0))
                .endTime(LocalDateTime.of(2023, 6, 20, 21, 0))
                .showDate(LocalDate.of(2023, 6, 20))
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(savedShowtime);

        ShowtimeDTO result = showtimeService.create(createDto);

        assertEquals(2L, result.getShowtimeId());
        assertEquals(20L, result.getMovieId());
        assertEquals(LocalDate.of(2023, 6, 20), result.getShowDate());
    }

    @Test
    void create_theatreNotFound() {
        ShowtimeDTO createDto = ShowtimeDTO.builder()
                .movieId(20L)
                .theatreId(999L)
                .startTime(LocalDateTime.of(2023, 6, 20, 19, 0))
                .endTime(LocalDateTime.of(2023, 6, 20, 21, 0))
                .build();

        when(theatreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> showtimeService.create(createDto));
        verify(showtimeRepository, never()).save(any());
    }

    @Test
    void create_invalidTimes() {
        ShowtimeDTO createDto = ShowtimeDTO.builder()
                .movieId(20L)
                .theatreId(1L)
                .startTime(LocalDateTime.of(2023, 6, 20, 21, 0))
                .endTime(LocalDateTime.of(2023, 6, 20, 19, 0)) // End before start
                .build();

        assertThrows(IllegalArgumentException.class, () -> showtimeService.create(createDto));
        verify(theatreRepository, never()).findById(any());
        verify(showtimeRepository, never()).save(any());
    }

    @Test
    void create_showDateFromStartTime() {
        ShowtimeDTO createDto = ShowtimeDTO.builder()
                .movieId(20L)
                .theatreId(1L)
                .startTime(LocalDateTime.of(2023, 6, 25, 20, 0))
                .endTime(LocalDateTime.of(2023, 6, 25, 22, 0))
                .showDate(null) // Should be derived from startTime
                .build();

        Showtime savedShowtime = Showtime.builder()
                .showtimeId(3L)
                .movieId(20L)
                .theatre(sampleTheatre)
                .startTime(LocalDateTime.of(2023, 6, 25, 20, 0))
                .endTime(LocalDateTime.of(2023, 6, 25, 22, 0))
                .showDate(LocalDate.of(2023, 6, 25))
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(savedShowtime);

        ShowtimeDTO result = showtimeService.create(createDto);

        assertEquals(LocalDate.of(2023, 6, 25), result.getShowDate());
    }

    @Test
    void create_normalizeMismatchedShowDate() {
        ShowtimeDTO createDto = ShowtimeDTO.builder()
                .movieId(20L)
                .theatreId(1L)
                .startTime(LocalDateTime.of(2023, 6, 25, 20, 0))
                .endTime(LocalDateTime.of(2023, 6, 25, 22, 0))
                .showDate(LocalDate.of(2023, 6, 24)) // Different from startTime date
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(showtimeRepository.save(any(Showtime.class))).thenAnswer(invocation -> {
            Showtime showtime = invocation.getArgument(0);
            showtime.setShowtimeId(4L);
            return showtime;
        });

        ShowtimeDTO result = showtimeService.create(createDto);

        // Should normalize to startTime date
        assertEquals(LocalDate.of(2023, 6, 25), result.getShowDate());
    }

    @Test
    void update_success() {
        ShowtimeDTO updateDto = ShowtimeDTO.builder()
                .movieId(30L)
                .theatreId(1L) // Same theatre
                .startTime(LocalDateTime.of(2023, 6, 16, 20, 0))
                .endTime(LocalDateTime.of(2023, 6, 16, 22, 30))
                .build();

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(sampleShowtime));

        ShowtimeDTO result = showtimeService.update(1L, updateDto);

        assertEquals(30L, result.getMovieId());
        assertEquals(LocalDate.of(2023, 6, 16), result.getShowDate());
        // Verify entity was updated
        assertEquals(30L, sampleShowtime.getMovieId());
    }

    @Test
    void update_changeTheatre() {
        Theatre newTheatre = Theatre.builder().theatreId(2L).name("New Theatre").build();
        ShowtimeDTO updateDto = ShowtimeDTO.builder()
                .movieId(30L)
                .theatreId(2L) // Different theatre
                .startTime(LocalDateTime.of(2023, 6, 16, 20, 0))
                .endTime(LocalDateTime.of(2023, 6, 16, 22, 30))
                .build();

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(sampleShowtime));
        when(theatreRepository.findById(2L)).thenReturn(Optional.of(newTheatre));

        ShowtimeDTO result = showtimeService.update(1L, updateDto);

        assertEquals(2L, result.getTheatreId());
        assertEquals(newTheatre, sampleShowtime.getTheatre());
    }

    @Test
    void update_newTheatreNotFound() {
        ShowtimeDTO updateDto = ShowtimeDTO.builder()
                .movieId(30L)
                .theatreId(999L)
                .startTime(LocalDateTime.of(2023, 6, 16, 20, 0))
                .endTime(LocalDateTime.of(2023, 6, 16, 22, 30))
                .build();

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(sampleShowtime));
        when(theatreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> showtimeService.update(1L, updateDto));
    }

    @Test
    void update_notFound() {
        ShowtimeDTO updateDto = ShowtimeDTO.builder().movieId(30L).build();
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ShowtimeNotFoundException.class, () -> showtimeService.update(999L, updateDto));
    }

    @Test
    void delete_success() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(sampleShowtime));

        showtimeService.delete(1L);

        verify(showtimeRepository).delete(sampleShowtime);
    }

    @Test
    void delete_notFound() {
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ShowtimeNotFoundException.class, () -> showtimeService.delete(999L));
        verify(showtimeRepository, never()).delete(any());
    }
}

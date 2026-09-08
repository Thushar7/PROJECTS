package nexi.cinetix.theatre_service.service;

import lombok.RequiredArgsConstructor;
import nexi.cinetix.theatre_service.dto.ShowtimeDTO;
import nexi.cinetix.theatre_service.entity.Showtime;
import nexi.cinetix.theatre_service.entity.Theatre;
import nexi.cinetix.theatre_service.exception.ShowtimeNotFoundException;
import nexi.cinetix.theatre_service.exception.TheatreNotFoundException;
import nexi.cinetix.theatre_service.mapper.ShowtimeMapper;
import nexi.cinetix.theatre_service.repository.ShowtimeRepository;
import nexi.cinetix.theatre_service.repository.TheatreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime; // added for now filtering
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final TheatreRepository theatreRepository;

    public List<ShowtimeDTO> list(Long movieId, Long theatreId, LocalDate date) {
        List<Showtime> data;
        if (movieId != null && theatreId != null && date != null) {
            data = showtimeRepository.findByMovieIdAndTheatre_TheatreIdAndShowDate(movieId, theatreId, date);
        } else if (movieId != null && date != null) {
            data = showtimeRepository.findByMovieIdAndShowDate(movieId, date);
        } else if (theatreId != null && date != null) {
            data = showtimeRepository.findByTheatre_TheatreIdAndShowDate(theatreId, date);
        } else if (date != null) {
            data = showtimeRepository.findByShowDate(date);
        } else if (movieId != null && theatreId != null) {
            data = showtimeRepository.findByMovieIdAndTheatre_TheatreId(movieId, theatreId);
        } else if (movieId != null) {
            data = showtimeRepository.findByMovieId(movieId);
        } else if (theatreId != null) {
            data = showtimeRepository.findByTheatre_TheatreId(theatreId);
        } else {
            data = showtimeRepository.findAll();
        }
        // Filter out past showtimes (only keep showtimes strictly after now)
        LocalDateTime now = LocalDateTime.now();
        return data.stream()
                .filter(s -> s.getStartTime() != null && s.getStartTime().isAfter(now))
                .sorted(Comparator.comparing(Showtime::getStartTime))
                .map(ShowtimeMapper::toDto)
                .toList();
    }

    public ShowtimeDTO get(Long id) { return ShowtimeMapper.toDto(find(id)); }

    @Transactional
    public ShowtimeDTO create(ShowtimeDTO dto) {
        validate(dto);
        Theatre theatre = theatreRepository.findById(dto.getTheatreId())
                .orElseThrow(() -> new TheatreNotFoundException(dto.getTheatreId()));
        if (dto.getShowDate() == null && dto.getStartTime() != null) {
            dto.setShowDate(dto.getStartTime().toLocalDate());
        }
        // Normalize showDate to startTime date if mismatch
        if (dto.getStartTime() != null && dto.getShowDate() != null && !dto.getShowDate().equals(dto.getStartTime().toLocalDate())) {
            dto.setShowDate(dto.getStartTime().toLocalDate());
        }
        dto.setShowtimeId(null);
        Showtime saved = showtimeRepository.save(ShowtimeMapper.toEntity(dto, theatre));
        return ShowtimeMapper.toDto(saved);
    }

    @Transactional
    public ShowtimeDTO update(Long id, ShowtimeDTO dto) {
        validate(dto);
        Showtime existing = find(id);
        Theatre newTheatre = null;
        if (!existing.getTheatre().getTheatreId().equals(dto.getTheatreId())) {
            newTheatre = theatreRepository.findById(dto.getTheatreId())
                    .orElseThrow(() -> new TheatreNotFoundException(dto.getTheatreId()));
        }
        ShowtimeMapper.updateEntity(existing, dto, newTheatre);
        if (dto.getStartTime() != null) {
            existing.setShowDate(dto.getStartTime().toLocalDate());
        }
        return ShowtimeMapper.toDto(existing);
    }

    @Transactional
    public void delete(Long id) { showtimeRepository.delete(find(id)); }

    private Showtime find(Long id) {
        return showtimeRepository.findById(id).orElseThrow(() -> new ShowtimeNotFoundException(id));
    }

    private void validate(ShowtimeDTO dto) {
        if (dto.getStartTime() != null && dto.getEndTime() != null && !dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
    }
}

package nexi.cinetix.theatre_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nexi.cinetix.theatre_service.dto.TheatreDTO;
import nexi.cinetix.theatre_service.dto.ShowtimeDTO;
import nexi.cinetix.theatre_service.service.TheatreService;
import nexi.cinetix.theatre_service.service.ShowtimeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/theatres")
@RequiredArgsConstructor
@Validated
public class TheatreController {

    private final TheatreService service;
    private final ShowtimeService showtimeService;

    @GetMapping
    public List<TheatreDTO> list(@RequestParam(required = false) String state,
                                 @RequestParam(required = false) String city) {
        return service.list(state, city);
    }

    @GetMapping("/{id}")
    public TheatreDTO get(@PathVariable Long id) { return service.get(id); }

    // New: list showtimes by movie (supports movieId or movie_id param name), optional theatre/date filters
    @GetMapping("/showtimes")
    public List<ShowtimeDTO> showtimesByMovie(@RequestParam(name = "movieId", required = false) Long movieId,
                                              @RequestParam(name = "movie_id", required = false) Long movieIdAlt,
                                              @RequestParam(name = "theatreId", required = false) Long theatreId,
                                              @RequestParam(name = "date", required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long effectiveMovieId = movieId != null ? movieId : movieIdAlt;
        if (effectiveMovieId == null) {
            throw new IllegalArgumentException("movieId is required");
        }
        return showtimeService.list(effectiveMovieId, theatreId, date);
    }

    @PostMapping
    public ResponseEntity<TheatreDTO> create(@Valid @RequestBody TheatreDTO dto) {
        TheatreDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/theatres/" + created.getTheatreId())).body(created);
    }

    @PutMapping("/{id}")
    public TheatreDTO update(@PathVariable Long id, @Valid @RequestBody TheatreDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

package nexi.cinetix.theatre_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nexi.cinetix.theatre_service.dto.ShowtimeDTO;
import nexi.cinetix.theatre_service.service.ShowtimeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // List showtimes filtered by optional parameters
    @GetMapping
    public List<ShowtimeDTO> list(@RequestParam(required = false) Long movieId,
                                  @RequestParam(required = false) Long theatreId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return showtimeService.list(movieId, theatreId, date);
    }

    // Retrieve single showtime
    @GetMapping("/{id}")
    public ShowtimeDTO get(@PathVariable Long id) { return showtimeService.get(id); }

    // Create showtime
    @PostMapping
    public ResponseEntity<ShowtimeDTO> create(@Valid @RequestBody ShowtimeDTO dto) {
        ShowtimeDTO created = showtimeService.create(dto);
        return ResponseEntity.created(URI.create("/showtimes/" + created.getShowtimeId())).body(created);
    }

    // Update showtime
    @PutMapping("/{id}")
    public ShowtimeDTO update(@PathVariable Long id, @Valid @RequestBody ShowtimeDTO dto) {
        return showtimeService.update(id, dto);
    }

    // Delete showtime
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { showtimeService.delete(id); }
}


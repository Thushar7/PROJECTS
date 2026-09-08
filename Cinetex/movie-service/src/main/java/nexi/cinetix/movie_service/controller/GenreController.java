package nexi.cinetix.movie_service.controller;

import lombok.RequiredArgsConstructor;
import nexi.cinetix.movie_service.dto.GenreDto;
import nexi.cinetix.movie_service.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/movies/genres")
@RequiredArgsConstructor
@Tag(name = "Genres", description = "Operations related to movie genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "List all genres")
    public List<GenreDto> list() { return genreService.getAll(); }

    @GetMapping("/{id}")
    @Operation(summary = "Get a genre by id")
    public GenreDto get(@PathVariable Integer id) { return genreService.get(id); }

    @PostMapping
    @Operation(summary = "Create a new genre")
    public ResponseEntity<GenreDto> create(@RequestBody GenreDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing genre")
    public GenreDto update(@PathVariable Integer id, @RequestBody GenreDto dto) { return genreService.update(id, dto); }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a genre")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

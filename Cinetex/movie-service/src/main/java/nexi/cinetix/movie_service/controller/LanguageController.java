package nexi.cinetix.movie_service.controller;

import lombok.RequiredArgsConstructor;
import nexi.cinetix.movie_service.dto.LanguageDto;
import nexi.cinetix.movie_service.service.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/movies/languages")
@RequiredArgsConstructor
@Tag(name = "Languages", description = "Operations related to movie languages")
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    @Operation(summary = "List all languages")
    public List<LanguageDto> list() { return languageService.getAll(); }

    @GetMapping("/{id}")
    @Operation(summary = "Get a language by id")
    public LanguageDto get(@PathVariable Integer id) { return languageService.get(id); }

    @PostMapping
    @Operation(summary = "Create a new language")
    public ResponseEntity<LanguageDto> create(@RequestBody LanguageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(languageService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing language")
    public LanguageDto update(@PathVariable Integer id, @RequestBody LanguageDto dto) { return languageService.update(id, dto); }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a language")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        languageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

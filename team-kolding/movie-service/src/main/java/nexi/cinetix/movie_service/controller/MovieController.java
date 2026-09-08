package nexi.cinetix.movie_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nexi.cinetix.movie_service.dto.MovieDto;
import nexi.cinetix.movie_service.dto.PageResponse;
import nexi.cinetix.movie_service.service.MovieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Operations related to movies and their associations")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "List all movies or filter by genres and languages (supports pagination with ?page=N, size fixed=10)")
    public List<MovieDto> list(@RequestParam(value = "genres", required = false) String genres,
                               @RequestParam(value = "languages", required = false) String languages,
                               @RequestParam(value = "page", required = false) Integer page) {
        List<Integer> genreIds = parseIdList(genres);
        List<Integer> languageIds = parseIdList(languages);
        boolean pageRequested = page != null;
        int pageIndex = (page != null && page >= 0) ? page : 0;
        boolean noGenreFilter = genreIds.isEmpty();
        boolean noLanguageFilter = languageIds.isEmpty();
        if (noGenreFilter && noLanguageFilter) {
            return pageRequested ? movieService.getAll(pageIndex) : movieService.getAll();
        }
        return pageRequested ? movieService.getAllFiltered(genreIds, languageIds, pageIndex)
                : movieService.getAllFiltered(genreIds, languageIds);
    }

    private List<Integer> parseIdList(String raw) {
        if (raw == null || raw.isBlank()) return Collections.emptyList();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .flatMap(token -> {
                    try { return Stream.of(Integer.valueOf(token)); } catch (NumberFormatException ex) { return Stream.empty(); }
                })
                .distinct()
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a movie by id")
    public MovieDto get(@PathVariable Integer id) { return movieService.get(id); }

    @GetMapping("/search")
    @Operation(summary = "Search movies by title substring")
    public List<MovieDto> search(@RequestParam("title") String title) { return movieService.searchByTitle(title); }

    @PostMapping
    @Operation(summary = "Create a new movie with optional genre and language associations (posterBase64 optional)")
    public ResponseEntity<MovieDto> create(@RequestBody MovieDto dto) { return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(dto)); }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing movie and replace its associations (posterBase64 optional)")
    public MovieDto update(@PathVariable Integer id, @RequestBody MovieDto dto) { return movieService.update(id, dto); }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie")
    public ResponseEntity<Void> delete(@PathVariable Integer id) { movieService.delete(id); return ResponseEntity.noContent().build(); }

    @PostMapping("/{id}/genres")
    @Operation(summary = "Add one or more genres to a movie")
    public MovieDto addGenres(@PathVariable Integer id, @RequestBody List<Integer> genreIds) { return movieService.addGenres(id, genreIds); }

    @DeleteMapping("/{movieId}/genres/{genreId}")
    @Operation(summary = "Remove a genre from a movie")
    public MovieDto removeGenre(@PathVariable Integer movieId, @PathVariable Integer genreId) { return movieService.removeGenre(movieId, genreId); }

    @PostMapping("/{id}/languages")
    @Operation(summary = "Add one or more languages to a movie")
    public MovieDto addLanguages(@PathVariable Integer id, @RequestBody List<Integer> languageIds) { return movieService.addLanguages(id, languageIds); }

    @DeleteMapping("/{movieId}/languages/{languageId}")
    @Operation(summary = "Remove a language from a movie")
    public MovieDto removeLanguage(@PathVariable Integer movieId, @PathVariable Integer languageId) { return movieService.removeLanguage(movieId, languageId); }

    @PostMapping(path = "/{id}/poster")
    @Operation(summary = "Upload or replace a movie poster (multipart/form-data; key 'file' or any first file)")
    public ResponseEntity<Map<String, Object>> uploadPoster(@PathVariable Integer id,
                                                            @RequestParam(value = "file", required = false) MultipartFile file,
                                                            HttpServletRequest rawRequest) throws Exception {
        if (file == null) {
            if (rawRequest instanceof MultipartHttpServletRequest mreq) {
                if (!mreq.getFileMap().isEmpty()) {
                    file = mreq.getFileMap().values().iterator().next();
                }
            }
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No file part present or file empty"));
        }
        byte[] data = file.getBytes();
        String contentType = file.getContentType();
        movieService.updatePoster(id, data, contentType);
        return ResponseEntity.ok(Map.of(
                "message", "Poster uploaded",
                "movieId", id,
                "size", data.length,
                "contentType", contentType,
                "fieldAccepted", file.getName()
        ));
    }

    @GetMapping("/{id}/poster")
    @Operation(summary = "Download movie poster as binary stream")
    public ResponseEntity<byte[]> getPoster(@PathVariable Integer id) {
        byte[] data = movieService.getPosterData(id);
        if (data == null || data.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        String contentType = movieService.getPosterContentType(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=movie-" + id + "-poster")
                .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/page")
    @Operation(summary = "Paged movie list with metadata; supports filters. Params: page (0-based), size (default 10, max 100), genres, languages")
    public PageResponse<MovieDto> page(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "genres", required = false) String genres,
                                       @RequestParam(value = "languages", required = false) String languages) {
        List<Integer> genreIds = parseIdList(genres);
        List<Integer> languageIds = parseIdList(languages);
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);
        return movieService.getPage(safePage, safeSize, genreIds, languageIds);
    }
}

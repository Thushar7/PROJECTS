package nexi.cinetix.movie_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.movie_service.dto.MovieDto;
import nexi.cinetix.movie_service.dto.PageResponse;
import nexi.cinetix.movie_service.entity.*;
import nexi.cinetix.movie_service.exception.ResourceNotFoundException;
import nexi.cinetix.movie_service.mapper.MovieMapper;
import nexi.cinetix.movie_service.messaging.MovieEventProducer;
import nexi.cinetix.movie_service.repository.*;
import nexi.cinetix.movie_service.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static nexi.cinetix.movie_service.constants.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final LanguageRepository languageRepository;
    private final MovieEventProducer movieEventProducer;
    private final MovieMapper movieMapper; // injected mapper

    @Override
    public List<MovieDto> getAll() {
        log.debug("Fetching all movies");
        return movieRepository.findAll().stream().map(movieMapper::toDto).toList();
    }

    @Override
    public List<MovieDto> getAll(int page) {
        int p = Math.max(0, page);
        Pageable pageable = PageRequest.of(p, 10);
        log.debug("Fetching movies page={} size=10", p);
        return movieRepository.findAll(pageable).getContent().stream().map(movieMapper::toDto).toList();
    }

    @Override
    public List<MovieDto> getAllFiltered(List<Integer> genreIds, List<Integer> languageIds) {
        boolean noGenreFilter = genreIds == null || genreIds.isEmpty();
        boolean noLanguageFilter = languageIds == null || languageIds.isEmpty();
        if (noGenreFilter && noLanguageFilter) {
            return getAll();
        }

        Specification<Movie> spec = (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        };

        if (!noGenreFilter) {
            spec = spec.and((root, query, cb) ->
                    root.join(FIELD_MOVIE_GENRES).get(FIELD_GENRE).get(FIELD_GENRE_ID).in(genreIds)
            );
        }

        if (!noLanguageFilter) {
            spec = spec.and((root, query, cb) ->
                    root.join(FIELD_MOVIE_LANGUAGES).get(FIELD_LANGUAGE).get(FIELD_LANGUAGE_ID).in(languageIds)
            );
        }

        log.debug("Filtering movies (unpaged) by genres={} languages={}", genreIds, languageIds);
        return movieRepository.findAll(spec).stream()
                .map(movieMapper::toDto)
                .toList();
    }



    @Override
    public List<MovieDto> getAllFiltered(List<Integer> genreIds, List<Integer> languageIds, int page) {
        int p = Math.max(0, page);
        boolean noGenreFilter = genreIds == null || genreIds.isEmpty();
        boolean noLanguageFilter = languageIds == null || languageIds.isEmpty();
        if (noGenreFilter && noLanguageFilter) {
            return getAll(p);
        }
        Specification<Movie> spec = (root, query, cb) -> { query.distinct(true); return cb.conjunction(); };
        if (!noGenreFilter) {
            spec = spec.and((root, query, cb) -> root.join("movieGenres").get("genre").get("genreId").in(genreIds));
        }
        if (!noLanguageFilter) {
            spec = spec.and((root, query, cb) -> root.join("movieLanguages").get("language").get("languageId").in(languageIds));
        }
        Pageable pageable = PageRequest.of(p, 10);
        log.debug("Filtering movies page={} size=10 by genres={} languages={}", p, genreIds, languageIds);
        return movieRepository.findAll(spec, pageable).getContent().stream().map(movieMapper::toDto).toList();
    }

    @Override
    public MovieDto get(Integer id) {
        log.debug("Fetching movie id={}", id);
        return movieMapper.toDto(findMovie(id));
    }

    @Override
    public List<MovieDto> searchByTitle(String titlePart) {
        log.debug("Searching movies by titlePart='{}'", titlePart);
        return movieRepository.findByTitleContainingIgnoreCase(titlePart).stream().map(movieMapper::toDto).toList();
    }

    @Override
    public MovieDto create(MovieDto dto) {
        log.debug("Creating movie payload={}", dto);
        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .duration(dto.getDuration())
                .releaseDate(dto.getReleaseDate())
                .description(dto.getDescription())
                .build();
        if (dto.getPosterBase64() != null && !dto.getPosterBase64().isBlank()) {
            try {
                movie.setPosterData(Base64.getDecoder().decode(dto.getPosterBase64()));
                movie.setPosterContentType(dto.getPosterContentType());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid Base64 poster data supplied on create");
            }
        }
        attachGenres(movie, dto.getGenreIds());
        attachLanguages(movie, dto.getLanguageIds());
        Movie saved = movieRepository.save(movie);
        log.info("Movie created id={} title={}", saved.getMovieId(), saved.getTitle());
        movieEventProducer.publishMovieAdded(
                new MovieEventProducer.MovieAddedEvent(saved.getMovieId(), saved.getTitle(), saved.getDescription()));
        return movieMapper.toDto(saved);
    }

    @Override
    public MovieDto update(Integer id, MovieDto dto) {
        log.debug("Updating movie id={} payload={}", id, dto);
        Movie movie = findMovie(id);
        movie.setTitle(dto.getTitle());
        movie.setDuration(dto.getDuration());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setDescription(dto.getDescription());
        if (dto.getPosterBase64() != null && !dto.getPosterBase64().isBlank()) {
            try {
                movie.setPosterData(Base64.getDecoder().decode(dto.getPosterBase64()));
                movie.setPosterContentType(dto.getPosterContentType());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid Base64 poster data supplied on update for movie id={}", id);
            }
        }
        movie.getMovieGenres().clear();
        movie.getMovieLanguages().clear();
        attachGenres(movie, dto.getGenreIds());
        attachLanguages(movie, dto.getLanguageIds());
        Movie saved = movieRepository.save(movie);
        log.info("Movie updated id={} title={}", saved.getMovieId(), saved.getTitle());
        return movieMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        log.debug("Deleting movie id={}", id);
        Movie movie = findMovie(id);
        movieRepository.delete(movie);
        log.info("Movie deleted id={}", id);
    }

    @Override
    public MovieDto addGenres(Integer movieId, List<Integer> genreIds) {
        log.debug("Adding genres {} to movie id={}", genreIds, movieId);
        Movie movie = findMovie(movieId);
        attachGenres(movie, genreIds);
        Movie saved = movieRepository.save(movie);
        log.info("Genres added to movie id={} currentGenres={}", movieId, saved.getMovieGenres().size());
        return movieMapper.toDto(saved);
    }

    @Override
    public MovieDto removeGenre(Integer movieId, Integer genreId) {
        log.debug("Removing genre id={} from movie id={}", genreId, movieId);
        Movie movie = findMovie(movieId);
        boolean removed = movie.getMovieGenres().removeIf(mg -> Objects.equals(mg.getGenre().getGenreId(), genreId));
        if (!removed) {
            log.warn("Genre id={} not associated with movie id={} during removal", genreId, movieId);
        }
        Movie saved = movieRepository.save(movie);
        log.info("Genre removal processed movie id={} remainingGenres={}", movieId, saved.getMovieGenres().size());
        return movieMapper.toDto(saved);
    }

    @Override
    public MovieDto addLanguages(Integer movieId, List<Integer> languageIds) {
        log.debug("Adding languages {} to movie id={}", languageIds, movieId);
        Movie movie = findMovie(movieId);
        attachLanguages(movie, languageIds);
        Movie saved = movieRepository.save(movie);
        log.info("Languages added to movie id={} currentLanguages={}", movieId, saved.getMovieLanguages().size());
        return movieMapper.toDto(saved);
    }

    @Override
    public MovieDto removeLanguage(Integer movieId, Integer languageId) {
        log.debug("Removing language id={} from movie id={}", languageId, movieId);
        Movie movie = findMovie(movieId);
        boolean removed = movie.getMovieLanguages().removeIf(ml -> Objects.equals(ml.getLanguage().getLanguageId(), languageId));
        if (!removed) {
            log.warn("Language id={} not associated with movie id={} during removal", languageId, movieId);
        }
        Movie saved = movieRepository.save(movie);
        log.info("Language removal processed movie id={} remainingLanguages={}", movieId, saved.getMovieLanguages().size());
        return movieMapper.toDto(saved);
    }

    @Override
    public void updatePoster(Integer movieId, byte[] data, String contentType) {
        Movie movie = findMovie(movieId);
        movie.setPosterData(data);
        movie.setPosterContentType(contentType);
        movieRepository.save(movie);
        log.info("Poster updated for movie id={} size={} bytes", movieId, data == null ? 0 : data.length);
    }

    @Override
    public byte[] getPosterData(Integer movieId) {
        return findMovie(movieId).getPosterData();
    }

    @Override
    public String getPosterContentType(Integer movieId) {
        return findMovie(movieId).getPosterContentType();
    }

    @Override
    public PageResponse<MovieDto> getPage(int page, int size, List<Integer> genreIds, List<Integer> languageIds) {
        int p = Math.max(0, page);
        int s = size <= 0 ? 10 : Math.min(size, 100);
        boolean noGenre = genreIds == null || genreIds.isEmpty();
        boolean noLang = languageIds == null || languageIds.isEmpty();
        Specification<Movie> spec = (root, query, cb) -> { if (query != null) {query.distinct(true);} return cb.conjunction(); };
        if (!noGenre) {
            spec = spec.and((root, query, cb) -> root.join("movieGenres").get("genre").get("genreId").in(genreIds));
        }
        if (!noLang) {
            spec = spec.and((root, query, cb) -> root.join("movieLanguages").get("language").get("languageId").in(languageIds));
        }
        Pageable pageable = PageRequest.of(p, s);
        Page<Movie> result = (noGenre && noLang) ? movieRepository.findAll(pageable) : movieRepository.findAll(spec, pageable);
        List<MovieDto> content = result.getContent().stream().map(movieMapper::toDto).toList();
        return PageResponse.<MovieDto>builder()
                .page(p)
                .size(s)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(content)
                .build();
    }

    private Movie findMovie(Integer id) {
        return movieRepository.findById(id).orElseThrow(() -> {
            log.warn("Movie not found id={}", id);
            return new ResourceNotFoundException("Movie not found: " + id);
        });
    }

    private void attachGenres(Movie movie, List<Integer> genreIds) {
        if (genreIds == null) return;
        for (Integer gid : genreIds) {
            if (gid == null) continue;
            Genre genre = genreRepository.findById(gid)
                    .orElseThrow(() -> {
                        log.warn("Genre not found when attaching to movie id={} genreId={}", movie.getMovieId(), gid);
                        return new ResourceNotFoundException("Genre not found: " + gid);
                    });
            boolean exists = movie.getMovieGenres().stream().anyMatch(mg -> Objects.equals(mg.getGenre().getGenreId(), gid));
            if (exists) {
                log.debug("Skipping duplicate genreId={} for movie id={}", gid, movie.getMovieId());
            } else {
                movie.addGenre(genre);
            }
        }
    }

    private void attachLanguages(Movie movie, List<Integer> languageIds) {
        if (languageIds == null) return;
        for (Integer lid : languageIds) {
            if (lid == null) continue;
            Language lang = languageRepository.findById(lid)
                    .orElseThrow(() -> {
                        log.warn("Language not found when attaching to movie id={} languageId={}", movie.getMovieId(), lid);
                        return new ResourceNotFoundException("Language not found: " + lid);
                    });
            boolean exists = movie.getMovieLanguages().stream().anyMatch(ml -> Objects.equals(ml.getLanguage().getLanguageId(), lid));
            if (exists) {
                log.debug("Skipping duplicate languageId={} for movie id={}", lid, movie.getMovieId());
            } else {
                movie.addLanguage(lang);
            }
        }
    }

}

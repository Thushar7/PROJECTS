package nexi.cinetix.movie_service.service.impl;

import nexi.cinetix.movie_service.dto.MovieDto;
import nexi.cinetix.movie_service.dto.PageResponse;
import nexi.cinetix.movie_service.entity.Genre;
import nexi.cinetix.movie_service.entity.Language;
import nexi.cinetix.movie_service.entity.Movie;
import nexi.cinetix.movie_service.entity.MovieGenre;
import nexi.cinetix.movie_service.entity.MovieLanguage;
import nexi.cinetix.movie_service.exception.ResourceNotFoundException;
import nexi.cinetix.movie_service.repository.GenreRepository;
import nexi.cinetix.movie_service.repository.LanguageRepository;
import nexi.cinetix.movie_service.repository.MovieRepository;
import nexi.cinetix.movie_service.messaging.MovieEventProducer;
import nexi.cinetix.movie_service.mapper.MovieMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private MovieEventProducer movieEventProducer;
    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie sampleMovie;
    private Genre sampleGenre;
    private Language sampleLanguage;

    @BeforeEach
    void setUp() {
        sampleGenre = Genre.builder().genreId(1).name("Action").build();
        sampleLanguage = Language.builder().languageId(1).name("English").build();

        sampleMovie = Movie.builder()
            .movieId(1)
            .title("Test Movie")
            .duration(120)
            .releaseDate(LocalDate.of(2023, 1, 1))
            .description("Test Description")
            .movieGenres(new HashSet<>())
            .movieLanguages(new HashSet<>())
            .build();

        // Configure mapper mock to return appropriate DTOs - using lenient to avoid UnnecessaryStubbingException
        lenient().when(movieMapper.toDto(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            return MovieDto.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                .description(movie.getDescription())
                .genreIds(List.of())
                .languageIds(List.of())
                .build();
        });
    }

    @Test
    void getAll_success() {
        when(movieRepository.findAll()).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.getAll();

        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
    }

    @Test
    void getAllPaged_success() {
        Page<Movie> page = new PageImpl<>(List.of(sampleMovie));
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<MovieDto> result = movieService.getAll(0);

        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
    }

    @Test
    void getAllFiltered_withGenresAndLanguages() {
        when(movieRepository.findAll(any(Specification.class))).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.getAllFiltered(List.of(1), List.of(1));

        assertEquals(1, result.size());
        verify(movieRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllFiltered_noFilters() {
        when(movieRepository.findAll()).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.getAllFiltered(null, null);

        assertEquals(1, result.size());
        verify(movieRepository).findAll();
    }

    @Test
    void get_found() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));

        MovieDto result = movieService.get(1);

        assertEquals("Test Movie", result.getTitle());
    }

    @Test
    void get_notFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.get(999));
    }

    @Test
    void searchByTitle_success() {
        when(movieRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.searchByTitle("Test");

        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
    }

    @Test
    void create_success() {
        MovieDto dto = MovieDto.builder()
            .title("New Movie")
            .duration(90)
            .releaseDate(LocalDate.now())
            .description("New Description")
            .genreIds(List.of(1))
            .languageIds(List.of(1))
            .build();

        when(genreRepository.findById(1)).thenReturn(Optional.of(sampleGenre));
        when(languageRepository.findById(1)).thenReturn(Optional.of(sampleLanguage));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(100);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(100, result.getMovieId());
        assertEquals("New Movie", result.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void create_withValidBase64Poster() {
        String validBase64 = Base64.getEncoder().encodeToString("test image data".getBytes());
        MovieDto dto = MovieDto.builder()
            .title("Movie with Poster")
            .duration(90)
            .posterBase64(validBase64)
            .posterContentType("image/jpeg")
            .genreIds(List.of())
            .languageIds(List.of())
            .build();

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(101);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(101, result.getMovieId());
        verify(movieRepository).save(argThat(movie -> movie.getPosterData() != null));
    }

    @Test
    void create_withInvalidBase64Poster() {
        MovieDto dto = MovieDto.builder()
            .title("Movie with Invalid Poster")
            .duration(90)
            .posterBase64("invalid-base64-data")
            .posterContentType("image/jpeg")
            .genreIds(List.of())
            .languageIds(List.of())
            .build();

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(102);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(102, result.getMovieId());
        verify(movieRepository).save(argThat(movie -> movie.getPosterData() == null));
    }

    @Test
    void update_success() {
        MovieDto dto = MovieDto.builder()
            .title("Updated Movie")
            .duration(150)
            .genreIds(List.of(1))
            .languageIds(List.of(1))
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(genreRepository.findById(1)).thenReturn(Optional.of(sampleGenre));
        when(languageRepository.findById(1)).thenReturn(Optional.of(sampleLanguage));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.update(1, dto);

        assertEquals("Updated Movie", result.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_notFound() {
        MovieDto dto = MovieDto.builder().title("Updated").build();
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.update(999, dto));
    }

    @Test
    void delete_success() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));

        movieService.delete(1);

        verify(movieRepository).delete(sampleMovie);
    }

    @Test
    void delete_notFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.delete(999));
    }

    @Test
    void addGenres_success() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(genreRepository.findById(1)).thenReturn(Optional.of(sampleGenre));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addGenres(1, List.of(1));

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addGenres_genreNotFound() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.addGenres(1, List.of(999)));
    }

    @Test
    void removeGenre_success() {
        MovieGenre movieGenre = new MovieGenre();
        movieGenre.setGenre(sampleGenre);
        sampleMovie.getMovieGenres().add(movieGenre);

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.removeGenre(1, 1);

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addLanguages_success() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(languageRepository.findById(1)).thenReturn(Optional.of(sampleLanguage));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addLanguages(1, List.of(1));

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void removeLanguage_success() {
        MovieLanguage movieLanguage = new MovieLanguage();
        movieLanguage.setLanguage(sampleLanguage);
        sampleMovie.getMovieLanguages().add(movieLanguage);

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.removeLanguage(1, 1);

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updatePoster_success() {
        byte[] posterData = "test poster data".getBytes();
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        movieService.updatePoster(1, posterData, "image/jpeg");

        verify(movieRepository).save(argThat(movie ->
            movie.getPosterData() == posterData &&
            "image/jpeg".equals(movie.getPosterContentType())
        ));
    }

    @Test
    void getPosterData_success() {
        byte[] expectedData = "poster data".getBytes();
        sampleMovie.setPosterData(expectedData);
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));

        byte[] result = movieService.getPosterData(1);

        assertEquals(expectedData, result);
    }

    @Test
    void getPosterContentType_success() {
        sampleMovie.setPosterContentType("image/png");
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));

        String result = movieService.getPosterContentType(1);

        assertEquals("image/png", result);
    }

    @Test
    void getPage_success() {
        Page<Movie> page = new PageImpl<>(List.of(sampleMovie), Pageable.ofSize(10), 1);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<MovieDto> result = movieService.getPage(0, 10, null, null);

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getPage_withFilters() {
        Page<Movie> page = new PageImpl<>(List.of(sampleMovie), Pageable.ofSize(10), 1);
        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<MovieDto> result = movieService.getPage(0, 10, List.of(1), List.of(1));

        assertEquals(1, result.getContent().size());
        verify(movieRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    // Additional test cases for increased coverage

    @Test
    void create_withNullGenreIds() {
        MovieDto dto = MovieDto.builder()
            .title("Movie without genres")
            .duration(90)
            .genreIds(null)
            .languageIds(List.of())
            .build();

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(103);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(103, result.getMovieId());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void create_withNullLanguageIds() {
        MovieDto dto = MovieDto.builder()
            .title("Movie without languages")
            .duration(90)
            .genreIds(List.of())
            .languageIds(null)
            .build();

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(104);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(104, result.getMovieId());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void create_withEmptyTitle() {
        MovieDto dto = MovieDto.builder()
            .title("")
            .duration(90)
            .genreIds(List.of())
            .languageIds(List.of())
            .build();

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(105);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(105, result.getMovieId());
        assertEquals("", result.getTitle());
    }

    @Test
    void create_withZeroDuration() {
        MovieDto dto = MovieDto.builder()
            .title("Movie with zero duration")
            .duration(0)
            .genreIds(List.of())
            .languageIds(List.of())
            .build();

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(106);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(106, result.getMovieId());
        assertEquals(0, result.getDuration());
    }

    @Test
    void create_genreNotFound() {
        MovieDto dto = MovieDto.builder()
            .title("Movie with non-existent genre")
            .duration(90)
            .genreIds(List.of(999))
            .languageIds(List.of())
            .build();

        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.create(dto));
        assertNotNull(exception);
    }

    @Test
    void create_languageNotFound() {
        MovieDto dto = MovieDto.builder()
            .title("Movie with non-existent language")
            .duration(90)
            .genreIds(List.of())
            .languageIds(List.of(999))
            .build();

        when(languageRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.create(dto));
        assertNotNull(exception);
    }

    @Test
    void create_withNullPosterContentType() {
        String validBase64 = Base64.getEncoder().encodeToString("test image data".getBytes());
        MovieDto dto = MovieDto.builder()
            .title("Movie with poster but no content type")
            .duration(90)
            .posterBase64(validBase64)
            .posterContentType(null)
            .genreIds(List.of())
            .languageIds(List.of())
            .build();

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(107);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(107, result.getMovieId());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_withNullGenreIds() {
        MovieDto dto = MovieDto.builder()
            .title("Updated Movie")
            .duration(150)
            .genreIds(null)
            .languageIds(List.of())
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.update(1, dto);

        assertEquals("Updated Movie", result.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_withNullLanguageIds() {
        MovieDto dto = MovieDto.builder()
            .title("Updated Movie")
            .duration(150)
            .genreIds(List.of())
            .languageIds(null)
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.update(1, dto);

        assertEquals("Updated Movie", result.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_genreNotFound() {
        MovieDto dto = MovieDto.builder()
            .title("Updated Movie")
            .genreIds(List.of(999))
            .languageIds(List.of())
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.update(1, dto));
        assertNotNull(exception);
    }

    @Test
    void update_languageNotFound() {
        MovieDto dto = MovieDto.builder()
            .title("Updated Movie")
            .genreIds(List.of())
            .languageIds(List.of(999))
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(languageRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.update(1, dto));
        assertNotNull(exception);
    }

    @Test
    void update_withPosterData() {
        String validBase64 = Base64.getEncoder().encodeToString("updated poster data".getBytes());
        MovieDto dto = MovieDto.builder()
            .title("Updated Movie with Poster")
            .duration(150)
            .posterBase64(validBase64)
            .posterContentType("image/png")
            .genreIds(List.of())
            .languageIds(List.of())
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.update(1, dto);

        assertEquals("Updated Movie with Poster", result.getTitle());
        verify(movieRepository).save(argThat(movie -> movie.getPosterData() != null));
    }

    @Test
    void addGenres_movieNotFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.addGenres(999, List.of(1)));
        assertNotNull(exception);
    }

    @Test
    void addGenres_withNullGenreIds() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addGenres(1, null);

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addGenres_withEmptyGenreIds() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addGenres(1, List.of());

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addGenres_duplicateGenre() {
        // Add a genre that already exists
        MovieGenre existingGenre = new MovieGenre();
        existingGenre.setGenre(sampleGenre);
        sampleMovie.getMovieGenres().add(existingGenre);

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(genreRepository.findById(1)).thenReturn(Optional.of(sampleGenre));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addGenres(1, List.of(1));

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void removeGenre_movieNotFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.removeGenre(999, 1));
        assertNotNull(exception);
    }

    @Test
    void removeGenre_genreNotInMovie() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.removeGenre(1, 999);

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addLanguages_movieNotFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.addLanguages(999, List.of(1)));
        assertNotNull(exception);
    }

    @Test
    void addLanguages_languageNotFound() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(languageRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.addLanguages(1, List.of(999)));
        assertNotNull(exception);
    }

    @Test
    void addLanguages_withNullLanguageIds() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addLanguages(1, null);

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addLanguages_withEmptyLanguageIds() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addLanguages(1, List.of());

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addLanguages_duplicateLanguage() {
        // Add a language that already exists
        MovieLanguage existingLanguage = new MovieLanguage();
        existingLanguage.setLanguage(sampleLanguage);
        sampleMovie.getMovieLanguages().add(existingLanguage);

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(languageRepository.findById(1)).thenReturn(Optional.of(sampleLanguage));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.addLanguages(1, List.of(1));

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void removeLanguage_movieNotFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.removeLanguage(999, 1));
        assertNotNull(exception);
    }

    @Test
    void removeLanguage_languageNotInMovie() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.removeLanguage(1, 999);

        assertNotNull(result);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updatePoster_movieNotFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.updatePoster(999, "test".getBytes(), "image/jpeg"));
        assertNotNull(exception);
    }

    @Test
    void updatePoster_withNullData() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        movieService.updatePoster(1, null, "image/jpeg");

        verify(movieRepository).save(argThat(movie -> movie.getPosterData() == null));
    }

    @Test
    void updatePoster_withNullContentType() {
        byte[] posterData = "test poster data".getBytes();
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        movieService.updatePoster(1, posterData, null);

        verify(movieRepository).save(argThat(movie ->
            movie.getPosterData() == posterData && movie.getPosterContentType() == null));
    }

    @Test
    void getPosterData_movieNotFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.getPosterData(999));
        assertNotNull(exception);
    }

    @Test
    void getPosterData_noPosterData() {
        sampleMovie.setPosterData(null);
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));

        byte[] result = movieService.getPosterData(1);

        assertNull(result);
    }

    @Test
    void getPosterContentType_movieNotFound() {
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> movieService.getPosterContentType(999));
        assertNotNull(exception);
    }

    @Test
    void getPosterContentType_noContentType() {
        sampleMovie.setPosterContentType(null);
        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));

        String result = movieService.getPosterContentType(1);

        assertNull(result);
    }

    @Test
    void getAllFiltered_withEmptyFilters() {
        when(movieRepository.findAll()).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.getAllFiltered(List.of(), List.of());

        assertEquals(1, result.size());
        verify(movieRepository).findAll();
    }

    @Test
    void getAllFiltered_withOnlyGenres() {
        when(movieRepository.findAll(any(Specification.class))).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.getAllFiltered(List.of(1), null);

        assertEquals(1, result.size());
        verify(movieRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllFiltered_withOnlyLanguages() {
        when(movieRepository.findAll(any(Specification.class))).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.getAllFiltered(null, List.of(1));

        assertEquals(1, result.size());
        verify(movieRepository).findAll(any(Specification.class));
    }

    @Test
    void searchByTitle_emptyResult() {
        when(movieRepository.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(List.of());

        List<MovieDto> result = movieService.searchByTitle("NonExistent");

        assertEquals(0, result.size());
    }

    @Test
    void searchByTitle_nullTitle() {
        when(movieRepository.findByTitleContainingIgnoreCase(null)).thenReturn(List.of());

        List<MovieDto> result = movieService.searchByTitle(null);

        assertEquals(0, result.size());
    }

    @Test
    void searchByTitle_emptyTitle() {
        when(movieRepository.findByTitleContainingIgnoreCase("")).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieService.searchByTitle("");

        assertEquals(1, result.size());
    }

    @Test
    void getPage_emptyResult() {
        Page<Movie> emptyPage = new PageImpl<>(List.of(), Pageable.ofSize(10), 0);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        PageResponse<MovieDto> result = movieService.getPage(0, 10, null, null);

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void getPage_withLargePageSize() {
        Page<Movie> page = new PageImpl<>(List.of(sampleMovie), Pageable.ofSize(100), 1);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<MovieDto> result = movieService.getPage(0, 1000, null, null);

        assertEquals(100, result.getSize()); // Service likely caps page size at 100
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getPage_withEmptyFilters() {
        Page<Movie> page = new PageImpl<>(List.of(sampleMovie), Pageable.ofSize(10), 1);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<MovieDto> result = movieService.getPage(0, 10, List.of(), List.of());

        assertEquals(1, result.getContent().size());
        verify(movieRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAll_emptyResult() {
        when(movieRepository.findAll()).thenReturn(List.of());

        List<MovieDto> result = movieService.getAll();

        assertEquals(0, result.size());
    }

    @Test
    void getAllPaged_emptyResult() {
        Page<Movie> emptyPage = new PageImpl<>(List.of());
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        List<MovieDto> result = movieService.getAll(0);

        assertEquals(0, result.size());
    }

    @Test
    void create_withMultipleGenresAndLanguages() {
        Genre genre2 = Genre.builder().genreId(2).name("Comedy").build();
        Language language2 = Language.builder().languageId(2).name("Spanish").build();

        MovieDto dto = MovieDto.builder()
            .title("Multi-genre Movie")
            .duration(90)
            .genreIds(List.of(1, 2))
            .languageIds(List.of(1, 2))
            .build();

        when(genreRepository.findById(1)).thenReturn(Optional.of(sampleGenre));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre2));
        when(languageRepository.findById(1)).thenReturn(Optional.of(sampleLanguage));
        when(languageRepository.findById(2)).thenReturn(Optional.of(language2));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setMovieId(108);
            return movie;
        });

        MovieDto result = movieService.create(dto);

        assertEquals(108, result.getMovieId());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_withMultipleGenresAndLanguages() {
        Genre genre2 = Genre.builder().genreId(2).name("Comedy").build();
        Language language2 = Language.builder().languageId(2).name("Spanish").build();

        MovieDto dto = MovieDto.builder()
            .title("Updated Multi-genre Movie")
            .duration(150)
            .genreIds(List.of(1, 2))
            .languageIds(List.of(1, 2))
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(genreRepository.findById(1)).thenReturn(Optional.of(sampleGenre));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre2));
        when(languageRepository.findById(1)).thenReturn(Optional.of(sampleLanguage));
        when(languageRepository.findById(2)).thenReturn(Optional.of(language2));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.update(1, dto);

        assertEquals("Updated Multi-genre Movie", result.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_withInvalidBase64Poster() {
        MovieDto dto = MovieDto.builder()
            .title("Updated Movie with Invalid Poster")
            .duration(150)
            .posterBase64("invalid-base64-data")
            .posterContentType("image/jpeg")
            .genreIds(List.of())
            .languageIds(List.of())
            .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(sampleMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);

        MovieDto result = movieService.update(1, dto);

        assertEquals("Updated Movie with Invalid Poster", result.getTitle());
        verify(movieRepository).save(argThat(movie -> movie.getPosterData() == null));
    }
}

package nexi.cinetix.movie_service.service.impl;

import nexi.cinetix.movie_service.dto.GenreDto;
import nexi.cinetix.movie_service.entity.Genre;
import nexi.cinetix.movie_service.exception.ResourceNotFoundException;
import nexi.cinetix.movie_service.mapper.GenreMapper;
import nexi.cinetix.movie_service.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    private GenreRepository genreRepository;
    private GenreMapper genreMapper;
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        genreRepository = mock(GenreRepository.class);
        genreMapper = mock(GenreMapper.class);
        genreService = new GenreServiceImpl(genreRepository, genreMapper);
    }

    @Test
    void getAll_shouldReturnListOfGenres() {
        // Arrange
        Genre genre1 = new Genre();
        genre1.setGenreId(2);
        genre1.setName("Action");
        Genre genre2 = new Genre();
        genre2.setGenreId(2);
        genre2.setName("Comedy");
        when(genreRepository.findAll()).thenReturn(Arrays.asList(genre1, genre2));
        when(genreMapper.toDto(genre1)).thenReturn(new GenreDto(1, "Action"));
        when(genreMapper.toDto(genre2)).thenReturn(new GenreDto(2, "Comedy"));

        // Act
        List<GenreDto> result = genreService.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Action", result.get(0).getName());
        assertEquals("Comedy", result.get(1).getName());
    }

    @Test
    void get_shouldReturnGenreDto_whenFound() {
        // Arrange
        Genre genre = new Genre();
        genre.setGenreId(1);
        genre.setName("Action");
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(new GenreDto(1, "Action"));

        // Act
        GenreDto result = genreService.get(1);

        // Assert
        assertEquals("Action", result.getName());
    }

    @Test
    void get_shouldThrowException_whenNotFound() {
        // Arrange
        when(genreRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> genreService.get(99));
        assertEquals("Genre not found: 99", ex.getMessage());
    }

    @Test
    void create_shouldThrowException_whenNameIsBlank() {
        // Arrange
        GenreDto dto = new GenreDto(null, "  ");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> genreService.create(dto));
        assertEquals("Genre name required", ex.getMessage());
    }

    @Test
    void create_shouldThrowException_whenGenreAlreadyExists() {
        // Arrange
        GenreDto dto = new GenreDto(null, "Action");
        when(genreRepository.existsByNameIgnoreCase("Action")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> genreService.create(dto));
        assertEquals("Genre already exists: Action", ex.getMessage());
    }

    @Test
    void create_shouldSaveAndReturnGenreDto() {
        // Arrange
        GenreDto dto = new GenreDto(null, "Action");
        Genre genre = new Genre();
        genre.setGenreId(1);
        genre.setName("Action");
        when(genreRepository.existsByNameIgnoreCase("Action")).thenReturn(false);
        when(genreRepository.save(any())).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(new GenreDto(1, "Action"));

        // Act
        GenreDto result = genreService.create(dto);

        // Assert
        assertEquals("Action", result.getName());
        assertEquals(1, result.getGenreId());
    }

    @Test
    void update_shouldThrowException_whenNameIsBlank() {
        // Arrange
        GenreDto dto = new GenreDto(null, " ");
        Genre genre = new Genre();
        genre.setGenreId(1);
        genre.setName("Drama");
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> genreService.update(1, dto));
        assertEquals("Genre name required", ex.getMessage());
    }

    @Test
    void update_shouldThrowException_whenNewNameAlreadyExists() {
        // Arrange
        GenreDto dto = new GenreDto(null, "Comedy");
        Genre genre = new Genre();
        genre.setGenreId(1);
        genre.setName("Drama");
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));
        when(genreRepository.existsByNameIgnoreCase("Comedy")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> genreService.update(1, dto));
        assertEquals("Genre already exists: Comedy", ex.getMessage());
    }

    @Test
    void update_shouldUpdateAndReturnGenreDto() {
        // Arrange
        GenreDto dto = new GenreDto(null, "Thriller");
        Genre genre = new Genre();
        genre.setGenreId(1);
        genre.setName("Action");
        Genre updated = new Genre();
        updated.setGenreId(1);
        updated.setName("Thriller");
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));
        when(genreRepository.existsByNameIgnoreCase("Thriller")).thenReturn(false);
        when(genreRepository.save(any())).thenReturn(updated);
        when(genreMapper.toDto(updated)).thenReturn(new GenreDto(1, "Thriller"));

        // Act
        GenreDto result = genreService.update(1, dto);

        // Assert
        assertEquals("Thriller", result.getName());
    }

    @Test
    void delete_shouldRemoveGenre_whenExists() {
        // Arrange
        Genre genre = new Genre();
        genre.setGenreId(1);
        genre.setName("Drama");
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        // Act
        genreService.delete(1);

        // Assert
        verify(genreRepository).delete(genre);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        // Arrange
        when(genreRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> genreService.delete(99));
        assertEquals("Genre not found: 99", ex.getMessage());
    }
}

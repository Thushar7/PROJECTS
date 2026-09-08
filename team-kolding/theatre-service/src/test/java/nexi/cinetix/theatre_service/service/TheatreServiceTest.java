package nexi.cinetix.theatre_service.service;

import nexi.cinetix.theatre_service.dto.TheatreDTO;
import nexi.cinetix.theatre_service.entity.Theatre;
import nexi.cinetix.theatre_service.exception.TheatreNotFoundException;
import nexi.cinetix.theatre_service.mapper.TheatreMapper;
import nexi.cinetix.theatre_service.repository.TheatreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TheatreServiceTest {

    @Mock
    private TheatreRepository theatreRepository;

    @Mock
    private TheatreMapper theatreMapper;

    @InjectMocks
    private TheatreService theatreService;

    private Theatre sampleTheatre;
    private TheatreDTO sampleTheatreDTO;

    @BeforeEach
    void setUp() {
        sampleTheatre = Theatre.builder()
                .theatreId(1L)
                .name("Central Cinema")
                .city("Copenhagen")
                .state("Capital Region")
                .build();

        sampleTheatreDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Central Cinema")
                .city("Copenhagen")
                .state("Capital Region")
                .build();
    }

    @Test
    void list_noFilters() {
        when(theatreRepository.findAll()).thenReturn(List.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        List<TheatreDTO> result = theatreService.list(null, null);

        assertEquals(1, result.size());
        assertEquals("Central Cinema", result.get(0).getName());
        verify(theatreRepository).findAll();
        verify(theatreMapper).toDto(sampleTheatre);
    }

    @Test
    void list_withStateAndCity() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("Capital Region", "Copenhagen"))
                .thenReturn(List.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        List<TheatreDTO> result = theatreService.list("Capital Region", "Copenhagen");

        assertEquals(1, result.size());
        assertEquals("Central Cinema", result.get(0).getName());
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("Capital Region", "Copenhagen");
        verify(theatreMapper).toDto(sampleTheatre);
    }

    @Test
    void list_withStateOnly() {
        when(theatreRepository.findByStateIgnoreCase("Capital Region"))
                .thenReturn(List.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        List<TheatreDTO> result = theatreService.list("Capital Region", null);

        assertEquals(1, result.size());
        assertEquals("Central Cinema", result.get(0).getName());
        verify(theatreRepository).findByStateIgnoreCase("Capital Region");
        verify(theatreMapper).toDto(sampleTheatre);
    }

    @Test
    void list_withCityOnly() {
        when(theatreRepository.findByCityIgnoreCase("Copenhagen"))
                .thenReturn(List.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        List<TheatreDTO> result = theatreService.list(null, "Copenhagen");

        assertEquals(1, result.size());
        assertEquals("Central Cinema", result.get(0).getName());
        verify(theatreRepository).findByCityIgnoreCase("Copenhagen");
        verify(theatreMapper).toDto(sampleTheatre);
    }

    @Test
    void get_found() {
        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        TheatreDTO result = theatreService.get(1L);

        assertEquals("Central Cinema", result.getName());
        assertEquals("Copenhagen", result.getCity());
        assertEquals("Capital Region", result.getState());
        verify(theatreRepository).findById(1L);
        verify(theatreMapper).toDto(sampleTheatre);
    }

    @Test
    void get_notFound() {
        when(theatreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.get(999L));
        verify(theatreRepository).findById(999L);
        verify(theatreMapper, never()).toDto(any());
    }

    @Test
    void create_success() {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .theatreId(100L) // This should be set to null by the service
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(2L)
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(2L)
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreMapper.toEntity(argThat(dto -> dto.getTheatreId() == null)))
                .thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(2L, result.getTheatreId());
        assertEquals("New Cinema", result.getName());
        assertEquals("Aarhus", result.getCity());
        assertEquals("Central Jutland", result.getState());
        verify(theatreRepository).save(entityToSave);
        verify(theatreMapper).toEntity(argThat(dto -> dto.getTheatreId() == null));
        verify(theatreMapper).toDto(savedEntity);
    }

    @Test
    void update_success() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Updated Cinema")
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(updateDTO);

        TheatreDTO result = theatreService.update(1L, updateDTO);

        assertEquals("Updated Cinema", result.getName());
        assertEquals("Updated City", result.getCity());
        assertEquals("Updated State", result.getState());
        verify(theatreRepository).findById(1L);
        verify(theatreMapper).toDto(sampleTheatre);

        // Verify that the entity was updated
        assertEquals("Updated Cinema", sampleTheatre.getName());
        assertEquals("Updated City", sampleTheatre.getCity());
        assertEquals("Updated State", sampleTheatre.getState());
    }

    @Test
    void update_notFound() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .name("Updated Cinema")
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.update(999L, updateDTO));
        verify(theatreRepository).findById(999L);
        verify(theatreMapper, never()).toDto(any());
    }

    @Test
    void delete_success() {
        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));

        theatreService.delete(1L);

        verify(theatreRepository).findById(1L);
        verify(theatreRepository).delete(sampleTheatre);
    }

    @Test
    void delete_notFound() {
        when(theatreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.delete(999L));
        verify(theatreRepository).findById(999L);
        verify(theatreRepository, never()).delete(any());
    }

    // Additional test cases for better coverage

    @Test
    void list_emptyResult() {
        when(theatreRepository.findAll()).thenReturn(List.of());

        List<TheatreDTO> result = theatreService.list(null, null);

        assertEquals(0, result.size());
        verify(theatreRepository).findAll();
        verify(theatreMapper, never()).toDto(any());
    }

    @Test
    void list_withStateAndCity_emptyResult() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("NonExistent", "NonExistent"))
                .thenReturn(List.of());

        List<TheatreDTO> result = theatreService.list("NonExistent", "NonExistent");

        assertEquals(0, result.size());
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("NonExistent", "NonExistent");
        verify(theatreMapper, never()).toDto(any());
    }

    @Test
    void list_withStateOnly_emptyResult() {
        when(theatreRepository.findByStateIgnoreCase("NonExistent"))
                .thenReturn(List.of());

        List<TheatreDTO> result = theatreService.list("NonExistent", null);

        assertEquals(0, result.size());
        verify(theatreRepository).findByStateIgnoreCase("NonExistent");
        verify(theatreMapper, never()).toDto(any());
    }

    @Test
    void list_withCityOnly_emptyResult() {
        when(theatreRepository.findByCityIgnoreCase("NonExistent"))
                .thenReturn(List.of());

        List<TheatreDTO> result = theatreService.list(null, "NonExistent");

        assertEquals(0, result.size());
        verify(theatreRepository).findByCityIgnoreCase("NonExistent");
        verify(theatreMapper, never()).toDto(any());
    }

    @Test
    void list_multipleTheatres() {
        Theatre theatre2 = Theatre.builder()
                .theatreId(2L)
                .name("Cinema 2")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO theatreDTO2 = TheatreDTO.builder()
                .theatreId(2L)
                .name("Cinema 2")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreRepository.findAll()).thenReturn(List.of(sampleTheatre, theatre2));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);
        when(theatreMapper.toDto(theatre2)).thenReturn(theatreDTO2);

        List<TheatreDTO> result = theatreService.list(null, null);

        assertEquals(2, result.size());
        assertEquals("Central Cinema", result.get(0).getName());
        assertEquals("Cinema 2", result.get(1).getName());
        verify(theatreRepository).findAll();
        verify(theatreMapper).toDto(sampleTheatre);
        verify(theatreMapper).toDto(theatre2);
    }

    @Test
    void create_withNullId() {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .theatreId(null) // Already null
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(3L)
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(3L)
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreMapper.toEntity(inputDTO)).thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(3L, result.getTheatreId());
        assertEquals("New Cinema", result.getName());
        assertEquals("Aarhus", result.getCity());
        assertEquals("Central Jutland", result.getState());
        verify(theatreRepository).save(entityToSave);
        verify(theatreMapper).toEntity(inputDTO);
        verify(theatreMapper).toDto(savedEntity);
    }

    @Test
    void update_partialUpdate() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Partially Updated Cinema")
                .city("Copenhagen") // Same city
                .state("Capital Region") // Same state
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(updateDTO);

        TheatreDTO result = theatreService.update(1L, updateDTO);

        assertEquals("Partially Updated Cinema", result.getName());
        assertEquals("Copenhagen", result.getCity());
        assertEquals("Capital Region", result.getState());
        verify(theatreRepository).findById(1L);
        verify(theatreMapper).toDto(sampleTheatre);
    }

    // Additional comprehensive test cases for 90%+ coverage

    @Test
    void list_withEmptyState() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("", "Copenhagen"))
                .thenReturn(List.of()); // Empty string state will be passed to repository, return empty list

        List<TheatreDTO> result = theatreService.list("", "Copenhagen");

        assertEquals(0, result.size()); // Should be 0, not 1
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("", "Copenhagen");
    }

    @Test
    void list_withEmptyCity() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("Capital Region", ""))
                .thenReturn(List.of()); // Empty string city will be passed to repository, return empty list

        List<TheatreDTO> result = theatreService.list("Capital Region", "");

        assertEquals(0, result.size()); // Should be 0, not 1
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("Capital Region", "");
    }

    @Test
    void list_withWhitespaceState() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("   ", "Copenhagen"))
                .thenReturn(List.of()); // Whitespace state will be passed to repository, return empty list

        List<TheatreDTO> result = theatreService.list("   ", "Copenhagen");

        assertEquals(0, result.size()); // Should be 0, not 1
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("   ", "Copenhagen");
    }

    @Test
    void list_withWhitespaceCity() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("Capital Region", "   "))
                .thenReturn(List.of()); // Whitespace city will be passed to repository, return empty list

        List<TheatreDTO> result = theatreService.list("Capital Region", "   ");

        assertEquals(0, result.size()); // Should be 0, not 1
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("Capital Region", "   ");
    }

    @Test
    void list_withBothEmptyStrings() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("", ""))
                .thenReturn(List.of()); // Both empty strings will be passed to repository, return empty list

        List<TheatreDTO> result = theatreService.list("", "");

        assertEquals(0, result.size()); // Should be 0, not 1
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("", "");
    }

    @Test
    void list_caseInsensitiveStateAndCity() {
        when(theatreRepository.findByStateIgnoreCaseAndCityIgnoreCase("capital region", "copenhagen"))
                .thenReturn(List.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        List<TheatreDTO> result = theatreService.list("capital region", "copenhagen");

        assertEquals(1, result.size());
        verify(theatreRepository).findByStateIgnoreCaseAndCityIgnoreCase("capital region", "copenhagen");
    }

    @Test
    void list_caseInsensitiveStateOnly() {
        when(theatreRepository.findByStateIgnoreCase("CAPITAL REGION"))
                .thenReturn(List.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        List<TheatreDTO> result = theatreService.list("CAPITAL REGION", null);

        assertEquals(1, result.size());
        verify(theatreRepository).findByStateIgnoreCase("CAPITAL REGION");
    }

    @Test
    void list_caseInsensitiveCityOnly() {
        when(theatreRepository.findByCityIgnoreCase("COPENHAGEN"))
                .thenReturn(List.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        List<TheatreDTO> result = theatreService.list(null, "COPENHAGEN");

        assertEquals(1, result.size());
        verify(theatreRepository).findByCityIgnoreCase("COPENHAGEN");
    }

    @Test
    void get_withZeroId() {
        when(theatreRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.get(0L));
        verify(theatreRepository).findById(0L);
    }

    @Test
    void get_withNegativeId() {
        when(theatreRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.get(-1L));
        verify(theatreRepository).findById(-1L);
    }

    @Test
    void get_withMaxLongId() {
        when(theatreRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.get(Long.MAX_VALUE));
        verify(theatreRepository).findById(Long.MAX_VALUE);
    }

    @Test
    void create_withEmptyName() {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name("")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name("")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(4L)
                .name("")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(4L)
                .name("")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreMapper.toEntity(inputDTO)).thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(4L, result.getTheatreId());
        assertEquals("", result.getName());
        verify(theatreRepository).save(entityToSave);
    }

    @Test
    void create_withNullName() {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name(null)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name(null)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(5L)
                .name(null)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(5L)
                .name(null)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreMapper.toEntity(inputDTO)).thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(5L, result.getTheatreId());
        assertNull(result.getName());
        verify(theatreRepository).save(entityToSave);
    }

    @Test
    void create_withNullCity() {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name("Cinema without City")
                .city(null)
                .state("Central Jutland")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name("Cinema without City")
                .city(null)
                .state("Central Jutland")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(6L)
                .name("Cinema without City")
                .city(null)
                .state("Central Jutland")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(6L)
                .name("Cinema without City")
                .city(null)
                .state("Central Jutland")
                .build();

        when(theatreMapper.toEntity(inputDTO)).thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(6L, result.getTheatreId());
        assertNull(result.getCity());
        verify(theatreRepository).save(entityToSave);
    }

    @Test
    void create_withNullState() {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name("Cinema without State")
                .city("Aarhus")
                .state(null)
                .build();

        Theatre entityToSave = Theatre.builder()
                .name("Cinema without State")
                .city("Aarhus")
                .state(null)
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(7L)
                .name("Cinema without State")
                .city("Aarhus")
                .state(null)
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(7L)
                .name("Cinema without State")
                .city("Aarhus")
                .state(null)
                .build();

        when(theatreMapper.toEntity(inputDTO)).thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(7L, result.getTheatreId());
        assertNull(result.getState());
        verify(theatreRepository).save(entityToSave);
    }

    @Test
    void create_withWhitespaceName() {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name("   ")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name("   ")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(8L)
                .name("   ")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(8L)
                .name("   ")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreMapper.toEntity(inputDTO)).thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(8L, result.getTheatreId());
        assertEquals("   ", result.getName());
        verify(theatreRepository).save(entityToSave);
    }

    @Test
    void create_withLongName() {
        String longName = "A".repeat(255); // Very long name
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name(longName)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name(longName)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(9L)
                .name(longName)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(9L)
                .name(longName)
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreMapper.toEntity(inputDTO)).thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        assertEquals(9L, result.getTheatreId());
        assertEquals(longName, result.getName());
        verify(theatreRepository).save(entityToSave);
    }

    @Test
    void update_withNullName() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name(null)
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(updateDTO);

        TheatreDTO result = theatreService.update(1L, updateDTO);

        assertNull(result.getName());
        verify(theatreRepository).findById(1L);
        assertNull(sampleTheatre.getName());
    }

    @Test
    void update_withEmptyName() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("")
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(updateDTO);

        TheatreDTO result = theatreService.update(1L, updateDTO);

        assertEquals("", result.getName());
        verify(theatreRepository).findById(1L);
        assertEquals("", sampleTheatre.getName());
    }

    @Test
    void update_withNullCity() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Updated Cinema")
                .city(null)
                .state("Updated State")
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(updateDTO);

        TheatreDTO result = theatreService.update(1L, updateDTO);

        assertNull(result.getCity());
        verify(theatreRepository).findById(1L);
        assertNull(sampleTheatre.getCity());
    }

    @Test
    void update_withNullState() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Updated Cinema")
                .city("Updated City")
                .state(null)
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(updateDTO);

        TheatreDTO result = theatreService.update(1L, updateDTO);

        assertNull(result.getState());
        verify(theatreRepository).findById(1L);
        assertNull(sampleTheatre.getState());
    }

    @Test
    void update_sameValues() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Central Cinema") // Same name
                .city("Copenhagen") // Same city
                .state("Capital Region") // Same state
                .build();

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(updateDTO);

        TheatreDTO result = theatreService.update(1L, updateDTO);

        assertEquals("Central Cinema", result.getName());
        assertEquals("Copenhagen", result.getCity());
        assertEquals("Capital Region", result.getState());
        verify(theatreRepository).findById(1L);
    }

    @Test
    void update_withZeroId() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .name("Updated Cinema")
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.update(0L, updateDTO));
        verify(theatreRepository).findById(0L);
    }

    @Test
    void update_withNegativeId() {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .name("Updated Cinema")
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.update(-1L, updateDTO));
        verify(theatreRepository).findById(-1L);
    }

    @Test
    void delete_withZeroId() {
        when(theatreRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.delete(0L));
        verify(theatreRepository).findById(0L);
        verify(theatreRepository, never()).delete(any());
    }

    @Test
    void delete_withNegativeId() {
        when(theatreRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.delete(-1L));
        verify(theatreRepository).findById(-1L);
        verify(theatreRepository, never()).delete(any());
    }

    @Test
    void delete_withMaxLongId() {
        when(theatreRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(TheatreNotFoundException.class, () -> theatreService.delete(Long.MAX_VALUE));
        verify(theatreRepository).findById(Long.MAX_VALUE);
        verify(theatreRepository, never()).delete(any());
    }

    @Test
    void list_largeDataset() {
        // Test with multiple theatres from different states and cities
        Theatre theatre2 = Theatre.builder()
                .theatreId(2L)
                .name("Cinema 2")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        Theatre theatre3 = Theatre.builder()
                .theatreId(3L)
                .name("Cinema 3")
                .city("Odense")
                .state("Southern Denmark")
                .build();

        TheatreDTO dto2 = TheatreDTO.builder()
                .theatreId(2L)
                .name("Cinema 2")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO dto3 = TheatreDTO.builder()
                .theatreId(3L)
                .name("Cinema 3")
                .city("Odense")
                .state("Southern Denmark")
                .build();

        when(theatreRepository.findAll()).thenReturn(List.of(sampleTheatre, theatre2, theatre3));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);
        when(theatreMapper.toDto(theatre2)).thenReturn(dto2);
        when(theatreMapper.toDto(theatre3)).thenReturn(dto3);

        List<TheatreDTO> result = theatreService.list(null, null);

        assertEquals(3, result.size());
        verify(theatreRepository).findAll();
        verify(theatreMapper, times(3)).toDto(any(Theatre.class));
    }

    @Test
    void find_privateMethodCoverage() {
        // This tests the private find method indirectly through get
        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreMapper.toDto(sampleTheatre)).thenReturn(sampleTheatreDTO);

        // Call get which uses the private find method
        theatreService.get(1L);

        verify(theatreRepository).findById(1L);
    }

    @Test
    void create_idSetToNull() {
        // Verify that the service properly sets ID to null before saving
        TheatreDTO inputDTO = TheatreDTO.builder()
                .theatreId(999L) // Non-null ID
                .name("Test Cinema")
                .city("Test City")
                .state("Test State")
                .build();

        Theatre entityToSave = Theatre.builder()
                .name("Test Cinema")
                .city("Test City")
                .state("Test State")
                .build();

        Theatre savedEntity = Theatre.builder()
                .theatreId(10L)
                .name("Test Cinema")
                .city("Test City")
                .state("Test State")
                .build();

        TheatreDTO expectedResult = TheatreDTO.builder()
                .theatreId(10L)
                .name("Test Cinema")
                .city("Test City")
                .state("Test State")
                .build();

        when(theatreMapper.toEntity(argThat(dto -> dto.getTheatreId() == null)))
                .thenReturn(entityToSave);
        when(theatreRepository.save(entityToSave)).thenReturn(savedEntity);
        when(theatreMapper.toDto(savedEntity)).thenReturn(expectedResult);

        TheatreDTO result = theatreService.create(inputDTO);

        // Verify the original DTO had its ID set to null
        assertNull(inputDTO.getTheatreId());
        assertEquals(10L, result.getTheatreId());
        verify(theatreMapper).toEntity(argThat(dto -> dto.getTheatreId() == null));
    }
}

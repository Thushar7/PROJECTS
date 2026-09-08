package nexi.cinetix.movie_service.service.impl;

import nexi.cinetix.movie_service.dto.LanguageDto;
import nexi.cinetix.movie_service.entity.Language;
import nexi.cinetix.movie_service.exception.ResourceNotFoundException;
import nexi.cinetix.movie_service.mapper.LanguageMapper;
import nexi.cinetix.movie_service.repository.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageServiceImplTest {

    private LanguageRepository languageRepository;
    private LanguageMapper languageMapper;
    private LanguageServiceImpl languageService;

    @BeforeEach
    void setUp() {
        languageRepository = mock(LanguageRepository.class);
        languageMapper = mock(LanguageMapper.class);
        languageService = new LanguageServiceImpl(languageRepository, languageMapper);
    }

    @Test
    void getAll_shouldReturnListOfLanguages() {
        Language lang1 = new Language();
        lang1.setLanguageId(1);
        lang1.setName("English");
        Language lang2 = new Language();
        lang2.setLanguageId(2);
        lang2.setName("Hindi");

        when(languageRepository.findAll()).thenReturn(Arrays.asList(lang1, lang2));
        when(languageMapper.toDto(lang1)).thenReturn(new LanguageDto(1, "English"));
        when(languageMapper.toDto(lang2)).thenReturn(new LanguageDto(2, "Hindi"));

        List<LanguageDto> result = languageService.getAll();

        assertEquals(2, result.size());
        assertEquals("English", result.get(0).getName());
        assertEquals("Hindi", result.get(1).getName());
    }

    @Test
    void get_shouldReturnLanguageDto_whenFound() {
        Language lang = new Language();
        lang.setLanguageId(1);
        lang.setName("English");

        when(languageRepository.findById(1)).thenReturn(Optional.of(lang));
        when(languageMapper.toDto(lang)).thenReturn(new LanguageDto(1, "English"));

        LanguageDto result = languageService.get(1);

        assertEquals("English", result.getName());
    }

    @Test
    void get_shouldThrowException_whenNotFound() {
        when(languageRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> languageService.get(99));
        assertEquals("Language not found: 99", ex.getMessage());
    }

    @Test
    void create_shouldThrowException_whenNameIsBlank() {
        LanguageDto dto = new LanguageDto(null, "  ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> languageService.create(dto));
        assertEquals("Language name required", ex.getMessage());
    }

    @Test
    void create_shouldThrowException_whenLanguageAlreadyExists() {
        LanguageDto dto = new LanguageDto(null, "English");

        when(languageRepository.existsByNameIgnoreCase("English")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> languageService.create(dto));
        assertEquals("Language already exists: English", ex.getMessage());
    }

    @Test
    void create_shouldSaveAndReturnLanguageDto() {
        LanguageDto dto = new LanguageDto(null, "English");
        Language lang = new Language();
        lang.setLanguageId(1);
        lang.setName("English");

        when(languageRepository.existsByNameIgnoreCase("English")).thenReturn(false);
        when(languageRepository.save(any())).thenReturn(lang);
        when(languageMapper.toDto(lang)).thenReturn(new LanguageDto(1, "English"));

        LanguageDto result = languageService.create(dto);

        assertEquals("English", result.getName());
        assertEquals(1, result.getLanguageId());
    }

    @Test
    void update_shouldThrowException_whenNameIsBlank() {
        LanguageDto dto = new LanguageDto(null, " ");
        Language lang = new Language();
        lang.setLanguageId(1);
        lang.setName("English");

        when(languageRepository.findById(1)).thenReturn(Optional.of(lang));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> languageService.update(1, dto));
        assertEquals("Language name required", ex.getMessage());
    }

    @Test
    void update_shouldThrowException_whenNewNameAlreadyExists() {
        LanguageDto dto = new LanguageDto(null, "Hindi");
        Language lang = new Language();
        lang.setLanguageId(1);
        lang.setName("English");

        when(languageRepository.findById(1)).thenReturn(Optional.of(lang));
        when(languageRepository.existsByNameIgnoreCase("Hindi")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> languageService.update(1, dto));
        assertEquals("Language already exists: Hindi", ex.getMessage());
    }

    @Test
    void update_shouldUpdateAndReturnLanguageDto() {
        LanguageDto dto = new LanguageDto(null, "Spanish");
        Language lang = new Language();
        lang.setLanguageId(1);
        lang.setName("English");
        Language updated = new Language();
        updated.setLanguageId(1);
        updated.setName("Spanish");

        when(languageRepository.findById(1)).thenReturn(Optional.of(lang));
        when(languageRepository.existsByNameIgnoreCase("Spanish")).thenReturn(false);
        when(languageRepository.save(any())).thenReturn(updated);
        when(languageMapper.toDto(updated)).thenReturn(new LanguageDto(1, "Spanish"));

        LanguageDto result = languageService.update(1, dto);

        assertEquals("Spanish", result.getName());
    }

    @Test
    void delete_shouldRemoveLanguage_whenExists() {
        Language lang = new Language();
        lang.setLanguageId(1);
        lang.setName("English");

        when(languageRepository.findById(1)).thenReturn(Optional.of(lang));

        languageService.delete(1);

        verify(languageRepository).delete(lang);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        when(languageRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> languageService.delete(99));
        assertEquals("Language not found: 99", ex.getMessage());
    }
}
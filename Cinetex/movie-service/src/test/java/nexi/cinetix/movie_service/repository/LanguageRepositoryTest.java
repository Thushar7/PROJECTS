package nexi.cinetix.movie_service.repository;

import nexi.cinetix.movie_service.entity.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LanguageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LanguageRepository languageRepository;

    @Test
    void languageRepository_findByNameIgnoreCase() {
        // Given
        Language language = Language.builder()
                .name("English")
                .build();

        entityManager.persistAndFlush(language);

        // When
        Optional<Language> result = languageRepository.findByNameIgnoreCase("english");

        // Then
        assertTrue(result.isPresent());
        assertEquals("English", result.get().getName());
    }

    @Test
    void languageRepository_findByNameIgnoreCaseUpperCase() {
        // Given
        Language language = Language.builder()
                .name("spanish")
                .build();

        entityManager.persistAndFlush(language);

        // When
        Optional<Language> result = languageRepository.findByNameIgnoreCase("SPANISH");

        // Then
        assertTrue(result.isPresent());
        assertEquals("spanish", result.get().getName());
    }

    @Test
    void languageRepository_findByNameIgnoreCaseNotFound() {
        // Given
        Language language = Language.builder()
                .name("French")
                .build();

        entityManager.persistAndFlush(language);

        // When
        Optional<Language> result = languageRepository.findByNameIgnoreCase("German");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void languageRepository_existsByNameIgnoreCase() {
        // Given
        Language language = Language.builder()
                .name("Italian")
                .build();

        entityManager.persistAndFlush(language);

        // When & Then
        assertTrue(languageRepository.existsByNameIgnoreCase("italian"));
        assertTrue(languageRepository.existsByNameIgnoreCase("ITALIAN"));
        assertTrue(languageRepository.existsByNameIgnoreCase("Italian"));
        assertFalse(languageRepository.existsByNameIgnoreCase("Portuguese"));
    }

    @Test
    void languageRepository_findAll() {
        // Given
        Language language1 = Language.builder().name("English").build();
        Language language2 = Language.builder().name("French").build();

        entityManager.persistAndFlush(language1);
        entityManager.persistAndFlush(language2);

        // When
        List<Language> result = languageRepository.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void languageRepository_save() {
        // Given
        Language language = Language.builder()
                .name("Japanese")
                .build();

        // When
        Language savedLanguage = languageRepository.save(language);

        // Then
        assertNotNull(savedLanguage.getLanguageId());
        assertEquals("Japanese", savedLanguage.getName());
    }

    @Test
    void languageRepository_deleteById() {
        // Given
        Language language = Language.builder()
                .name("Korean")
                .build();

        Language savedLanguage = entityManager.persistAndFlush(language);
        Integer languageId = savedLanguage.getLanguageId();

        // When
        languageRepository.deleteById(languageId);

        // Then
        Optional<Language> result = languageRepository.findById(languageId);
        assertFalse(result.isPresent());
    }
}

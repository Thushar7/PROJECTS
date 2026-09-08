package nexi.cinetix.movie_service.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

    @Test
    void language_builderPattern() {
        Language language = Language.builder()
                .languageId(1)
                .name("English")
                .build();

        assertEquals(1, language.getLanguageId());
        assertEquals("English", language.getName());
        assertNotNull(language.getMovieLanguages());
    }

    @Test
    void language_settersAndGetters() {
        Language language = new Language();

        language.setLanguageId(5);
        language.setName("Spanish");

        assertEquals(5, language.getLanguageId());
        assertEquals("Spanish", language.getName());
    }

    @Test
    void language_defaultCollections() {
        Language language = Language.builder().build();

        assertNotNull(language.getMovieLanguages());
        assertTrue(language.getMovieLanguages().isEmpty());
    }

    @Test
    void language_noArgsConstructor() {
        Language language = new Language();
        assertNotNull(language);
        assertNull(language.getLanguageId());
        assertNull(language.getName());
    }

    @Test
    void language_allArgsConstructor() {
        Language language = new Language(10, "French", new HashSet<>());

        assertEquals(10, language.getLanguageId());
        assertEquals("French", language.getName());
        assertNotNull(language.getMovieLanguages());
    }
}

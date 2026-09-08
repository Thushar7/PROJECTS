package nexi.cinetix.movie_service.mapper;

import nexi.cinetix.movie_service.dto.LanguageDto;
import nexi.cinetix.movie_service.entity.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageMapperTest {

    private final LanguageMapper mapper = new LanguageMapper();

    @Test
    void toDto_shouldMapAllFields() {
        Language language = Language.builder()
                .languageId(10)
                .name("English")
                .build();

        LanguageDto dto = mapper.toDto(language);

        assertNotNull(dto);
        assertEquals(10, dto.getLanguageId());
        assertEquals("English", dto.getName());
    }

    @Test
    void toDto_shouldHandleSpecialCharacters() {
        Language language = Language.builder()
                .languageId(11)
                .name("Français (CA)")
                .build();

        LanguageDto dto = mapper.toDto(language);

        assertEquals(11, dto.getLanguageId());
        assertEquals("Français (CA)", dto.getName());
    }

    @Test
    void toDto_nullInput_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.toDto(null));
    }
}

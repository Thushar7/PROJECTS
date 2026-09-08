package nexi.cinetix.movie_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageDtoTest {

    @Test
    void languageDto_builderPattern() {
        LanguageDto dto = LanguageDto.builder()
                .languageId(1)
                .name("English")
                .build();

        assertEquals(1, dto.getLanguageId());
        assertEquals("English", dto.getName());
    }

    @Test
    void languageDto_settersAndGetters() {
        LanguageDto dto = new LanguageDto();

        dto.setLanguageId(5);
        dto.setName("Spanish");

        assertEquals(5, dto.getLanguageId());
        assertEquals("Spanish", dto.getName());
    }

    @Test
    void languageDto_equalsAndHashCode() {
        LanguageDto dto1 = LanguageDto.builder()
                .languageId(1)
                .name("French")
                .build();

        LanguageDto dto2 = LanguageDto.builder()
                .languageId(1)
                .name("French")
                .build();

        LanguageDto dto3 = LanguageDto.builder()
                .languageId(2)
                .name("German")
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void languageDto_toString() {
        LanguageDto dto = LanguageDto.builder()
                .languageId(1)
                .name("Italian")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("languageId"));
        assertTrue(toString.contains("name"));
    }

    @Test
    void languageDto_noArgsConstructor() {
        LanguageDto dto = new LanguageDto();
        assertNotNull(dto);
        assertNull(dto.getLanguageId());
        assertNull(dto.getName());
    }

    @Test
    void languageDto_allArgsConstructor() {
        LanguageDto dto = new LanguageDto(10, "Japanese");

        assertEquals(10, dto.getLanguageId());
        assertEquals("Japanese", dto.getName());
    }
}

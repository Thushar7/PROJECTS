package nexi.cinetix.movie_service.controller;

import nexi.cinetix.movie_service.dto.LanguageDto;
import nexi.cinetix.movie_service.service.LanguageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LanguageController.class)
class LanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LanguageService languageService;

    @Test
    void list_shouldReturnAllLanguages() throws Exception {
        // Arrange
        LanguageDto dto = new LanguageDto(1, "English");
        when(languageService.getAll()).thenReturn(List.of(dto));
        mockMvc.perform(get("/movies/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].languageId").value(1))
                .andExpect(jsonPath("$[0].name").value("English"));
    }

    @Test
    void get_shouldReturnLanguageById() throws Exception {
        // Arrange
        LanguageDto dto = new LanguageDto(1, "English");
        when(languageService.get(anyInt())).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/movies/languages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.languageId").value(1))
                .andExpect(jsonPath("$.name").value("English"));
    }

    @Test
    void create_shouldReturnCreatedLanguage() throws Exception {
        // Arrange
        LanguageDto dto = new LanguageDto(2, "Spanish");
        when(languageService.create(any(LanguageDto.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/movies/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Spanish\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.languageId").value(2))
                .andExpect(jsonPath("$.name").value("Spanish"));
    }

    @Test
    void update_shouldReturnUpdatedLanguage() throws Exception {
        // Arrange
        LanguageDto dto = new LanguageDto(1, "Hindi");
        when(languageService.update(anyInt(), any(LanguageDto.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(put("/movies/languages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hindi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.languageId").value(1))
                .andExpect(jsonPath("$.name").value("Hindi"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(languageService).delete(anyInt());

        // Act & Assert
        mockMvc.perform(delete("/movies/languages/1"))
                .andExpect(status().isNoContent());
    }
}
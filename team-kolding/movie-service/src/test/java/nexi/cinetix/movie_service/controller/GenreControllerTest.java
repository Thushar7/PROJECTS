package nexi.cinetix.movie_service.controller;

import nexi.cinetix.movie_service.dto.GenreDto;
import nexi.cinetix.movie_service.service.GenreService;
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

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    void list_shouldReturnAllGenres() throws Exception {
        // Arrange
        GenreDto dto = new GenreDto(1, "Action");
        when(genreService.getAll()).thenReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/movies/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genreId").value(1))
                .andExpect(jsonPath("$[0].name").value("Action"));
    }

    @Test
    void get_shouldReturnGenreById() throws Exception {
        // Arrange
        GenreDto dto = new GenreDto(1, "Action");
        when(genreService.get(anyInt())).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/movies/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(1))
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void create_shouldReturnCreatedGenre() throws Exception {
        // Arrange
        GenreDto dto = new GenreDto(2, "Comedy");
        when(genreService.create(any(GenreDto.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/movies/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Comedy\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.genreId").value(2))
                .andExpect(jsonPath("$.name").value("Comedy"));
    }

    @Test
    void update_shouldReturnUpdatedGenre() throws Exception {
        // Arrange
        GenreDto dto = new GenreDto(1, "Thriller");
        when(genreService.update(anyInt(), any(GenreDto.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(put("/movies/genres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Thriller\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(1))
                .andExpect(jsonPath("$.name").value("Thriller"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(genreService).delete(anyInt());

        // Act & Assert
        mockMvc.perform(delete("/movies/genres/1"))
                .andExpect(status().isNoContent());
    }
}
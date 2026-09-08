package nexi.cinetix.movie_service.controller;

import nexi.cinetix.movie_service.dto.MovieDto;
import nexi.cinetix.movie_service.dto.PageResponse;
import nexi.cinetix.movie_service.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void list_shouldReturnAllMovies() throws Exception {
        when(movieService.getAll()).thenReturn(List.of(new MovieDto()));
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk());
    }

    @Test
    void list_shouldReturnFilteredMoviesWithPagination() throws Exception {
        when(movieService.getAllFiltered(anyList(), anyList(), anyInt())).thenReturn(List.of(new MovieDto()));
        mockMvc.perform(get("/movies")
                        .param("genres", "1,2")
                        .param("languages", "3")
                        .param("page", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void get_shouldReturnMovieById() throws Exception {
        when(movieService.get(anyInt())).thenReturn(new MovieDto());
        mockMvc.perform(get("/movies/1"))
                .andExpect(status().isOk());
    }

    @Test
    void search_shouldReturnMoviesByTitle() throws Exception {
        when(movieService.searchByTitle(anyString())).thenReturn(List.of(new MovieDto()));
        mockMvc.perform(get("/movies/search").param("title", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnCreatedMovie() throws Exception {
        when(movieService.create(any())).thenReturn(new MovieDto());
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    void update_shouldReturnUpdatedMovie() throws Exception {
        when(movieService.update(anyInt(), any())).thenReturn(new MovieDto());
        mockMvc.perform(put("/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/movies/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void addGenres_shouldReturnUpdatedMovie() throws Exception {
        when(movieService.addGenres(anyInt(), anyList())).thenReturn(new MovieDto());
        mockMvc.perform(post("/movies/1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2]"))
                .andExpect(status().isOk());
    }

    @Test
    void removeGenre_shouldReturnUpdatedMovie() throws Exception {
        when(movieService.removeGenre(anyInt(), anyInt())).thenReturn(new MovieDto());
        mockMvc.perform(delete("/movies/1/genres/2"))
                .andExpect(status().isOk());
    }

    @Test
    void addLanguages_shouldReturnUpdatedMovie() throws Exception {
        when(movieService.addLanguages(anyInt(), anyList())).thenReturn(new MovieDto());
        mockMvc.perform(post("/movies/1/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2]"))
                .andExpect(status().isOk());
    }

    @Test
    void removeLanguage_shouldReturnUpdatedMovie() throws Exception {
        when(movieService.removeLanguage(anyInt(), anyInt())).thenReturn(new MovieDto());
        mockMvc.perform(delete("/movies/1/languages/2"))
                .andExpect(status().isOk());
    }

    @Test
    void uploadPoster_shouldReturnSuccessResponse() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "poster.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());
        mockMvc.perform(multipart("/movies/1/poster").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Poster uploaded"));
    }

    @Test
    void uploadPoster_shouldReturnBadRequest_whenFileMissing() throws Exception {
        mockMvc.perform(post("/movies/1/poster"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No file part present or file empty"));
    }

    @Test
    void getPoster_shouldReturnPosterData() throws Exception {
        when(movieService.getPosterData(anyInt())).thenReturn("image".getBytes());
        when(movieService.getPosterContentType(anyInt())).thenReturn(MediaType.IMAGE_JPEG_VALUE.toString());
        mockMvc.perform(get("/movies/1/poster"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=movie-1-poster"));
    }

    @Test
    void getPoster_shouldReturnNotFound_whenNoData() throws Exception {
        when(movieService.getPosterData(anyInt())).thenReturn(new byte[0]);
        mockMvc.perform(get("/movies/1/poster"))
                .andExpect(status().isNotFound());
    }

    @Test
    void page_shouldReturnPagedMovies() throws Exception {
        when(movieService.getPage(anyInt(), anyInt(), anyList(), anyList())).thenReturn(new PageResponse<>());
        mockMvc.perform(get("/movies/page")
                        .param("page", "0")
                        .param("size", "10")
                        .param("genres", "1")
                        .param("languages", "2"))
                .andExpect(status().isOk());
    }
}
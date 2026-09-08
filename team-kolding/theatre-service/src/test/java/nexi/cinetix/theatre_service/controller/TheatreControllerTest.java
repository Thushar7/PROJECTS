package nexi.cinetix.theatre_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nexi.cinetix.theatre_service.dto.ShowtimeDTO;
import nexi.cinetix.theatre_service.dto.TheatreDTO;

import nexi.cinetix.theatre_service.exception.TheatreNotFoundException;
import nexi.cinetix.theatre_service.service.TheatreService;
import nexi.cinetix.theatre_service.service.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TheatreController.class)
class TheatreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TheatreService theatreService;

    @MockitoBean
    private ShowtimeService showtimeService;

    @Autowired
    private ObjectMapper objectMapper;

    private TheatreDTO sampleTheatreDTO;
    private ShowtimeDTO sampleShowtimeDTO;

    @BeforeEach
    void setUp() {
        sampleTheatreDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Central Cinema")
                .city("Copenhagen")
                .state("Capital Region")
                .build();

        sampleShowtimeDTO = ShowtimeDTO.builder()
                .showtimeId(1L)
                .movieId(100L)
                .theatreId(1L)
                .startTime(LocalDateTime.of(2025, 10, 15, 19, 30))
                .build();
    }

    // =============== LIST THEATRES TESTS ===============

    @Test
    @WithMockUser
    void listTheatres_noFilters_shouldReturnAllTheatres() throws Exception {
        when(theatreService.list(null, null)).thenReturn(List.of(sampleTheatreDTO));

        mockMvc.perform(get("/theatres")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].theatreId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Central Cinema"))
                .andExpect(jsonPath("$[0].city").value("Copenhagen"))
                .andExpect(jsonPath("$[0].state").value("Capital Region"));

        verify(theatreService).list(null, null);
    }

    @Test
    @WithMockUser
    void listTheatres_withStateAndCity_shouldReturnFilteredTheatres() throws Exception {
        when(theatreService.list("Capital Region", "Copenhagen")).thenReturn(List.of(sampleTheatreDTO));

        mockMvc.perform(get("/theatres")
                        .param("state", "Capital Region")
                        .param("city", "Copenhagen")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].theatreId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Central Cinema"));

        verify(theatreService).list("Capital Region", "Copenhagen");
    }

    @Test
    @WithMockUser
    void listTheatres_withStateOnly_shouldReturnFilteredTheatres() throws Exception {
        when(theatreService.list("Capital Region", null)).thenReturn(List.of(sampleTheatreDTO));

        mockMvc.perform(get("/theatres")
                        .param("state", "Capital Region")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].theatreId").value(1L));

        verify(theatreService).list("Capital Region", null);
    }

    @Test
    @WithMockUser
    void listTheatres_withCityOnly_shouldReturnFilteredTheatres() throws Exception {
        when(theatreService.list(null, "Copenhagen")).thenReturn(List.of(sampleTheatreDTO));

        mockMvc.perform(get("/theatres")
                        .param("city", "Copenhagen")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].theatreId").value(1L));

        verify(theatreService).list(null, "Copenhagen");
    }

    @Test
    @WithMockUser
    void listTheatres_emptyResult_shouldReturnEmptyArray() throws Exception {
        when(theatreService.list(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/theatres")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(theatreService).list(null, null);
    }

    // =============== GET THEATRE TESTS ===============

    @Test
    @WithMockUser
    void getTheatre_validId_shouldReturnTheatre() throws Exception {
        when(theatreService.get(1L)).thenReturn(sampleTheatreDTO);

        mockMvc.perform(get("/theatres/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.theatreId").value(1L))
                .andExpect(jsonPath("$.name").value("Central Cinema"))
                .andExpect(jsonPath("$.city").value("Copenhagen"))
                .andExpect(jsonPath("$.state").value("Capital Region"));

        verify(theatreService).get(1L);
    }

    @Test
    @WithMockUser
    void getTheatre_notFound_shouldReturn404() throws Exception {
        when(theatreService.get(999L)).thenThrow(new TheatreNotFoundException(999L));

        mockMvc.perform(get("/theatres/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(theatreService).get(999L);
    }

    // =============== SHOWTIMES TESTS ===============

    @Test
    @WithMockUser
    void showtimesByMovie_withMovieIdParam_shouldReturnShowtimes() throws Exception {
        when(showtimeService.list(100L, null, null)).thenReturn(List.of(sampleShowtimeDTO));

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].showtimeId").value(1L))
                .andExpect(jsonPath("$[0].movieId").value(100L))
                .andExpect(jsonPath("$[0].theatreId").value(1L));

        verify(showtimeService).list(100L, null, null);
    }

    @Test
    @WithMockUser
    void showtimesByMovie_withMovieIdAltParam_shouldReturnShowtimes() throws Exception {
        when(showtimeService.list(100L, null, null)).thenReturn(List.of(sampleShowtimeDTO));

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movie_id", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].movieId").value(100L));

        verify(showtimeService).list(100L, null, null);
    }

    @Test
    @WithMockUser
    void showtimesByMovie_withBothMovieIdParams_shouldPreferMovieId() throws Exception {
        when(showtimeService.list(100L, null, null)).thenReturn(List.of(sampleShowtimeDTO));

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", "100")
                        .param("movie_id", "200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(showtimeService).list(100L, null, null);
    }

    @Test
    @WithMockUser
    void showtimesByMovie_withAllFilters_shouldReturnFilteredShowtimes() throws Exception {
        LocalDate date = LocalDate.of(2025, 10, 15);
        when(showtimeService.list(100L, 1L, date)).thenReturn(List.of(sampleShowtimeDTO));

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", "100")
                        .param("theatreId", "1")
                        .param("date", "2025-10-15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(showtimeService).list(100L, 1L, date);
    }

    @Test
    @WithMockUser
    void showtimesByMovie_withTheatreIdOnly_shouldReturnFilteredShowtimes() throws Exception {
        when(showtimeService.list(100L, 1L, null)).thenReturn(List.of(sampleShowtimeDTO));

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", "100")
                        .param("theatreId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(showtimeService).list(100L, 1L, null);
    }

    @Test
    @WithMockUser
    void showtimesByMovie_withDateOnly_shouldReturnFilteredShowtimes() throws Exception {
        LocalDate date = LocalDate.of(2025, 10, 15);
        when(showtimeService.list(100L, null, date)).thenReturn(List.of(sampleShowtimeDTO));

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", "100")
                        .param("date", "2025-10-15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(showtimeService).list(100L, null, date);
    }

    @Test
    @WithMockUser
    void showtimesByMovie_noMovieId_shouldReturn400() throws Exception {
        mockMvc.perform(get("/theatres/showtimes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(showtimeService, never()).list(anyLong(), any(), any());
    }


    @Test
    @WithMockUser
    void showtimesByMovie_emptyResult_shouldReturnEmptyArray() throws Exception {
        when(showtimeService.list(100L, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(showtimeService).list(100L, null, null);
    }

    // =============== CREATE THEATRE TESTS ===============

    @Test
    @WithMockUser
    void createTheatre_validData_shouldReturnCreated() throws Exception {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        TheatreDTO savedDTO = TheatreDTO.builder()
                .theatreId(2L)
                .name("New Cinema")
                .city("Aarhus")
                .state("Central Jutland")
                .build();

        when(theatreService.create(any(TheatreDTO.class))).thenReturn(savedDTO);

        mockMvc.perform(post("/theatres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/theatres/2"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.theatreId").value(2L))
                .andExpect(jsonPath("$.name").value("New Cinema"))
                .andExpect(jsonPath("$.city").value("Aarhus"))
                .andExpect(jsonPath("$.state").value("Central Jutland"));

        verify(theatreService).create(any(TheatreDTO.class));
    }


    @Test
    @WithMockUser
    void createTheatre_validationError_shouldReturn400() throws Exception {
        // Assuming validation exists for required fields
        TheatreDTO inputDTO = TheatreDTO.builder().build(); // Empty DTO

        mockMvc.perform(post("/theatres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());

        verify(theatreService, never()).create(any());
    }

    // =============== UPDATE THEATRE TESTS ===============

    @Test
    @WithMockUser
    void updateTheatre_validData_shouldReturnUpdated() throws Exception {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .theatreId(1L)
                .name("Updated Cinema")
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreService.update(eq(1L), any(TheatreDTO.class))).thenReturn(updateDTO);

        mockMvc.perform(put("/theatres/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.theatreId").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Cinema"))
                .andExpect(jsonPath("$.city").value("Updated City"))
                .andExpect(jsonPath("$.state").value("Updated State"));

        verify(theatreService).update(eq(1L), any(TheatreDTO.class));
    }

    @Test
    @WithMockUser
    void updateTheatre_notFound_shouldReturn404() throws Exception {
        TheatreDTO updateDTO = TheatreDTO.builder()
                .name("Updated Cinema")
                .city("Updated City")
                .state("Updated State")
                .build();

        when(theatreService.update(eq(999L), any(TheatreDTO.class)))
                .thenThrow(new TheatreNotFoundException(999L));

        mockMvc.perform(put("/theatres/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(theatreService).update(eq(999L), any(TheatreDTO.class));
    }


    @Test
    @WithMockUser
    void updateTheatre_validationError_shouldReturn400() throws Exception {
        // Assuming validation exists
        TheatreDTO updateDTO = TheatreDTO.builder().build(); // Invalid DTO

        mockMvc.perform(put("/theatres/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());

        verify(theatreService, never()).update(anyLong(), any());
    }

    // =============== DELETE THEATRE TESTS ===============

    @Test
    @WithMockUser
    void deleteTheatre_validId_shouldReturn204() throws Exception {
        doNothing().when(theatreService).delete(1L);

        mockMvc.perform(delete("/theatres/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(theatreService).delete(1L);
    }

    @Test
    @WithMockUser
    void deleteTheatre_notFound_shouldReturn404() throws Exception {
        doThrow(new TheatreNotFoundException(999L))
                .when(theatreService).delete(999L);

        mockMvc.perform(delete("/theatres/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(theatreService).delete(999L);
    }


    // =============== EDGE CASE TESTS ===============

    @Test
    @WithMockUser
    void listTheatres_withSpecialCharacters_shouldHandleCorrectly() throws Exception {
        when(theatreService.list("Øst Danmark", "Århus")).thenReturn(List.of(sampleTheatreDTO));

        mockMvc.perform(get("/theatres")
                        .param("state", "Øst Danmark")
                        .param("city", "Århus")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(theatreService).list("Øst Danmark", "Århus");
    }

    @Test
    @WithMockUser
    void createTheatre_withSpecialCharacters_shouldWork() throws Exception {
        TheatreDTO inputDTO = TheatreDTO.builder()
                .name("Ålborg Kino")
                .city("Ålborg")
                .state("Nordjylland")
                .build();

        TheatreDTO savedDTO = TheatreDTO.builder()
                .theatreId(5L)
                .name("Ålborg Kino")
                .city("Ålborg")
                .state("Nordjylland")
                .build();

        when(theatreService.create(any(TheatreDTO.class))).thenReturn(savedDTO);

        mockMvc.perform(post("/theatres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Ålborg Kino"));

        verify(theatreService).create(any(TheatreDTO.class));
    }

    @Test
    @WithMockUser
    void showtimesByMovie_withZeroIds_shouldWork() throws Exception {
        when(showtimeService.list(0L, 0L, null)).thenReturn(List.of());

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", "0")
                        .param("theatreId", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(showtimeService).list(0L, 0L, null);
    }


    @Test
    @WithMockUser
    void showtimesByMovie_withMaxLongIds_shouldWork() throws Exception {
        when(showtimeService.list(Long.MAX_VALUE, Long.MAX_VALUE, null)).thenReturn(List.of());

        mockMvc.perform(get("/theatres/showtimes")
                        .param("movieId", String.valueOf(Long.MAX_VALUE))
                        .param("theatreId", String.valueOf(Long.MAX_VALUE))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(showtimeService).list(Long.MAX_VALUE, Long.MAX_VALUE, null);
    }
}

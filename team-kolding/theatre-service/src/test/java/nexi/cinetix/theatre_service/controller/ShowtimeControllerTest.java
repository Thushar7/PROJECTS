package nexi.cinetix.theatre_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nexi.cinetix.theatre_service.dto.ShowtimeDTO;
import nexi.cinetix.theatre_service.service.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ShowtimeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowtimeService showtimeService;

    private ObjectMapper objectMapper;
    private ShowtimeDTO showtimeDTO1;
    private ShowtimeDTO showtimeDTO2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        showtimeDTO1 = ShowtimeDTO.builder()
                .showtimeId(1L)
                .movieId(101L)
                .theatreId(201L)
                .showDate(today)
                .startTime(now)
                .endTime(now.plusHours(2))
                .build();

        showtimeDTO2 = ShowtimeDTO.builder()
                .showtimeId(2L)
                .movieId(102L)
                .theatreId(202L)
                .showDate(today)
                .startTime(now.plusHours(3))
                .endTime(now.plusHours(5))
                .build();
    }

    @Test
    void listShowtimes_NoParameters_ReturnsAllShowtimes() throws Exception {
        List<ShowtimeDTO> showtimes = Arrays.asList(showtimeDTO1, showtimeDTO2);
        when(showtimeService.list(null, null, null)).thenReturn(showtimes);

        mockMvc.perform(get("/showtimes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].showtimeId").value(showtimeDTO1.getShowtimeId()))
                .andExpect(jsonPath("$[1].showtimeId").value(showtimeDTO2.getShowtimeId()));

        verify(showtimeService).list(null, null, null);
    }

    @Test
    void listShowtimes_WithMovieId_ReturnsFilteredShowtimes() throws Exception {
        List<ShowtimeDTO> showtimes = Arrays.asList(showtimeDTO1);
        when(showtimeService.list(eq(101L), isNull(), isNull())).thenReturn(showtimes);

        mockMvc.perform(get("/showtimes")
                .param("movieId", "101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].movieId").value(101L));

        verify(showtimeService).list(101L, null, null);
    }

    @Test
    void listShowtimes_WithTheatreId_ReturnsFilteredShowtimes() throws Exception {
        List<ShowtimeDTO> showtimes = Arrays.asList(showtimeDTO1);
        when(showtimeService.list(isNull(), eq(201L), isNull())).thenReturn(showtimes);

        mockMvc.perform(get("/showtimes")
                .param("theatreId", "201"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].theatreId").value(201L));

        verify(showtimeService).list(null, 201L, null);
    }

    @Test
    void listShowtimes_WithDate_ReturnsFilteredShowtimes() throws Exception {
        List<ShowtimeDTO> showtimes = Arrays.asList(showtimeDTO1, showtimeDTO2);
        LocalDate today = LocalDate.now();
        when(showtimeService.list(isNull(), isNull(), eq(today))).thenReturn(showtimes);

        mockMvc.perform(get("/showtimes")
                .param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(showtimeService).list(null, null, today);
    }

    @Test
    void getShowtime_ExistingId_ReturnsShowtime() throws Exception {
        when(showtimeService.get(1L)).thenReturn(showtimeDTO1);

        mockMvc.perform(get("/showtimes/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.showtimeId").value(1L))
                .andExpect(jsonPath("$.movieId").value(101L))
                .andExpect(jsonPath("$.theatreId").value(201L));

        verify(showtimeService).get(1L);
    }

    @Test
    void createShowtime_ValidInput_ReturnsCreated() throws Exception {
        ShowtimeDTO inputDto = ShowtimeDTO.builder()
                .movieId(101L)
                .theatreId(201L)
                .showDate(LocalDate.now())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        ShowtimeDTO createdDto = ShowtimeDTO.builder()
                .showtimeId(3L)
                .movieId(inputDto.getMovieId())
                .theatreId(inputDto.getTheatreId())
                .showDate(inputDto.getShowDate())
                .startTime(inputDto.getStartTime())
                .endTime(inputDto.getEndTime())
                .build();

        when(showtimeService.create(any(ShowtimeDTO.class))).thenReturn(createdDto);

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/showtimes/3"))
                .andExpect(jsonPath("$.showtimeId").value(3L))
                .andExpect(jsonPath("$.movieId").value(inputDto.getMovieId()))
                .andExpect(jsonPath("$.theatreId").value(inputDto.getTheatreId()));

        verify(showtimeService).create(any(ShowtimeDTO.class));
    }

    @Test
    void updateShowtime_ValidInput_ReturnsUpdatedShowtime() throws Exception {
        ShowtimeDTO inputDto = ShowtimeDTO.builder()
                .movieId(103L)
                .theatreId(203L)
                .showDate(LocalDate.now())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        ShowtimeDTO updatedDto = ShowtimeDTO.builder()
                .showtimeId(1L)
                .movieId(inputDto.getMovieId())
                .theatreId(inputDto.getTheatreId())
                .showDate(inputDto.getShowDate())
                .startTime(inputDto.getStartTime())
                .endTime(inputDto.getEndTime())
                .build();

        when(showtimeService.update(eq(1L), any(ShowtimeDTO.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/showtimes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.showtimeId").value(1L))
                .andExpect(jsonPath("$.movieId").value(inputDto.getMovieId()))
                .andExpect(jsonPath("$.theatreId").value(inputDto.getTheatreId()));

        verify(showtimeService).update(eq(1L), any(ShowtimeDTO.class));
    }

    @Test
    void deleteShowtime_ExistingId_ReturnsNoContent() throws Exception {
        doNothing().when(showtimeService).delete(1L);

        mockMvc.perform(delete("/showtimes/{id}", 1))
                .andExpect(status().isNoContent());

        verify(showtimeService).delete(1L);
    }
}

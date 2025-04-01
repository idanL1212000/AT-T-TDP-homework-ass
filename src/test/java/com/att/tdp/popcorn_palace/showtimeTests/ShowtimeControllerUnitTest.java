package com.att.tdp.popcorn_palace.showtimeTests;

import com.att.tdp.popcorn_palace.GlobalExceptionHandler;
import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.exceptions.InvalidMovieIdNotFoundException;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeController;
import com.att.tdp.popcorn_palace.showTime.ShowtimeService;
import com.att.tdp.popcorn_palace.showTime.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.time.Instant;

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ShowtimeControllerUnitTest {


    private MockMvc mockMvc;

    @Mock
    private ShowtimeService showtimeService;

    @InjectMocks
    private ShowtimeController showtimeController;

    private ObjectMapper objectMapper;

    private Movie movie;
    private Showtime showtime;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(showtimeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        movie = makeMovie("movie","base",0.0,120,2000);
        movie.setId(1L);
        showtime = makeShowtime(movie,
                "test Theater",10.0,Instant.now().plus(Duration.ofHours(2)),
                Instant.now().plus(Duration.ofHours(4)));
        showtime.setId(1L);
    }

    @Test
    public void testShowtimeValidationNegativeMovieId() throws Exception {
        // Arrange
        showtime.setMovieId(-1L);

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("MovieId need to be greater than 0")));

        // Service should not be called
        verify(showtimeService, never()).addShowtime(any(Showtime.class));
    }

    @Test
    public void testShowtimeValidationNegativePrice() throws Exception {
        // Arrange
        showtime.setPrice(-10.0);

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("Price must be positive")));
    }

    @Test
    public void testShowtimeValidationEmptyTheater() throws Exception {
        // Arrange
        showtime.setTheater("");

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("Theater name is required")));
    }

    @Test
    public void testShowtimeValidationWhiteSpaceTheater() throws Exception {
        // Arrange
        showtime.setTheater("    ");

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("Theater name is required")));
    }

    @Test
    public void testShowtimeValidationNullTheater() throws Exception {
        // Arrange
        showtime.setTheater(null);

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("Theater name is required")));
    }

    @Test
    public void testShowtimeValidationPastStartTime() throws Exception {
        // Arrange
        showtime.setStartTime(Instant.now().minus(Duration.ofDays(1)));

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("Start time must be in the future")));
    }

    @Test
    public void testShowtimeValidationNullEndTime() throws Exception {
        // Arrange
        showtime.setEndTime(null);

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("End time is required")));
    }

    @Test
    public void testShowtimeValidationMany() throws Exception {
        // Arrange
        showtime.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        showtime.setPrice(-10.0);
        showtime.setMovieId(-1L);
        showtime.setTheater("");

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message")
                        .value(containsString("Start time must be in the future")))
                .andExpect(jsonPath("$.message").value(containsString("Theater name is required")))
                .andExpect(jsonPath("$.message").value(containsString("Price must be positive")))
                .andExpect(jsonPath("$.message").value(containsString("MovieId need to be greater than 0")));
    }

    @Test
    public void testGetShowtime_success() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        when(showtimeService.getShowtimeById(showtimeId)).thenReturn(showtime);

        // Act & Assert
        mockMvc.perform(get("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showtimeId))
                .andExpect(jsonPath("$.theater").value(showtime.getTheater()));

        verify(showtimeService).getShowtimeById(showtimeId);
    }

    @Test
    public void testGetShowtimeNotFound() throws Exception {
        // Arrange
        Long showtimeId = 999L;
        when(showtimeService.getShowtimeById(showtimeId))
                .thenThrow(new InvalidShowtimeIdNotFoundException());

        // Act & Assert
        mockMvc.perform(get("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testGetShowtimeNegativeId() throws Exception {
        // Arrange
        Long showtimeId = -1L;
        // Act & Assert
        mockMvc.perform(get("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"))
                .andExpect(jsonPath("$.message").exists());

        // Verify service is NOT called with negative ID
        verify(showtimeService, never()).getShowtimeById(showtimeId);
    }

    @Test
    public void testAddShowtimeSuccess() throws Exception {
        // Arrange
        when(showtimeService.addShowtime(any(Showtime.class))).thenReturn(showtime);

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showtime.getId()))
                .andExpect(jsonPath("$.theater").value(showtime.getTheater()));

        verify(showtimeService).addShowtime(any(Showtime.class));
    }

    @Test
    public void testAddShowtime_conflict() throws Exception {
        // Arrange
        when(showtimeService.addShowtime(any(Showtime.class)))
                .thenThrow(new ShowtimeOverlapException("Showtime overlaps with existing showtime"));

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.error").value("Showtime Conflict"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testAddShowtimeInvalidMovieId() throws Exception {
        // Arrange
        when(showtimeService.addShowtime(any(Showtime.class)))
                .thenThrow(new InvalidMovieIdNotFoundException());

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Movie Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testAddShowtimeInvalidShowtimeBecauseMovieTime() throws Exception {
        // Arrange
        showtime.setEndTime(Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(60)));
        when(showtimeService.addShowtime(any(Showtime.class)))
                .thenThrow(new InvalidShowtimeDurationException(60,120,150));

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"));
    }

    @Test
    public void testAddShowtimeInvalidShowtimeStartTimeEndTime() throws Exception {
        // Arrange
        showtime.setEndTime(Instant.now().plus(Duration.ofDays(1)).minus(Duration.ofMinutes(60)));
        when(showtimeService.addShowtime(any(Showtime.class)))
                .thenThrow(new InvalidShowtimeStartTimeEndTimeException());

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"));
    }

    @Test
    public void testAddShowtimeValidationFailure() throws Exception {
        // Arrange
        Showtime showtime = new Showtime(); // Empty showtime that will fail validation

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateShowtime_success() throws Exception {
        Showtime updated = makeShowtime(movie,"Updated Theater",10.0,
                Instant.now().plus(Duration.ofHours(2)),
                Instant.now().plus(Duration.ofHours(4)));
        updated.setTheater("Updated Theater");

        mockMvc.perform(post("/showtimes/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());

        verify(showtimeService).updateShowtime(argThat(st ->
                st.getTheater().equals("Updated Theater")), eq(1L));
    }

    @Test
    public void testUpdateShowtimeNotFound() throws Exception {
        // Arrange
        Long showtimeId = 999L;
        doThrow(new InvalidShowtimeIdNotFoundException())
                .when(showtimeService).updateShowtime(any(Showtime.class), eq(showtimeId));

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"))
                .andExpect(jsonPath("$.message").exists());

        verify(showtimeService,times(1)).updateShowtime(any(Showtime.class),eq((showtimeId)));
    }

    @Test
    public void testUpdateShowtimeOverlap() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        doThrow(new ShowtimeOverlapException("Showtime overlaps with existing showtime"))
                .when(showtimeService).updateShowtime(any(Showtime.class), eq(showtimeId));

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.error").value("Showtime Conflict"))
                .andExpect(jsonPath("$.message").exists());

        verify(showtimeService,times(1)).updateShowtime(any(Showtime.class),eq((showtimeId)));

    }

    @Test
    public void testUpdateShowtimeNegativeId() throws Exception {
        // Arrange
        Long showtimeId = -1L;

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"))
                .andExpect(jsonPath("$.message").exists());

        // Verify service is NOT called with negative ID
        verify(showtimeService, never()).updateShowtime(any(Showtime.class), eq(showtimeId));
    }

    @Test
    public void testUpdateShowtimeInvalidShowtimeBecauseMovieTime() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        showtime.setEndTime(Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(60)));
        doThrow(new InvalidShowtimeDurationException(60,120,150))
                .when(showtimeService).updateShowtime(any(Showtime.class), eq(showtimeId));

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"));
    }

    @Test
    public void testUpdateShowtimeInvalidShowtimeStartTimeEndTime() throws Exception {
        // Arrange
        Long showtimeId = 1L;

        showtime.setEndTime(Instant.now().plus(Duration.ofDays(1)).minus(Duration.ofMinutes(60)));
        doThrow(new InvalidShowtimeStartTimeEndTimeException())
                .when(showtimeService).updateShowtime(any(Showtime.class), eq(showtimeId));

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"));
    }

    @Test
    public void testDeleteShowtime_success() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        doNothing().when(showtimeService).deleteShowtime(showtimeId);

        // Act & Assert
        mockMvc.perform(delete("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isOk());

        verify(showtimeService).deleteShowtime(showtimeId);
    }

    @Test
    public void testDeleteShowtimeNotFound() throws Exception {
        // Arrange
        Long showtimeId = 999L;
        doThrow(new InvalidShowtimeIdNotFoundException())
                .when(showtimeService).deleteShowtime(showtimeId);

        // Act & Assert
        mockMvc.perform(delete("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testDeleteShowtimeNegativeId() throws Exception {
        // Arrange
        Long showtimeId = -1L;

        // Act & Assert
        mockMvc.perform(delete("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Invalid Showtime"))
                .andExpect(jsonPath("$.message").exists());

        // Verify service is NOT called with negative ID
        verify(showtimeService, never()).deleteShowtime(showtimeId);
    }
}

package com.att.tdp.popcorn_palace.showtimeTests;

import com.att.tdp.popcorn_palace.GlobalExceptionHandler;
import com.att.tdp.popcorn_palace.movies.exceptions.InvalidMovieIdException;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ShowtimeControllerTests {

    private MockMvc mockMvc;

    @Mock
    private ShowtimeService showtimeService;

    @InjectMocks
    private ShowtimeController showtimeController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(showtimeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testShowtimeValidation_NegativeMovieId() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
        showtime.setMovieId(-1L);

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(containsString("Movie ID must be positive")));

        // Service should not be called
        verify(showtimeService, never()).addShowtime(any(Showtime.class));
    }

    @Test
    public void testShowtimeValidation_NegativePrice() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testShowtimeValidation_EmptyTheater() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testShowtimeValidation_WhiteSpaceTheater() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testShowtimeValidation_NullTheater() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testShowtimeValidation_PastStartTime() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testShowtimeValidation_NullEndTime() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testShowtimeValidation_Many() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
                .andExpect(jsonPath("$.message").value(containsString("Movie ID must be positive")));

        ;
    }

    @Test
    public void testGetShowtime_Success() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        Showtime showtime = createSampleShowtime();
        when(showtimeService.getShowtimeById(showtimeId)).thenReturn(showtime);

        // Act & Assert
        mockMvc.perform(get("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showtimeId))
                .andExpect(jsonPath("$.theater").value(showtime.getTheater()));

        verify(showtimeService).getShowtimeById(showtimeId);
    }

    @Test
    public void testGetShowtime_NotFound() throws Exception {
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
    public void testGetShowtime_NegativeId() throws Exception {
        // Arrange
        Long showtimeId = -1L;
        // No need to mock service method since controller should reject before service call

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
    public void testAddShowtime_Success() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testAddShowtime_Conflict() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testAddShowtime_InvalidMovieId() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
        when(showtimeService.addShowtime(any(Showtime.class)))
                .thenThrow(new InvalidMovieIdException());

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
    public void testAddShowtime_InvalidShowtimeBecuseMovieTime() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testAddShowtime_InvalidShowtimeStartTimeEndTime() throws Exception {
        // Arrange
        Showtime showtime = createSampleShowtime();
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
    public void testAddShowtime_ValidationFailure() throws Exception {
        // Arrange
        Showtime showtime = new Showtime(); // Empty showtime that will fail validation

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateShowtime_Success() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        Showtime showtime = createSampleShowtime();
        showtime.setId(showtimeId);
        doNothing().when(showtimeService).updateShowtime(any(Showtime.class), eq(showtimeId));

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isOk());

        verify(showtimeService).updateShowtime(any(Showtime.class), eq(showtimeId));
    }

    @Test
    public void testUpdateShowtime_NotFound() throws Exception {
        // Arrange
        Long showtimeId = 999L;
        Showtime showtime = createSampleShowtime();
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
    }

    @Test
    public void testUpdateShowtime_Overlap() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        Showtime showtime = createSampleShowtime();
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
    }

    @Test
    public void testUpdateShowtime_NegativeId() throws Exception {
        // Arrange
        Long showtimeId = -1L;
        Showtime showtime = createSampleShowtime();

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
    public void testUpdateShowtime_InvalidShowtimeBecuseMovieTime() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        Showtime showtime = createSampleShowtime();
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
    public void testUpdateShowtime_InvalidShowtimeStartTimeEndTime() throws Exception {
        // Arrange
        Long showtimeId = 1L;

        Showtime showtime = createSampleShowtime();
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
    public void testDeleteShowtime_Success() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        doNothing().when(showtimeService).deleteShowtime(showtimeId);

        // Act & Assert
        mockMvc.perform(delete("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isOk());

        verify(showtimeService).deleteShowtime(showtimeId);
    }

    @Test
    public void testDeleteShowtime_NotFound() throws Exception {
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
    public void testDeleteShowtime_NegativeId() throws Exception {
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

    // Helper method to create a sample showtime for testing
    private Showtime createSampleShowtime() {

        Showtime showtime = new Showtime();
        showtime.setId(1L);
        showtime.setMovieId(1L);
        showtime.setTheater("Test Theater");
        showtime.setPrice(10.0);
        showtime.setStartTime(Instant.now().plus(Duration.ofDays(1)));
        showtime.setEndTime(Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(150)));
        return showtime;
    }
}


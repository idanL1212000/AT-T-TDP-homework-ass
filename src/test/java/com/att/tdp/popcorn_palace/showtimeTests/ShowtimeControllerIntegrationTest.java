package com.att.tdp.popcorn_palace.showtimeTests;

import com.att.tdp.popcorn_palace.booking.Booking;
import com.att.tdp.popcorn_palace.booking.BookingRepository;
import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
import com.att.tdp.popcorn_palace.showTime.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeMovie;
import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeShowtime;
import static com.att.tdp.popcorn_palace.EntityFactoryForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShowtimeControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ShowtimeService showtimeService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private Movie testMovie;
    private Showtime testShowtime;
    private List<Booking> bookings;

    @BeforeEach
    void setup() {
        testMovie = movieRepository.save(makeMovie("Inception", "Sci-Fi", 8.8, 90, 2000));

        testShowtime = showtimeRepository.save(makeShowtime(
                testMovie,
                "IMAX 1",
                15.99,
                Instant.now().plus(Duration.ofHours(2)),
                Instant.now().plus(Duration.ofHours(4)))
        );
        bookings = makeBookings(testShowtime, UUID.randomUUID().toString());
        for (int i = 0; i < bookings.size(); i++) {
            bookingRepository.save(bookings.get(i));
        }
    }

    @Test
    void getShowtime_ExistingId_ReturnsShowtime() throws Exception {
        mockMvc.perform(get("/showtimes/{id}", testShowtime.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testShowtime.getId()))
                .andExpect(jsonPath("$.price").value(15.99));
    }

    @Test
    void getShowtime_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/showtimes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getShowtime_NegativeId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/showtimes/{id}", -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShowtime_ValidRequest_ReturnsCreated() throws Exception {
        Showtime newShowtime = makeShowtime(
                testMovie,
                "Screen 5",
                12.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(120))
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newShowtime)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theater").value("Screen 5"));
    }

    @Test
    void createShowtime_InvalidStartEndTime_ReturnsBadRequest() throws Exception {
        Showtime invalidShowtime = makeShowtime(
                testMovie,
                "Screen 5",
                12.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now() // End time before start time
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidShowtime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShowtime_NegativePrice_ReturnsBadRequest() throws Exception {
        // Create showtime with negative price
        Showtime invalidShowtime = makeShowtime(
                testMovie,
                "Screen 5",
                -10.99, // Negative price
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidShowtime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShowtime_PastStartTime_ReturnsBadRequest() throws Exception {
        // Create showtime with start time in the past
        Showtime invalidShowtime = makeShowtime(
                testMovie,
                "Screen 5",
                12.99,
                Instant.now().minus(Duration.ofDays(1)), // Past time
                Instant.now().plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidShowtime)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void createShowtime_EmptyTheater_ReturnsBadRequest() throws Exception {
        // Create showtime with empty theater name
        Showtime invalidShowtime = makeShowtime(
                testMovie,
                "", // Empty theater name
                12.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidShowtime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShowtime_InvalidDuration_ReturnsBadRequest() throws Exception {
        // Create showtime with too short duration (assuming your validation checks for this)
        Showtime shortDurationShowtime = makeShowtime(
                testMovie,
                "Screen 5",
                12.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).minus(Duration.ofMinutes(5)) // Very short duration
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortDurationShowtime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShowtime_OverlappingTimes_ReturnsConflict() throws Exception {
        Showtime overlapping = makeShowtime(
                testMovie,
                "IMAX 1",
                15.99,
                testShowtime.getStartTime().plus(Duration.ofMinutes(30)),
                testShowtime.getEndTime().plus(Duration.ofMinutes((30)))
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overlapping)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShowtime_ValidUpdate_ReturnsUpdated() throws Exception {
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                19.99,
                testShowtime.getStartTime().plus(Duration.ofDays(1)),
                testShowtime.getEndTime().plus(Duration.ofDays(1))
        );

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }

    @Test
    void updateShowtime_NonExistentId_ReturnsNotFound() throws Exception {
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                19.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes/update/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShowtime_NegativeId_ReturnsBadRequest() throws Exception {
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                19.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes/update/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShowtime_OverlappingTimes_ReturnsConflict() throws Exception {
        // First create a second showtime
        Showtime secondShowtime = showtimeRepository.save(makeShowtime(
                testMovie,
                "IMAX 2",
                12.99,
                Instant.now().plus(Duration.ofHours(5)),
                Instant.now().plus(Duration.ofHours(7))
        ));

        // Then try to update it to overlap with the first showtime
        Showtime update = makeShowtime(
                testMovie,
                "IMAX 2",
                12.99,
                testShowtime.getStartTime().plus(Duration.ofMinutes(30)),
                testShowtime.getEndTime().minus(Duration.ofMinutes(30))
        );

        mockMvc.perform(post("/showtimes/update/{id}", secondShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShowtime_InvalidStartEndTime_ReturnsBadRequest() throws Exception {
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                19.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofHours(1)) // End time before start time
        );

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShowtime_NegativePrice_ReturnsBadRequest() throws Exception {
        // Create update with negative price
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                -19.99, // Negative price
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShowtime_PastStartTime_ReturnsBadRequest() throws Exception {
        // Create update with start time in the past
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                19.99,
                Instant.now().minus(Duration.ofDays(1)), // Past time
                Instant.now().plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShowtime_EmptyTheater_ReturnsBadRequest() throws Exception {
        // Create update with empty theater name
        Showtime update = makeShowtime(
                testMovie,
                "", // Empty theater name
                19.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShowtime_InvalidDuration_ReturnsBadRequest() throws Exception {
        // Create update with invalid time duration
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                19.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).minus(Duration.ofMinutes(5)) // End time before start time
        );

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShowtime_WithBookings_ReturnsBadRequest() throws Exception {
        // Try to update a showtime that already has bookings
        testShowtime.setBookings(bookings);
        Showtime update = makeShowtime(
                testMovie,
                testShowtime.getTheater(),
                testShowtime.getPrice(),
                testShowtime.getStartTime().plus(Duration.ofHours(2)), // Changed time
                testShowtime.getEndTime().plus(Duration.ofHours(2))
        );

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShowtime_NullStartTime_ReturnsBadRequest() throws Exception {
        // Create showtime with null start time
        Showtime update = new Showtime();
        update.setMovie(testMovie);
        update.setTheater("Updated Theater");
        update.setPrice(19.99);
        update.setStartTime(null); // Null start time
        update.setEndTime(Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2)));

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShowtime_NullEndTime_ReturnsBadRequest() throws Exception {
        // Create showtime with null end time
        Showtime update = new Showtime();
        update.setMovie(testMovie);
        update.setTheater("Updated Theater");
        update.setPrice(19.99);
        update.setStartTime(Instant.now().plus(Duration.ofDays(1)));
        update.setEndTime(null); // Null end time

        mockMvc.perform(post("/showtimes/update/{id}", testShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShowtime_WithBookings_CascadesDeletion() throws Exception {
        testShowtime.setBookings(bookings);
        assertTrue(showtimeRepository.existsById(testShowtime.getId()));
        assertEquals(bookings.size(), bookingRepository.count());

        // Execute deletion
        mockMvc.perform(delete("/showtimes/{id}", testShowtime.getId()))
                .andExpect(status().isOk());

        // Verify deletion
        assertFalse(showtimeRepository.existsById(testShowtime.getId()));
        assertEquals(0, bookingRepository.count());
    }

    @Test
    void deleteShowtime_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/showtimes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShowtime_NegativeId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/showtimes/{id}", -1L))
                .andExpect(status().isBadRequest());
    }
}

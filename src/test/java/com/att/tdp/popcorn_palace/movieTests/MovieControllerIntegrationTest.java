package com.att.tdp.popcorn_palace.movieTests;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
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
import java.util.ArrayList;
import java.util.List;

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeMovie;
import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeShowtime;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MovieControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ShowtimeRepository showtimeRepository;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private Movie testMovie;
    private List<Showtime> testShowtimes;

    @BeforeEach
    void setup() {
        // Create a test movie
        testMovie = movieRepository.save(makeMovie("Inception", "Sci-Fi", 8.8, 148, 2010));

        // Create some showtimes for the movie
        testShowtimes = new ArrayList<>();
        Showtime showtime1 = showtimeRepository.save(makeShowtime(
                testMovie,
                "Theater 1",
                15.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(testMovie.getDuration()))
        ));
        Showtime showtime2 = showtimeRepository.save(makeShowtime(
                testMovie,
                "Theater 2",
                12.99,
                Instant.now().plus(Duration.ofDays(2)),
                Instant.now().plus(Duration.ofDays(2)).plus(Duration.ofMinutes(testMovie.getDuration()))
        ));
        testShowtimes.add(showtime1);
        testShowtimes.add(showtime2);
        testMovie.setShowtimes(testShowtimes);
    }

    @Test
    void getAllMovies_ReturnsMoviesList() throws Exception {
        // Create a second movie to ensure multiple movies are returned
        movieRepository.save(makeMovie("The Matrix", "Sci-Fi", 8.7, 136, 1999));

        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[1].title").exists());
    }

    @Test
    void createMovie_ValidMovie_ReturnsCreated() throws Exception {
        Movie newMovie = makeMovie("The Dark Knight", "Action", 9.0, 152, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Dark Knight"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createMovie_EmptyTitle_ReturnsBadRequest() throws Exception {
        Movie invalidMovie = makeMovie("", "Action", 9.0, 152, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMovie_NullTitle_ReturnsBadRequest() throws Exception {
        Movie movie = new Movie();
        movie.setGenre("Action");
        movie.setRating(9.0);
        movie.setDuration(152);
        movie.setReleaseYear(2008);
        // title is null

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMovie_EmptyGenre_ReturnsBadRequest() throws Exception {
        Movie invalidMovie = makeMovie("The Dark Knight", "", 9.0, 152, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMovie_NegativeRating_ReturnsBadRequest() throws Exception {
        Movie invalidMovie = makeMovie("The Dark Knight", "Action", -1.0, 152, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMovie_RatingAboveTen_ReturnsBadRequest() throws Exception {
        Movie invalidMovie = makeMovie("The Dark Knight", "Action", 11.0, 152, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMovie_NegativeDuration_ReturnsBadRequest() throws Exception {
        Movie invalidMovie = makeMovie("The Dark Knight", "Action", 9.0, -10, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMovie_ZeroDuration_ReturnsBadRequest() throws Exception {
        Movie invalidMovie = makeMovie("The Dark Knight", "Action", 9.0, 0, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMovie_InvalidReleaseYear_ReturnsBadRequest() throws Exception {
        // Assuming movies should be released after 1900
        Movie invalidMovie = makeMovie("The Dark Knight", "Action", 9.0, 152, 1800);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMovie_ValidUpdate_ReturnsUpdated() throws Exception {
        Movie update = makeMovie("Inception (Director's Cut)", "Sci-Fi/Thriller", 9.0, 170, 2010);
        testMovie.setShowtimes(null);
        mockMvc.perform(post("/movies/update/{movieTitle}", testMovie.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }

    @Test
    void updateMovie_NonExistentTitle_ReturnsNotFound() throws Exception {
        Movie update = makeMovie("Updated Movie", "Drama", 8.0, 120, 2015);

        mockMvc.perform(post("/movies/update/{movieTitle}", "None")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_emptyTitle_ReturnsBadRequest() throws Exception {
        Movie update = makeMovie("Updated Movie", "Drama", 8.0, 120, 2015);

        mockMvc.perform(post("/movies/update/{movieTitle}", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateMovie_InvalidData_ReturnsBadRequest() throws Exception {
        Movie invalidUpdate = makeMovie("", "Drama", 8.0, 120, 2015);

        mockMvc.perform(post("/movies/update/{movieTitle}", testMovie.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMovie_ExistingTitle_DeletesMovieAndShowtimes() throws Exception {
        assertTrue(movieRepository.existsById(testMovie.getId()));
        assertEquals(2, showtimeRepository.findAll().size());

        mockMvc.perform(delete("/movies/{movieTitle}", testMovie.getTitle()))
                .andExpect(status().isOk());

        assertFalse(movieRepository.existsById(testMovie.getId()));
        // Verify that associated showtimes were also deleted (assuming cascade delete)
        assertEquals(0, showtimeRepository.findAll().size());
    }

    @Test
    void deleteMovie_NonExistentTitle_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/movies/{movieTitle}", "None"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovie_emptyTitle_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/movies/{movieTitle}", ""))
                .andExpect(status().isInternalServerError());
    }

}


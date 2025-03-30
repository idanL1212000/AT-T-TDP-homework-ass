package com.att.tdp.popcorn_palace.showtimeTests;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import com.att.tdp.popcorn_palace.movies.exceptions.InvalidMovieIdNotFoundException;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
import com.att.tdp.popcorn_palace.showTime.ShowtimeService;
import com.att.tdp.popcorn_palace.showTime.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeMovie;
import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeShowtime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ShowtimeServiceImpTestsIn {
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private MovieRepository movieRepository;

    private Movie testMovie;
    private Showtime baseShowtime;

    @BeforeEach
    void setup() {
        testMovie = movieRepository.save(makeMovie("Interstellar", "Sci-Fi", 8.6,90, 2014));

        baseShowtime = showtimeRepository.save(makeShowtime(testMovie,
                "IMAX 2",
                18.99,
                Instant.now().plus(Duration.ofHours(2)),
                Instant.now().plus(Duration.ofHours(4)))
        );
    }
    @Test
    void getShowtimeById_Success() throws InvalidShowtimeIdNotFoundException {
        Showtime retrieved = showtimeService.getShowtimeById(baseShowtime.getId());

        assertNotNull(retrieved);
        assertEquals(baseShowtime.getId(), retrieved.getId());
        assertEquals(baseShowtime.getTheater(), retrieved.getTheater());
    }

    @Test
    void getShowtimeById_InvalidId() {
        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> {
            showtimeService.getShowtimeById(999L); // Non-existent ID
        });
    }

    @Test
    void addShowtime_success() throws ShowtimeOverlapException,
            InvalidMovieIdNotFoundException,
            InvalidShowtimeStartTimeEndTimeException, InvalidShowtimeDurationException {
        Showtime newShowtime = makeShowtime(testMovie,
                "Screen 3",
                12.50,
                Instant.now().plus(Duration.ofHours(4)),
                Instant.now().plus(Duration.ofHours(6))
        );

        Showtime saved = showtimeService.addShowtime(newShowtime);

        assertNotNull(saved.getId());
        assertEquals(2, showtimeRepository.count());
        assertEquals("Screen 3", showtimeRepository.findById(saved.getId()).get().getTheater());
    }


    @Test
    void addShowtime_InvalidMovieNotFound() {
        Movie invalidMovie =  makeMovie("Non-existent movie","none",0.0,10,2000);
        invalidMovie.setId(999L);
        Showtime invalidShowtime = makeShowtime(
                invalidMovie, // Non-existent movie
                "Screen 4",
                14.99,
                Instant.now().plus(Duration.ofDays(2)),
                Instant.now().plus(Duration.ofDays(2)).plus(Duration.ofMinutes(120))
        );

        assertThrows(InvalidMovieIdNotFoundException.class, () -> {
            showtimeService.addShowtime(invalidShowtime);
        });
    }

    @Test
    void addShowtime_InvalidDuration() {
        Showtime invalid = makeShowtime(
                testMovie,
                "Screen 5",
                15.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(50)) // Movie duration is 120
        );

        assertThrows(InvalidShowtimeDurationException.class, () -> {
            showtimeService.addShowtime(invalid);
        });
    }

    @Test
    void addShowtime_InvalidStartTimeEndTime() {
        Showtime invalid = makeShowtime(
                testMovie,
                "Screen 6",
                15.99,
                Instant.now().plus(Duration.ofDays(1)), // Start time
                Instant.now().plus(Duration.ofHours(20)) // End time before start time
        );

        assertThrows(InvalidShowtimeStartTimeEndTimeException.class, () -> {
            showtimeService.addShowtime(invalid);
        });
    }

    @Test
    void addShowtime_DurationTooLong() {
        Showtime invalid = makeShowtime(
                testMovie,
                "Screen 7",
                15.99,
                Instant.now().plus(Duration.ofDays(1)),
                // Movie duration (90 min) + extra (30 min) = 120 min max, this is 130 min
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(130))
        );
        assertThrows(InvalidShowtimeDurationException.class, () -> {
            showtimeService.addShowtime(invalid);
        });
    }

    @Test
    void addShowtime_OverlapExistingShowtime() {
        // Create a showtime that overlaps with baseShowtime
        Showtime overlapping = makeShowtime(
                testMovie,
                baseShowtime.getTheater(), // Same theater
                15.99,
                baseShowtime.getStartTime().plus(Duration.ofMinutes(30)), // Starts during baseShowtime
                baseShowtime.getEndTime().plus(Duration.ofMinutes(30)) // Ends after baseShowtime
        );

        assertThrows(ShowtimeOverlapException.class, () -> {
            showtimeService.addShowtime(overlapping);
        });
    }

    @Test
    void addShowtime_OverlapWithStartTime() {
        // Create a showtime where only the start time overlaps
        Showtime overlapping = makeShowtime(
                testMovie,
                baseShowtime.getTheater(), // Same theater
                15.99,
                baseShowtime.getStartTime().minus(Duration.ofMinutes(90)), // Starts before baseShowtime
                baseShowtime.getStartTime().plus(Duration.ofMinutes(30)) // Ends during baseShowtime
        );

        assertThrows(ShowtimeOverlapException.class, () -> {
            showtimeService.addShowtime(overlapping);
        });
    }

    @Test
    void addShowtime_OverlapWithEndTime() {
        // Create a showtime where only the end time overlaps
        Showtime overlapping = makeShowtime(
                testMovie,
                baseShowtime.getTheater(), // Same theater
                15.99,
                baseShowtime.getEndTime().minus(Duration.ofMinutes(30)), // Starts during baseShowtime
                baseShowtime.getEndTime().plus(Duration.ofMinutes(90)) // Ends after baseShowtime
        );

        assertThrows(ShowtimeOverlapException.class, () -> {
            showtimeService.addShowtime(overlapping);
        });
    }

    @Test
    void addShowtime_NoOverlapDifferentTheater() throws Exception {
        // Create a showtime with same time but different theater - should succeed
        Showtime differentTheater = makeShowtime(
                testMovie,
                "Different Theater", // Different theater
                15.99,
                baseShowtime.getStartTime(), // Same start time
                baseShowtime.getEndTime() // Same end time
        );

        Showtime saved = showtimeService.addShowtime(differentTheater);
        assertNotNull(saved.getId());
        assertEquals(2, showtimeRepository.count());
    }

    @Test
    void updateShowtime_success() throws ShowtimeOverlapException, InvalidShowtimeIdNotFoundException, InvalidShowtimeStartTimeEndTimeException, InvalidShowtimeDurationException, UpdateShowtimeWithBookingsException, InvalidMovieIdNotFoundException {
        Showtime update = makeShowtime(
                testMovie,
                baseShowtime.getTheater(),
                baseShowtime.getPrice(),
                baseShowtime.getStartTime(),
                baseShowtime.getEndTime()
        );

        showtimeService.updateShowtime(update, baseShowtime.getId());

        assertEquals(baseShowtime.getTheater(), showtimeRepository.findById(baseShowtime.getId()).get().getTheater());
        assertEquals(1, showtimeRepository.count());
    }

    @Test
    void updateShowtime_InvalidShowtimeId() {
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                18.99,
                Instant.now().plus(Duration.ofHours(6)),
                Instant.now().plus(Duration.ofHours(8))
        );

        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> {
            showtimeService.updateShowtime(update, 999L); // Non-existent ID
        });
    }

    @Test
    void updateShowtime_InvalidMovieId() {
        Movie nonExistentMovie = makeMovie("Non-existent", "none", 0.0, 10, 2000);
        nonExistentMovie.setId(999L);

        Showtime update = makeShowtime(
                nonExistentMovie, // Non-existent movie
                "Updated Theater",
                18.99,
                Instant.now().plus(Duration.ofHours(6)),
                Instant.now().plus(Duration.ofHours(8))
        );
        assertThrows(InvalidMovieIdNotFoundException.class, () -> {
            showtimeService.updateShowtime(update, baseShowtime.getId());
        });
    }
    @Test
    void updateShowtime_InvalidDuration() {
        Showtime update = makeShowtime(
                testMovie,
                "Updated Theater",
                18.99,
                Instant.now().plus(Duration.ofHours(6)),
                // Movie duration is 90 minutes, this is too short
                Instant.now().plus(Duration.ofHours(6)).plus(Duration.ofMinutes(60))
        );

        assertThrows(InvalidShowtimeDurationException.class, () -> {
            showtimeService.updateShowtime(update, baseShowtime.getId());
        });
    }

    @Test
    void updateShowtime_OverlapWithOtherShowtime() throws Exception {
        // First create another showtime
        Showtime anotherShowtime = makeShowtime(
                testMovie,
                "Screen 3",
                12.50,
                Instant.now().plus(Duration.ofHours(4)),
                Instant.now().plus(Duration.ofHours(6))
        );
        showtimeService.addShowtime(anotherShowtime);

        // Try to update baseShowtime to overlap with anotherShowtime
        Showtime update = makeShowtime(
                testMovie,
                "Screen 3", // Same theater as anotherShowtime
                18.99,
                Instant.now().plus(Duration.ofHours(5)), // Overlaps with anotherShowtime
                Instant.now().plus(Duration.ofHours(7))
        );

        assertThrows(ShowtimeOverlapException.class, () -> {
            showtimeService.updateShowtime(update, baseShowtime.getId());
        });
    }

    @Test
    void deleteShowtime_success() throws InvalidShowtimeIdNotFoundException {
        showtimeService.deleteShowtime(baseShowtime.getId());

        assertFalse(showtimeRepository.existsById(baseShowtime.getId()));
        assertEquals(0, showtimeRepository.count());
    }

    @Test
    void deleteShowtime_InvalidId() {
        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> {
            showtimeService.deleteShowtime(999L);
        });
    }
}

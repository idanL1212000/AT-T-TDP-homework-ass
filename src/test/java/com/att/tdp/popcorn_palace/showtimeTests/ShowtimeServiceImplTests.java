package com.att.tdp.popcorn_palace.showtimeTests;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.impl.MovieServiceImpl;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
import com.att.tdp.popcorn_palace.showTime.exception.InvalidShowtimeDurationException;
import com.att.tdp.popcorn_palace.showTime.exception.InvalidShowtimeIdNotFoundException;
import com.att.tdp.popcorn_palace.showTime.exception.ShowtimeOverlapException;
import com.att.tdp.popcorn_palace.showTime.impl.ShowtimeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowtimeServiceImplTests {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieServiceImpl movieService;

    @InjectMocks
    private ShowtimeServiceImpl showtimeService;

    private Movie movie;
    private Showtime showtime;

    @BeforeEach
    public void setUp() {
        // Create a sample movie
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setDuration(120); // 1.5 hours
        movie.setReleaseYear(2015);

        // Create a sample showtime
        showtime = new Showtime();
        showtime.setId(1L);
        showtime.setMovieId(movie.getId());
        showtime.setTheater("Test Theater");
        showtime.setPrice(10.0);
        showtime.setStartTime(Instant.now().plus(Duration.ofDays(1)));
        showtime.setEndTime(Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(150)));

    }

    @Test
    public void testGetShowtimeById_Exists() throws Exception{
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        // Act
        Showtime result = showtimeService.getShowtimeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(showtime, result);

    }

    @Test
    public void testGetShowtimeById_NotExists(){
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> showtimeService.getShowtimeById(1L));
    }

    @Test
    public void testAddShowtime_Success() throws Exception {
        // Arrange
        when(showtimeRepository.hasOverlappingShowtime(any(), any(), any(), any())).thenReturn(false);
        when(showtimeRepository.save(any())).thenReturn(showtime);
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));

        // Act
        Showtime savedShowtime = showtimeService.addShowtime(showtime);

        // Assert
        assertNotNull(savedShowtime);
        verify(showtimeRepository).save(showtime);
    }

    @Test
    public void testAddShowtime_InvalidMovieId() {
        // Arrange
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidMovieIdException.class, () -> {
            showtimeService.addShowtime(showtime);
        });
    }

    @Test
    public void testAddShowtime_OverlappingShowtime() {
        // Arrange
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.hasOverlappingShowtime(any(), any(), any(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(ShowtimeOverlapException.class, () -> {
            showtimeService.addShowtime(showtime);
        });
    }

    @Test
    public void testAddShowtime_EndTimeBeforeStartTime() {
        // Arrange
        Instant startTime = Instant.now().plus(Duration.ofDays(1));
        showtime.setStartTime(startTime);
        showtime.setEndTime(startTime.minus(Duration.ofHours(1))); // End time before start time
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));

        // Act & Assert
        assertThrows(InvalidShowtimeStartTimeEndTimeException.class, () -> {
            showtimeService.addShowtime(showtime);
        });
    }

    @Test
    public void testAddShowtime_InvalidShowtimeDurationMoreThanMovie() {
        // Arrange
        int duration = 90;
        movie.setDuration(duration); // Set movie duration to 90 minutes
        showtime.setEndTime(showtime.getStartTime().plus(Duration.ofMinutes(180)));// Set end time beyond allowed duration
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));

        // Act & Assert
        assertThrows(InvalidShowtimeDurationException.class, () -> {
            showtimeService.addShowtime(showtime);
        });
    }

    @Test
    public void testAddShowtime_InvalidShowtimeDurationLessThanMovie() {
        // Arrange
        int duration = 90;
        movie.setDuration(duration); // Set movie duration to 90 minutes
        showtime.setEndTime(showtime.getStartTime().plus(Duration.ofMinutes(30)));// Set end time before allowed duration
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));

        // Act & Assert
        assertThrows(InvalidShowtimeDurationException.class, () -> {
            showtimeService.addShowtime(showtime);
        });
    }

    @Test
    public void testUpdateShowtime_Success() throws Exception{
        // Arrange
        Long showtimeId = 1L;

        // Updated showtime with different values
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setMovieId(1L); // Different movie
        updatedShowtime.setTheater("New Theater"); // Different theater
        updatedShowtime.setPrice(15.0); // Different price
        updatedShowtime.setStartTime(Instant.now().plus(Duration.ofDays(2))); // Different start time
        updatedShowtime.setEndTime(Instant.now().plus(Duration.ofDays(2)).plus(Duration.ofMinutes(150))); // Different end time

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.hasOverlappingShowtime(any(), any(), any(), any())).thenReturn(false);

        // Act
        showtimeService.updateShowtime(updatedShowtime, showtimeId);

        // Assert
        verify(showtimeRepository).save(showtime);
        assertEquals(updatedShowtime.getMovieId(), showtime.getMovieId());
        assertEquals(updatedShowtime.getTheater(), showtime.getTheater());
        assertEquals(updatedShowtime.getPrice(), showtime.getPrice());
        assertEquals(updatedShowtime.getStartTime(), showtime.getStartTime());
        assertEquals(updatedShowtime.getEndTime(), showtime.getEndTime());
    }
    @Test
    public void testUpdateShowtime_InvalidShowtimeId() {
        // Arrange
        Long invalidShowtimeId = 999L;
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setMovieId(1L);
        updatedShowtime.setTheater("New Theater");
        updatedShowtime.setPrice(15.0);
        updatedShowtime.setStartTime(Instant.now().plus(Duration.ofDays(2)));
        updatedShowtime.setEndTime(Instant.now().plus(Duration.ofDays(2)).plus(Duration.ofMinutes(150)));

        when(showtimeRepository.findById(invalidShowtimeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> {
            showtimeService.updateShowtime(updatedShowtime, invalidShowtimeId);
        });
    }

    @Test
    public void testUpdateShowtime_InvalidUpdateShowtimeId() {
        // Arrange
        Long invalidShowtimeId = 1L;
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setId(-1L);
        updatedShowtime.setMovieId(1L);
        updatedShowtime.setTheater("New Theater");
        updatedShowtime.setPrice(15.0);
        updatedShowtime.setStartTime(Instant.now().plus(Duration.ofDays(2)));
        updatedShowtime.setEndTime(Instant.now().plus(Duration.ofDays(2)).plus(Duration.ofMinutes(150)));

        when(showtimeRepository.findById(invalidShowtimeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> {
            showtimeService.updateShowtime(updatedShowtime, invalidShowtimeId);
        });
    }

    @Test
    public void testUpdateShowtime_EndTimeBeforeStartTime() {
        // Arrange
        Long showtimeId = 1L;
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setMovieId(1L);
        updatedShowtime.setTheater("New Theater");
        updatedShowtime.setPrice(15.0);
        Instant startTime = Instant.now().plus(Duration.ofDays(2));
        updatedShowtime.setStartTime(startTime);
        updatedShowtime.setEndTime(startTime.minus(Duration.ofHours(1))); // End time before start time

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        lenient().when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));

        // Act & Assert
        assertThrows(InvalidShowtimeStartTimeEndTimeException.class, () -> {
            showtimeService.updateShowtime(updatedShowtime, showtimeId);
        });
    }

    @Test
    public void testUpdateShowtime_OverlappingShowtimeFails() {
        // Arrange
        Long showtimeId = 1L;

        // Create an existing showtime that conflicts
        Showtime existingConflictingShowtime = new Showtime();
        existingConflictingShowtime.setId(2L);
        existingConflictingShowtime.setMovieId(1L);
        existingConflictingShowtime.setTheater("Test Theater");
        existingConflictingShowtime.setStartTime(Instant.now().plus(Duration.ofHours(2)));
        existingConflictingShowtime.setEndTime(Instant.now().plus(Duration.ofHours(4)));

        // Prepare the updated showtime with a time that overlaps with the existing showtime
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setMovieId(1L);
        updatedShowtime.setTheater("Test Theater");
        updatedShowtime.setPrice(15.0);
        updatedShowtime.setStartTime(Instant.now().plus(Duration.ofHours(3)));
        updatedShowtime.setEndTime(Instant.now().plus(Duration.ofHours(5)));

        // Mock the repository calls
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.hasOverlappingShowtime(any(), any(), any(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(ShowtimeOverlapException.class, () -> {
            showtimeService.updateShowtime(updatedShowtime, showtimeId);
        });
    }

    @Test
    public void testUpdateShowtime_OverlappingShowtimeSucceedsWhenUpdatingSameShowtime() throws Exception {
        // Arrange
        Long showtimeId = 1L;
        // Prepare the updated showtime with a new time for the same showtime
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setId(showtimeId);  // Same ID as the existing showtime
        updatedShowtime.setMovieId(1L);
        updatedShowtime.setTheater("Test Theater");
        updatedShowtime.setPrice(15.0);
        updatedShowtime.setStartTime(Instant.now().plus(Duration.ofDays(1)));
        updatedShowtime.setEndTime(Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofMinutes(150)));

        // Mock the repository calls
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(movieService.getMovieById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.hasOverlappingShowtime(any(), any(), any(), any())).thenReturn(false);

        // Act
        showtimeService.updateShowtime(updatedShowtime, showtimeId);

        // Assert
        verify(showtimeRepository).save(showtime);
        assertEquals(updatedShowtime.getMovieId(), showtime.getMovieId());
        assertEquals(updatedShowtime.getTheater(), showtime.getTheater());
        assertEquals(updatedShowtime.getPrice(), showtime.getPrice());
        assertEquals(updatedShowtime.getStartTime(), showtime.getStartTime());
        assertEquals(updatedShowtime.getEndTime(), showtime.getEndTime());
    }

    @Test
    public void testDeleteShowtime_Success() throws InvalidShowtimeIdNotFoundException {
        // Arrange
        Long showtimeId = 1L;
        when(showtimeRepository.existsById(showtimeId)).thenReturn(true);

        // Act
        showtimeService.deleteShowtime(showtimeId);

        // Assert
        verify(showtimeRepository).deleteById(showtimeId);
    }

    @Test
    public void testDeleteShowtime_NonExistentShowtime() {
        // Arrange
        Long nonExistentShowtimeId = 999L;
        when(showtimeRepository.existsById(nonExistentShowtimeId)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> {
            showtimeService.deleteShowtime(nonExistentShowtimeId);
        });
    }

    @Test
    public void testDeleteShowtime_NegativeId() {
        // Arrange
        Long invalidShowtimeId = -1L;

        // Act & Assert
        assertThrows(InvalidShowtimeIdNotFoundException.class, () -> {
            showtimeService.deleteShowtime(invalidShowtimeId);
        });
    }

    @Test
    public void testDeleteShowtime_RepositoryThrowsException() {
        // Arrange
        Long showtimeId = 1L;
        when(showtimeRepository.existsById(showtimeId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(showtimeRepository).deleteById(showtimeId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            showtimeService.deleteShowtime(showtimeId);
        });
    }
}

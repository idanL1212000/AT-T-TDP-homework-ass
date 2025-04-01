package com.att.tdp.popcorn_palace.movieTests;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import com.att.tdp.popcorn_palace.movies.exceptions.*;
import com.att.tdp.popcorn_palace.movies.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MovieServiceImplTestsIn {

    @Autowired
    private MovieServiceImpl movieService;

    @Autowired
    private MovieRepository movieRepository;

    private Movie testMovie;

    @BeforeEach
    public void setUp() {
        movieRepository.deleteAll();

        testMovie = makeMovie("Test Movie","Drama",4.5,120,2023);;

        movieRepository.save(testMovie);
    }

    @Test
    public void testGetAllMovies() {
        // Arrange - setup is handled in setUp()

        // Act
        List<Movie> movies = movieService.getAllMovies();

        // Assert
        assertFalse(movies.isEmpty());
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getTitle());
    }

    @Test
    public void testAddMovie_Success() throws MovieAlreadyExistsException {
        // Arrange
        Movie newMovie = makeMovie("Another Movie","Comedy",8.8,100,2000);

        // Act
        Movie savedMovie = movieService.addMovie(newMovie);

        // Assert
        assertNotNull(savedMovie);
        assertNotNull(savedMovie.getId());
        assertEquals("Another Movie", savedMovie.getTitle());

        // Verify the movie was actually saved to the repository
        assertEquals(2, movieRepository.findAll().size());
    }

    @Test
    public void testAddMovie_AlreadyExists() {
        // Arrange
        Movie duplicateMovie = makeMovie("Test Movie","Drama",4.8,100,2024);

        // Act & Assert
        assertThrows(MovieAlreadyExistsException.class, () -> {
            movieService.addMovie(duplicateMovie);
        });
    }

    @Test
    public void testUpdateMovieByTitle_Success() throws InvalidMovieTitleNotFoundException, MovieAlreadyExistsException, UpdateMovieWithShowtimeException {
        // Arrange
        Movie updatedMovie = makeMovie("Updated Test Movie","Drama",4.8,150,2024);

        // Act
        movieService.updateMovieByTitle("Test Movie", updatedMovie);

        // Assert
        Movie retrievedMovie = movieRepository.findByTitle("Updated Test Movie").orElse(null);
        assertNotNull(retrievedMovie);
        assertEquals(updatedMovie.getTitle(), retrievedMovie.getTitle());
        assertEquals(updatedMovie.getGenre(), retrievedMovie.getGenre());
        assertEquals(updatedMovie.getDuration(), retrievedMovie.getDuration());
        assertEquals(updatedMovie.getRating(), retrievedMovie.getRating());
        assertEquals(updatedMovie.getReleaseYear(), retrievedMovie.getReleaseYear());
    }

    @Test
    public void testUpdateMovieByTitle_NotFound() {
        // Arrange
        Movie updatedMovie = makeMovie("New Title","Action",7.8,90,2005);

        // Act & Assert
        assertThrows(InvalidMovieTitleNotFoundException.class, () -> {
            movieService.updateMovieByTitle("Non-existent Movie", updatedMovie);
        });
    }

    @Test
    public void testUpdateMovieByTitle_DuplicateTitle() throws MovieAlreadyExistsException {
        // Arrange
        Movie anotherMovie = makeMovie("Another Movie","Comedy",8.8,100,2000);
        movieRepository.save(anotherMovie);

        Movie updatedMovie = makeMovie("Another Movie","Action",7.8,90,2005);

        // Act & Assert
        assertThrows(MovieAlreadyExistsException.class, () -> {
            movieService.updateMovieByTitle(testMovie.getTitle(), updatedMovie);
        });
    }

    @Test
    public void testUpdateMovieByTitle_WithShowtime() {
        testMovie.setShowtimes(List.of(makeShowtime(testMovie,"test",10.0,
                Instant.now().plus(1, ChronoUnit.HOURS),
                Instant.now().plus(3, ChronoUnit.HOURS))));

        Movie updatedMovie = makeMovie("Updated Title","Updated Genre",8.0,90,2000);

        // Act & Assert
        assertThrows(UpdateMovieWithShowtimeException.class, () -> {
            movieService.updateMovieByTitle(testMovie.getTitle(), updatedMovie);
        });
    }

    @Test
    public void testDeleteMovieByTitle_Success() throws InvalidMovieTitleNotFoundException {
        movieService.deleteMovieByTitle("Test Movie");

        // Assert
        assertEquals(0, movieRepository.findAll().size());
        assertTrue(movieRepository.findByTitle("Test Movie").isEmpty());
    }

    @Test
    public void testDeleteMovieByTitle_NotFound() {
        // Act & Assert
        assertThrows(InvalidMovieTitleNotFoundException.class, () -> {
            movieService.deleteMovieByTitle("Non-existent Movie");
        });
    }
}
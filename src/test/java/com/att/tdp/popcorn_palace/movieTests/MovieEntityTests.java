package com.att.tdp.popcorn_palace.movieTests;

import com.att.tdp.popcorn_palace.movies.Movie;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MovieEntityTests {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAllFieldsAreValid_thenNoViolations() {
        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setDuration(148);
        movie.setRating(8.8);
        movie.setReleaseYear(2010);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testTitleIsBlank_thenViolations() {
        Movie movie = new Movie();
        movie.setTitle("");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(7.5);
        movie.setReleaseYear(2020);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Movie Title is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testGenreIsBlank_thenViolations() {
        Movie movie = new Movie();
        movie.setTitle("The Dark Knight");
        movie.setGenre("");
        movie.setDuration(152);
        movie.setRating(9.0);
        movie.setReleaseYear(2008);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Movie Genre is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testDurationIsNegative_thenViolations() {
        Movie movie = new Movie();
        movie.setTitle("Interstellar");
        movie.setGenre("Sci-Fi");
        movie.setDuration(-10);
        movie.setRating(8.6);
        movie.setReleaseYear(2014);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Duration must be positive", violations.iterator().next().getMessage());
    }

    @Test
    public void testRatingIsOutOfRange_thenViolations() {
        Movie movie = new Movie();
        movie.setTitle("Pulp Fiction");
        movie.setGenre("Crime");
        movie.setDuration(154);
        movie.setRating(11.0);
        movie.setReleaseYear(1994);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Rating must not exceed 10.0", violations.iterator().next().getMessage());
    }

    @Test
    public void testReleaseYearIsInvalid_thenViolations() {
        Movie movie = new Movie();
        movie.setTitle("Future Movie");
        movie.setGenre("Sci-Fi");
        movie.setDuration(120);
        movie.setRating(8.0);
        movie.setReleaseYear(Year.now().getValue() + 5);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Release year must be less than or equal to three years in the future.", violations.iterator().next().getMessage());
    }

    @Test
    public void testReleaseYearIsNextYear_thenNoViolations() {
        Movie movie = new Movie();
        movie.setTitle("Coming Soon");
        movie.setGenre("Drama");
        movie.setDuration(120);
        movie.setRating(8.0);
        movie.setReleaseYear(Year.now().getValue() + 1); // Next year is valid

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertTrue(violations.isEmpty());
    }
}


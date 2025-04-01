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

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeMovie;
import static org.junit.jupiter.api.Assertions.*;

public class MovieEntityTest {


    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAllFieldsAreValid_thenNoViolations() {
        Movie movie = makeMovie("Avengers: Endgame","Action",8.4,181,2019);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testTitleIsBlank_thenViolations() {
        Movie movie = makeMovie("","Action",8.4,181,2019);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Movie Title is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testGenreIsBlank_thenViolations() {
        Movie movie = makeMovie("Avengers: Endgame","",8.4,181,2019);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Movie Genre is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testDurationIsNegative_thenViolations() {
        Movie movie = makeMovie("Avengers: Endgame","Action",8.4,-10,2019);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Duration must be positive", violations.iterator().next().getMessage());
    }

    @Test
    public void testDurationMoreThanFiveHours_thenViolations() {
        Movie movie = makeMovie("Avengers: Endgame","Action",8.4,301,2019);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Duration must not exceed 5 hours", violations.iterator().next().getMessage());
    }

    @Test
    public void testRatingIsOutOfRange_thenViolations() {
        Movie movie = makeMovie("Avengers: Endgame","Action",11.0,181,2019);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Rating must not exceed 10.0", violations.iterator().next().getMessage());
    }

    @Test
    public void testReleaseYearIsInvalid_thenViolations() {
        Movie movie = makeMovie("FutureMovie","Action",8.4,181,Year.now().getValue() + 5);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertEquals(1, violations.size());
        assertEquals("Release year must be less than or equal to three years in the future.", violations.iterator().next().getMessage());
    }

    @Test
    public void testReleaseYearIsNextYear_thenNoViolations() {
        Movie movie = makeMovie("FutureMovie","Action",8.4,181,Year.now().getValue() + 1);

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertTrue(violations.isEmpty());
    }
}

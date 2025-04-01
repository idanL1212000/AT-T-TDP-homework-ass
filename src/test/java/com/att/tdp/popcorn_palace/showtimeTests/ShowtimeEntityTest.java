package com.att.tdp.popcorn_palace.showtimeTests;

import com.att.tdp.popcorn_palace.showTime.Showtime;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ShowtimeEntityTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAllFieldsAreValid_thenNoViolations() {
        Showtime showtime = new Showtime();
        showtime.setMovieId(1L);
        showtime.setPrice(15.5);
        showtime.setTheater("Main Hall");
        showtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS)); // Future time
        showtime.setEndTime(Instant.now().plus(2, ChronoUnit.DAYS));

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testMovieIdIsNegative_thenViolations() {
        Showtime showtime = new Showtime();
        showtime.setMovieId(-5L);
        showtime.setPrice(15.5);
        showtime.setTheater("Main Hall");
        showtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        showtime.setEndTime(Instant.now().plus(2, ChronoUnit.DAYS));

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);
        assertEquals(1, violations.size());
        assertEquals("MovieId need to be greater than 0", violations.iterator().next().getMessage());
    }

    @Test
    public void testPriceIsNegative_thenViolations() {
        Showtime showtime = new Showtime();
        showtime.setMovieId(1L);
        showtime.setPrice(-10.0);
        showtime.setTheater("Main Hall");
        showtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        showtime.setEndTime(Instant.now().plus(2, ChronoUnit.DAYS));

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);
        assertEquals(1, violations.size());
        assertEquals("Price must be positive", violations.iterator().next().getMessage());
    }

    @Test
    public void testTheaterNameIsBlank_thenViolations() {
        Showtime showtime = new Showtime();
        showtime.setMovieId(1L);
        showtime.setPrice(15.5);
        showtime.setTheater("");
        showtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        showtime.setEndTime(Instant.now().plus(2, ChronoUnit.DAYS));

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);
        assertEquals(1, violations.size());
        assertEquals("Theater name is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testStartTimeIsNotInFuture_thenViolations() {
        Showtime showtime = new Showtime();
        showtime.setMovieId(1L);
        showtime.setPrice(15.5);
        showtime.setTheater("Main Hall");
        showtime.setStartTime(Instant.now().minus(1, ChronoUnit.DAYS)); // Past time
        showtime.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS));

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);
        assertEquals(1, violations.size());
        assertEquals("Start time must be in the future", violations.iterator().next().getMessage());
    }

    @Test
    public void testEndTimeIsNull_thenViolations() {
        Showtime showtime = new Showtime();
        showtime.setMovieId(1L);
        showtime.setPrice(15.5);
        showtime.setTheater("Main Hall");
        showtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        showtime.setEndTime(null); // Null end time

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);
        assertEquals(1, violations.size());
        assertEquals("End time is required", violations.iterator().next().getMessage());
    }
}
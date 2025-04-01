package com.att.tdp.popcorn_palace.showtimeTests;

import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ShowtimeRepositoryTests {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Showtime showtime1;
    private Showtime showtime2;

    @BeforeEach
    public void setUp() {
        showtime1 = new Showtime();
        showtime1.setMovieId(1L);
        showtime1.setTheater("Theater A");
        showtime1.setPrice(10.0);
        showtime1.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        showtime1.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));

        showtime2 = new Showtime();
        showtime2.setMovieId(2L);
        showtime2.setTheater("Theater A");
        showtime2.setPrice(12.0);
        showtime2.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS));
        showtime2.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(5, ChronoUnit.HOURS));

        entityManager.persist(showtime1);
        entityManager.persist(showtime2);
        entityManager.flush();
    }

    @Test
    public void testHasOverlappingShowtime_Overlapping() {
        // Arrange
        Showtime testShowtime = new Showtime();
        testShowtime.setMovieId(1L);
        testShowtime.setTheater("Theater A");
        testShowtime.setPrice(11.0);
        testShowtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS));
        testShowtime.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS));

        // Act
        boolean hasOverlap = showtimeRepository.hasOverlappingShowtime(
                testShowtime.getTheater(),
                testShowtime.getStartTime(),
                testShowtime.getEndTime(),
                -1L
        );

        // Assert
        assertTrue(hasOverlap);
    }

    @Test
    public void testHasOverlappingShowtime_NotOverlapping() {
        // Arrange
        Showtime testShowtime = new Showtime();
        testShowtime.setMovieId(1L);
        testShowtime.setTheater("Theater A");
        testShowtime.setPrice(11.0);
        testShowtime.setStartTime(Instant.now().plus(2, ChronoUnit.DAYS));
        testShowtime.setEndTime(Instant.now().plus(2, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));

        // Act
        boolean hasOverlap = showtimeRepository.hasOverlappingShowtime(
                testShowtime.getTheater(),
                testShowtime.getStartTime(),
                testShowtime.getEndTime(),
                -1L
        );

        // Assert
        assertFalse(hasOverlap);
    }

    @Test
    public void testHasOverlappingShowtime_NotOverlappingEdge() {
        // Arrange
        Showtime testShowtime = new Showtime();
        testShowtime.setMovieId(1L);
        testShowtime.setTheater("Theater A");
        testShowtime.setPrice(11.0);
        testShowtime.setStartTime(showtime1.getEndTime().plus(1,ChronoUnit.NANOS));
        testShowtime.setEndTime(showtime2.getStartTime().minus(1,ChronoUnit.NANOS));

        // Act
        boolean hasOverlap = showtimeRepository.hasOverlappingShowtime(
                testShowtime.getTheater(),
                testShowtime.getStartTime(),
                testShowtime.getEndTime(),
                -1L
        );

        // Assert
        assertFalse(hasOverlap);
    }

    @Test
    public void testHasOverlappingShowtime_NotOverlappingTheater() {
        // Arrange
        Showtime testShowtime = new Showtime();
        testShowtime.setMovieId(1L);
        testShowtime.setTheater("Theater B");
        testShowtime.setPrice(11.0);
        testShowtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(1,ChronoUnit.HOURS));
        testShowtime.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS));

        // Act
        boolean hasOverlap = showtimeRepository.hasOverlappingShowtime(
                testShowtime.getTheater(),
                testShowtime.getStartTime(),
                testShowtime.getEndTime(),
                -1L
        );

        // Assert
        assertFalse(hasOverlap);
    }

    @Test
    public void testFindOverlappingShowtimes() {
        // Arrange
        Showtime testShowtime = new Showtime();
        testShowtime.setMovieId(1L);
        testShowtime.setTheater("Theater A");
        testShowtime.setPrice(11.0);
        testShowtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS));
        testShowtime.setEndTime(showtime2.getStartTime());

        // Act
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                testShowtime.getTheater(),
                testShowtime.getStartTime(),
                testShowtime.getEndTime(),
                -1L
        );
    }

    @Test
    public void testFindOverlappingShowtimesWithoutHimself() {
        // Arrange
        showtime1.setEndTime(showtime2.getEndTime());

        // Act
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime1.getTheater(),
                showtime1.getStartTime(),
                showtime1.getEndTime(),
                showtime1.getId()
        );

        // Assert
        assertFalse(overlappingShowtimes.isEmpty());
        assertEquals(1, overlappingShowtimes.size());
        assertEquals(showtime2.getId(), overlappingShowtimes.get(0).getId());
    }

    @Test
    public void testFindOverlappingShowtimeDifferentTheater() {
        // Arrange
        Showtime testShowtime = new Showtime();
        testShowtime.setMovieId(1L);
        testShowtime.setTheater("Theater B");
        testShowtime.setPrice(11.0);
        testShowtime.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS));
        testShowtime.setEndTime(showtime2.getStartTime().plus(15,ChronoUnit.MINUTES));

        // Act
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                testShowtime.getTheater(),
                testShowtime.getStartTime(),
                testShowtime.getEndTime(),
                -1L
        );

        // Assert
        assertTrue(overlappingShowtimes.isEmpty());
    }

    @Test
    public void testHasOverlappingShowtime_OverlappingHimself() {

        // Act
        boolean hasOverlap = showtimeRepository.hasOverlappingShowtime(
                showtime1.getTheater(),
                showtime1.getStartTime(),
                showtime1.getEndTime(),
                showtime1.getId()
        );

        // Assert
        assertFalse(hasOverlap);
    }
}

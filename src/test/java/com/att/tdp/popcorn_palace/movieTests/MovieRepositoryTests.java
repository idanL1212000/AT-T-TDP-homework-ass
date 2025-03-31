package com.att.tdp.popcorn_palace.movieTests;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.makeMovie;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MovieRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void testFindByTitle_thenReturnMovie() {
        // given
        Movie movie = makeMovie("Avengers: Endgame","Action",8.4,181,2019);

        entityManager.persist(movie);
        entityManager.flush();

        // when
        Optional<Movie> found = movieRepository.findByTitle("Avengers: Endgame");

        // then
        assertTrue(found.isPresent());
        assertEquals("Avengers: Endgame", found.get().getTitle());
    }

    @Test
    public void testFindByNonExistingTitle_thenReturnEmpty() {
        // when
        Optional<Movie> found = movieRepository.findByTitle("Non-existing Movie");

        // then
        assertFalse(found.isPresent());
    }

    @Test
    public void testDeleteByTitle_thenRemoveMovie() {
        // given
        Movie movie = makeMovie("The Matrix","The Matrix",8.7,136,1999);

        entityManager.persist(movie);
        entityManager.flush();

        // when
        Long deletedCount = movieRepository.deleteByTitle("The Matrix");
        Optional<Movie> found = movieRepository.findByTitle("The Matrix");

        // then
        assertEquals(1L, deletedCount);
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindById_thenReturnMovie() {
        // given
        Movie movie = makeMovie("Joker","Drama",8.4,122,2019);

        Movie savedMovie = entityManager.persist(movie);
        entityManager.flush();

        // when
        Optional<Movie> found = movieRepository.findById(savedMovie.getId());

        // then
        assertTrue(found.isPresent());
        assertEquals("Joker", found.get().getTitle());
    }
}

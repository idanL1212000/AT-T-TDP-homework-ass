package com.att.tdp.popcorn_palace.movies;

import com.att.tdp.popcorn_palace.movies.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MoviesController {

    private final MovieService movieService;

    public MoviesController(MovieService movieService){
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) throws MovieAlreadyExistsException {
        return ResponseEntity.ok(movieService.addMovie(movie));
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Void> updateMovie(@PathVariable String movieTitle,@Valid @RequestBody Movie newMovieData)
            throws InvalidMovieTitleNotFoundException, MovieAlreadyExistsException {
        movieService.updateMovieByTitle(movieTitle, newMovieData);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) throws InvalidMovieTitleNotFoundException {
        movieService.deleteMovieByTitle(movieTitle);
        return ResponseEntity.ok().build();
    }
}



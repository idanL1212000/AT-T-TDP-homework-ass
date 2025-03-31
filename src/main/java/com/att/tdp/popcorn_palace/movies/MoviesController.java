package com.att.tdp.popcorn_palace.movies;

import com.att.tdp.popcorn_palace.movies.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MoviesController {

    private MovieService movieService;

    public MoviesController(MovieService movieService){
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) throws MovieAlreadyExistsException {
        return new ResponseEntity<>(movieService.addMovie(movie), HttpStatus.OK);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<String> updateMovie(@PathVariable String movieTitle,@Valid @RequestBody Movie newMovieData)
            throws InvalidMovieTitleNotFoundException, MovieAlreadyExistsException {
        movieService.updateMovieByTitle(movieTitle, newMovieData);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        if(movieService.deleteMovieByTitle(movieTitle)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}


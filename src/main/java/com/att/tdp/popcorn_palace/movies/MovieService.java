package com.att.tdp.popcorn_palace.movies;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> getAllMovies();

    Optional<Movie> getMovieById(Long movieId);

    Movie addMovie(Movie movie) throws com.embarkx.FirstSpring.movies.exceptions.MovieAlreadyExistsException;

    void updateMovieByTitle(String movieTitle, Movie newMovieData) throws com.embarkx.FirstSpring.movies.exceptions.InvalidMovieTitleException, com.embarkx.FirstSpring.movies.exceptions.MovieAlreadyExistsException;

    boolean deleteMovieByTitle(String movieTitle);

}

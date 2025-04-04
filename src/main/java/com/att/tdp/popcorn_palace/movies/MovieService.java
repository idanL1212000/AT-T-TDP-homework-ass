package com.att.tdp.popcorn_palace.movies;

import com.att.tdp.popcorn_palace.movies.exceptions.*;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> getAllMovies();

    Movie addMovie(Movie movie) throws MovieAlreadyExistsException;

    void updateMovieByTitle(String movieTitle, Movie newMovieData)
            throws InvalidMovieTitleNotFoundException, MovieAlreadyExistsException, UpdateMovieWithShowtimeException;

    void deleteMovieByTitle(String movieTitle) throws InvalidMovieTitleNotFoundException;

}

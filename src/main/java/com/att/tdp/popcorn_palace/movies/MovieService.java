package com.att.tdp.popcorn_palace.movies;

import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();

    Movie addMovie(Movie movie);

    boolean updateMovieByTitle(String movieTitle, Movie newMovieData);

    boolean deleteMovieByTitle(String movieTitle);

    boolean TitleIsUsed(String movieTitle);
}

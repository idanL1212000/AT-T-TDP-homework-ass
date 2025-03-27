package com.att.tdp.popcorn_palace.movies.impl;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import com.att.tdp.popcorn_palace.movies.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public boolean updateMovieByTitle(String movieTitle, Movie newMovieData) {
        Optional<Movie> movieOptional = movieRepository.findByTitle(movieTitle);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            movie.setTitle(newMovieData.getTitle());
            movie.setGenre(newMovieData.getGenre());
            movie.setDuration(newMovieData.getDuration());
            movie.setRating(newMovieData.getRating());
            movie.setReleaseYear(newMovieData.getReleaseYear());
            movieRepository.save(movie);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteMovieByTitle(String movieTitle) {
        if (movieRepository.findByTitle(movieTitle).isEmpty()) {
            return false; // Ensures 404 when the movie does not exist
        }
        Long deletedCount = movieRepository.deleteByTitle(movieTitle);
        return deletedCount > 0; // Ensures 200 when deletion is successful
    }

    @Override
    public boolean TitleIsUsed(String movieTitle) {
        Movie movie = movieRepository.findByTitle(movieTitle).orElse(null);
        if (movie == null) {return false;}
        return true;
    }
}

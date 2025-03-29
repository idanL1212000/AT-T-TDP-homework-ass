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

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Optional<Movie> getMovieById(Long movieId) {
        return movieRepository.findById(movieId);
    }


    @Override
    public Movie addMovie(Movie movie) throws com.embarkx.FirstSpring.movies.exceptions.MovieAlreadyExistsException {
        Optional<Movie> existingMovie = movieRepository.findByTitle(movie.getTitle());
        if (existingMovie.isPresent()) {
            throw new com.embarkx.FirstSpring.movies.exceptions.MovieAlreadyExistsException();
        }
        return movieRepository.save(movie);
    }

    @Override
    public void updateMovieByTitle(String movieTitle, Movie newMovieData) throws com.embarkx.FirstSpring.movies.exceptions.InvalidMovieTitleException, com.embarkx.FirstSpring.movies.exceptions.MovieAlreadyExistsException {
        Optional<Movie> optionalMovie = movieRepository.findByTitle(movieTitle);
        if (optionalMovie.isEmpty()){
            throw new com.embarkx.FirstSpring.movies.exceptions.InvalidMovieTitleException();
        }
        if(!movieTitle.equals(newMovieData.getTitle()) && movieRepository.findByTitle(newMovieData.getTitle()).isPresent()){
            throw new com.embarkx.FirstSpring.movies.exceptions.MovieAlreadyExistsException();
        }
        Movie movie = optionalMovie.get();
        movie.setTitle(newMovieData.getTitle());
        movie.setGenre(newMovieData.getGenre());
        movie.setDuration(newMovieData.getDuration());
        movie.setRating(newMovieData.getRating());
        movie.setReleaseYear(newMovieData.getReleaseYear());
        movieRepository.save(movie);
    }

    @Override
    @Transactional
    public boolean deleteMovieByTitle(String movieTitle) {
        if (movieRepository.findByTitle(movieTitle).isEmpty()) {
            return false;
        }
        Long deletedCount = movieRepository.deleteByTitle(movieTitle);
        return deletedCount>0;
    }
}


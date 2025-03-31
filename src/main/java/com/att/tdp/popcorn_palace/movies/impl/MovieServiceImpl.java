package com.att.tdp.popcorn_palace.movies.impl;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import com.att.tdp.popcorn_palace.movies.MovieService;
import com.att.tdp.popcorn_palace.movies.exceptions.*;
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
    public Movie addMovie(Movie movie) throws MovieAlreadyExistsException {
        Optional<Movie> existingMovie = movieRepository.findByTitle(movie.getTitle());
        if (existingMovie.isPresent()) {
            throw new MovieAlreadyExistsException();
        }
        return movieRepository.save(movie);
    }

    @Override
    public void updateMovieByTitle(String movieTitle, Movie newMovieData)
            throws InvalidMovieTitleNotFoundException,
            MovieAlreadyExistsException,UpdateMovieWithShowtimeException {
        Optional<Movie> optionalMovie = movieRepository.findByTitle(movieTitle);
        if (optionalMovie.isEmpty()){
            throw new InvalidMovieTitleNotFoundException();
        }
        if(optionalMovie.get().getShowtimes() != null){
            throw new UpdateMovieWithShowtimeException();
        }
        if(!movieTitle.equals(newMovieData.getTitle()) && movieRepository.findByTitle(newMovieData.getTitle()).isPresent()){
            throw new MovieAlreadyExistsException();
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
    public void deleteMovieByTitle(String movieTitle) throws InvalidMovieTitleNotFoundException {
        movieRepository.findByTitle(movieTitle)
                .orElseThrow(InvalidMovieTitleNotFoundException::new);
        movieRepository.deleteByTitle(movieTitle);
    }
}


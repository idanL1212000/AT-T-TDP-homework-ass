package com.embarkx.FirstSpring.movies.exceptions;

public class InvalidMovieIdException extends Exception {
    public InvalidMovieIdException() {
        super("Movie Id Not Found");
    }
}

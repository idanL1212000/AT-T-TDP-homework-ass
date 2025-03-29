package com.att.tdp.popcorn_palace.movies.exceptions;

public class InvalidMovieIdException extends Exception {
    public InvalidMovieIdException() {
        super("Movie Id Not Found");
    }
}

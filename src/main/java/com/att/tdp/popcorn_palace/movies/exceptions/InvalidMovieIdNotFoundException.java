package com.att.tdp.popcorn_palace.movies.exceptions;

public class InvalidMovieIdNotFoundException extends Exception {
    public InvalidMovieIdNotFoundException() {
        super("Movie Id Not Found");
    }

}

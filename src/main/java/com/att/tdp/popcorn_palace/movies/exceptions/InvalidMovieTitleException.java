package com.att.tdp.popcorn_palace.movies.exceptions;

public class InvalidMovieTitleException extends Exception {
    public InvalidMovieTitleException() {
        super("Movie Title Not Found.");
    }
}

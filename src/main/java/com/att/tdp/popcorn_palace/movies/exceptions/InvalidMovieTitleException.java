package com.embarkx.FirstSpring.movies.exceptions;

public class InvalidMovieTitleException extends Exception {
    public InvalidMovieTitleException() {
        super("Movie Title Not Found.");
    }
}

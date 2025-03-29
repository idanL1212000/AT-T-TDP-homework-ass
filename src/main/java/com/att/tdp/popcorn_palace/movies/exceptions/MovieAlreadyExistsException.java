package com.embarkx.FirstSpring.movies.exceptions;

public class MovieAlreadyExistsException extends Exception {
    public MovieAlreadyExistsException() {
        super("A movie with this title already exists.");
    }
}

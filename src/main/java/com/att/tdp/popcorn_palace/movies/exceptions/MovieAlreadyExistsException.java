package com.att.tdp.popcorn_palace.movies.exceptions;

public class MovieAlreadyExistsException extends Exception {
    public MovieAlreadyExistsException() {
        super("A movie with this title already exists.");
    }
}

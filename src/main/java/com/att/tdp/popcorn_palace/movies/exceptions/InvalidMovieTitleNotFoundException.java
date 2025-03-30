package com.att.tdp.popcorn_palace.movies.exceptions;

public class InvalidMovieTitleNotFoundException extends Exception {
    public InvalidMovieTitleNotFoundException() {
        super("Movie Title Not Found.");
    }

}

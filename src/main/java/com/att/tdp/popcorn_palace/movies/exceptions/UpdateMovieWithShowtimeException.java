package com.att.tdp.popcorn_palace.movies.exceptions;

public class UpdateMovieWithShowtimeException extends RuntimeException {
    public UpdateMovieWithShowtimeException() {
        super("Cant update movie with active showtime");
    }
}

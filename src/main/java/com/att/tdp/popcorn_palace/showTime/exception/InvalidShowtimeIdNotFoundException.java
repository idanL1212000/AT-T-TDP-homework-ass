package com.att.tdp.popcorn_palace.showTime.exception;

public class InvalidShowtimeIdNotFoundException extends Exception {
    public InvalidShowtimeIdNotFoundException() {
        super("Showtime id not found.");
    }

    public InvalidShowtimeIdNotFoundException(String message) {
        super(message);
    }
}

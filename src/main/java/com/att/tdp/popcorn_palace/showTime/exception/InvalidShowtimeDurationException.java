package com.att.tdp.popcorn_palace.showTime.exception;

public class InvalidShowtimeDurationException extends Exception {

    public InvalidShowtimeDurationException(long showtimeMinutes, long movieMinutes, long maxMinutes) {
    super(String.format(
            "Showtime duration (%d minutes) must be between movie duration (%d minutes) and movie duration + 30 minutes (%d minutes)",
            showtimeMinutes,
            movieMinutes,
            maxMinutes));
    }

    public InvalidShowtimeDurationException(String message) {
        super(message);
    }
}

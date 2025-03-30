package com.att.tdp.popcorn_palace.showTime.exception;

public class UpdateShowtimeWithBookingsException extends Exception {
    public UpdateShowtimeWithBookingsException() {
        super("Cant update showtime with bookings");
    }
}

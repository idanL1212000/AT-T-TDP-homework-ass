package com.att.tdp.popcorn_palace.booking.exception;

public class SeatAlreadyBookedException extends Exception {
    public SeatAlreadyBookedException(Long showtimeId, Integer seatNumber) {
        super("Seat " + seatNumber + " is already booked for showtime " + showtimeId);
    }
}

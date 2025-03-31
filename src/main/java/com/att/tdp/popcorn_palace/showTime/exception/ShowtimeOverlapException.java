package com.att.tdp.popcorn_palace.showTime.exception;

public class ShowtimeOverlapException extends Exception{
    public ShowtimeOverlapException(String theater,String conflictDetails) {
        super("Showtime conflicts with existing showtimes in " +
                theater + ". Conflicting showtimes: " + conflictDetails);
    }

    public ShowtimeOverlapException(String message) {
        super(message);
    }
}

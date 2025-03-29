package com.att.tdp.popcorn_palace.showTime.exception;

public class InvalidShowtimeStartTimeEndTimeException extends Exception {
    public InvalidShowtimeStartTimeEndTimeException() {
        super("end time need to be after start time");
    }

    public InvalidShowtimeStartTimeEndTimeException(String message) {
        super(message);
    }
}

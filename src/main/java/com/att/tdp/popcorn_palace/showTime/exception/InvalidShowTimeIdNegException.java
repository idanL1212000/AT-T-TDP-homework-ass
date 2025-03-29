package com.att.tdp.popcorn_palace.showTime.exception;

public class InvalidShowTimeIdNegException extends RuntimeException {
    public InvalidShowTimeIdNegException() {
        super("Id needs to be a positive number");
    }

    public InvalidShowTimeIdNegException(String message) {
        super(message);
    }
}

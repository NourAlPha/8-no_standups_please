package com.example.exception;

public class InvalidActionException extends RuntimeException {
    public InvalidActionException(final String message) {
        super("Invalid action: " + message);
    }
}

package ru.practicum.stats.server.exception;

public class EmptyDateException extends RuntimeException {
    public EmptyDateException(String message) {
        super(message);
    }
}
package ru.practicum.ewm.exception;

public class StatsServiceException extends RuntimeException {
    public StatsServiceException(final String message) {
        super(message);
    }
}

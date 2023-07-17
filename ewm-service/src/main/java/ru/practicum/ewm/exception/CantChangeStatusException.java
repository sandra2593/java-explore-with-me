package ru.practicum.ewm.exception;

public class CantChangeStatusException extends RuntimeException {
    public CantChangeStatusException(String message) {
        super(message);
    }
}

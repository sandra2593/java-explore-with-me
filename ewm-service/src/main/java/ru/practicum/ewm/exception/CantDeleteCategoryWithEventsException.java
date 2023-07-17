package ru.practicum.ewm.exception;

public class CantDeleteCategoryWithEventsException extends RuntimeException {
    public CantDeleteCategoryWithEventsException(String message) {
        super(message);
    }
}

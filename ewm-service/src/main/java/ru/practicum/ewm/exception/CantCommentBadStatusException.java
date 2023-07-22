package ru.practicum.ewm.exception;

public class CantCommentBadStatusException extends RuntimeException {
    public CantCommentBadStatusException(String message) {
        super(message);
    }
}

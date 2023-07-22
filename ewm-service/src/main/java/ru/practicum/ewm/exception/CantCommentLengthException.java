package ru.practicum.ewm.exception;

public class CantCommentLengthException extends RuntimeException {
    public CantCommentLengthException(String message) {
        super(message);
    }
}

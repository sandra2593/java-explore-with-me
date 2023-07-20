package ru.practicum.ewm.exception;

public class CantDeleteCommentException extends RuntimeException {
    public CantDeleteCommentException(String message) {
        super(message);
    }
}

package ru.practicum.ewm.exception;

public class CantParticipateInEventException extends RuntimeException {
    public CantParticipateInEventException(String message) {
        super(message);
    }
}

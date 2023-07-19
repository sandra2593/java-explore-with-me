package ru.practicum.ewm.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.*;

import java.util.Map;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler({DuplicateException.class, CantChangeStatusException.class, CantParticipateInEventException.class, CantDeleteCategoryWithEventsException.class, EventStatusException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(EventDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(StatsServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerErrorExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }
}

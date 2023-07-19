package ru.practicum.stats.server.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stats.server.exception.EmptyDateException;
import ru.practicum.stats.server.exception.PeriodDateException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler({EmptyDateException.class, PeriodDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }
}

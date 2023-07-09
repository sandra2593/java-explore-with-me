package ru.practicum.stats.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.stats.dto.HitDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsGatewayController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> create(@RequestBody @Valid HitDto hitDto) {
        return statsClient.create(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end, @RequestParam(required = false, defaultValue = "") List<String> uris, @RequestParam(required = false, defaultValue = "false") boolean unique) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата начала не позже даты конца");
        }

        return statsClient.get(start, end, uris, unique);
    }
}

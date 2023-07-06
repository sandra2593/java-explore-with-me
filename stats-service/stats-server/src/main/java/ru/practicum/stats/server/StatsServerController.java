package ru.practicum.stats.server;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;
import ru.practicum.stats.server.service.StatsServerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class StatsServerController {
    private final StatsServerService statsService;

    @Autowired
    public StatsServerController(StatsServerService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public ResponseEntity<Object> create(@RequestBody HitDto hitDto) {
        statsService.create(hitDto);
        return new ResponseEntity<>(Map.of("message", "Информация добавлена"), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<HitStatsDto> getStats(
            @RequestParam List<String> uris,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam boolean unique
    ) {
        return statsService.getStats(uris, start, end, unique);
    }
}

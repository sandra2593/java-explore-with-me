package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerServiceInt {
    void create(HitDto hitDto);

    List<HitStatsDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, boolean unique);
}

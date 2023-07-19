package ru.practicum.ewm.stats;

import ru.practicum.stats.dto.HitStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServiceIntf {
    void create(String ipAddr, String requestUri);

    List<HitStatsDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, boolean unique);

}

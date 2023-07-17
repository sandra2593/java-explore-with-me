package ru.practicum.stats.server.service;

import jdk.jfr.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;
import ru.practicum.stats.server.exception.EmptyDateException;
import ru.practicum.stats.server.exception.PeriodDateException;
import ru.practicum.stats.server.mapper.HitMapper;
import ru.practicum.stats.server.storage.StatsServerStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsServerService implements StatsServerServiceInt {
    private final StatsServerStorage statsStorage;

    @Autowired
    public StatsServerService(StatsServerStorage statsStorage) {
        this.statsStorage = statsStorage;
    }

    @Override
    public void create(HitDto hitDto) {
        statsStorage.save(HitMapper.fromDto(hitDto));
    }

    @Override
    public List<HitStatsDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, boolean unique) {
        if (start == null || end == null) {
            throw new EmptyDateException("даты начала и конца непустые");
        }
        if ((start).isAfter(end)) {
            throw new PeriodDateException("дата начала большее даты конца");
        }

        if (uris.isEmpty()) {
            if (unique) {
                return statsStorage.findAllUniqueHits(start, end);
            } else {
                return statsStorage.findAllHits(start, end);
            }
        } else {
            if (unique) {
                return statsStorage.findUniqueHitsByUri(uris, start, end);
            } else {
                return statsStorage.findAllHitsByUri(uris, start, end);
            }
        }
    }
}

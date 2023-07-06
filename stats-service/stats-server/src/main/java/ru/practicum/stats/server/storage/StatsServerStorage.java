package ru.practicum.stats.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.dto.HitStatsDto;
import ru.practicum.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerStorage extends JpaRepository<Hit, Integer> {
    @Query(
            "SELECT new ru.practicum.stats.dto.HitStatsDto(h.app, h.uri, COUNT(h.ip)) " +
                    "FROM Hit h " +
                    "WHERE h.uri in :uris AND timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h.ip) DESC"
    )
    List<HitStatsDto> getAllHitsByUri(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(
            "SELECT new ru.practicum.stats.dto.HitStatsDto(h.app, h.uri, COUNT(h.ip)) " +
                    "FROM Hit h " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h.ip) DESC"
    )
    List<HitStatsDto> getAllHits(LocalDateTime start, LocalDateTime end);

    @Query(
            "SELECT new ru.practicum.stats.dto.HitStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
                    "FROM Hit h " +
                    "WHERE h.uri in :uris AND timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h.ip) DESC"
    )
    List<HitStatsDto> getUniqueHitsByUri(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(
            "SELECT new ru.practicum.stats.dto.HitStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
                    "FROM Hit h " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h.ip) DESC"
    )
    List<HitStatsDto> getAllUniqueHits(LocalDateTime start, LocalDateTime end);

}

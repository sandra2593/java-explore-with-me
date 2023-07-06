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
    List<HitStatsDto> findAllHitsByUri(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(
            "SELECT new ru.practicum.stats.dto.HitStatsDto(h.app, h.uri, COUNT(h.ip)) " +
                    "FROM Hit h " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h.ip) DESC"
    )
    List<HitStatsDto> findAllHits(LocalDateTime start, LocalDateTime end);

    @Query(
            "SELECT new ru.practicum.stats.dto.HitStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
                    "FROM Hit h " +
                    "WHERE h.uri in :uris AND timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h.ip) DESC"
    )
    List<HitStatsDto> findUniqueHitsByUri(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(
            "SELECT new ru.practicum.stats.dto.HitStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
                    "FROM Hit h " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h.ip) DESC"
    )
    List<HitStatsDto> findAllUniqueHits(LocalDateTime start, LocalDateTime end);

}

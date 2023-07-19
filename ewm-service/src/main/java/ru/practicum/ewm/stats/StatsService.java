package ru.practicum.ewm.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.StatsServiceException;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;
import ru.practicum.stats.gateway.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatsService implements StatsServiceIntf {
    private final StatsClient statsClient;
    private final DateTimeFormatter dateTimeFormatter;
    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    public StatsService(@Value("${spring.application.name}") String appName, @Value("${format.pattern.datetime}") String dateTimeFormat, StatsClient statsClient) {
        this.appName = appName;
        this.statsClient = statsClient;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    }


    @Override
    public void create(String ipAddress, String requestUri) {
        HitDto hitDto = HitDto.builder().app(appName).ip(ipAddress).uri(requestUri).timestamp(LocalDateTime.now()).build();
        ResponseEntity<Object> response = statsClient.create(hitDto);
        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new StatsServiceException("ошибка при сохрании данных в сервис статистики");
        }
    }

    @Override
    public List<HitStatsDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, boolean unique) {
        if (start == null || end == null) {
            start = LocalDateTime.of(2000, 1, 1, 0, 0);
            end = LocalDateTime.of(9999, 1, 1, 0, 0);
        }
        if (Objects.isNull(unique)) {
            unique = false;
        }

        ResponseEntity<Object> response = statsClient.get(start.format(dateTimeFormatter), end.format(dateTimeFormatter), uris, unique);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new StatsServiceException("ошибка при получении данных из сервиса статистики");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> stats = (List<Object>) response.getBody();
        return stats.stream().map(object -> objectMapper.convertValue(object, HitStatsDto.class)).collect(Collectors.toList());
    }
}

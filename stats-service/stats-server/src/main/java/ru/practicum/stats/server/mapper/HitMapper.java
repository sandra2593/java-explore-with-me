package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.model.Hit;

public class HitMapper {
    public static Hit fromDto(HitDto hitDto) {
        Hit endpointHit = new Hit();
        endpointHit.setApp(hitDto.getApp());
        endpointHit.setUri(hitDto.getUri());
        endpointHit.setIp(hitDto.getIp());
        endpointHit.setTimestamp(hitDto.getTimestamp());

        return endpointHit;
    }
}

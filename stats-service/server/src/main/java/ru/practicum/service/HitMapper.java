package ru.practicum.service;

import lombok.NoArgsConstructor;
import dto.HitDto;
import ru.practicum.model.Hit;
import ru.practicum.utilities.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public final class HitMapper {
    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(),
                DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN)));
        return hit;
    }
}
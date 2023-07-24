package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dto.HitDto;
import dto.ViewStats;
import ru.practicum.repository.StatsRepository;
import ru.practicum.utilities.Constants;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    private static LocalDateTime getDateTime(String dateTime) {
        dateTime = URLDecoder.decode(dateTime, StandardCharsets.UTF_8);
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN));
    }

    @Override
    public void save(HitDto hitDto) {
        repository.save(HitMapper.toHit(hitDto));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        if (unique) {
            return repository.findUniqueViewStats(getDateTime(start), getDateTime(end), uris);
        } else {
            return repository.findViewStats(getDateTime(start), getDateTime(end), uris);
        }
    }
}
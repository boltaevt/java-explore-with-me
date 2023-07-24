package ru.practicum.service;

import dto.HitDto;
import dto.ViewStats;

import java.util.List;

public interface StatsService {

    void save(HitDto hitDto);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
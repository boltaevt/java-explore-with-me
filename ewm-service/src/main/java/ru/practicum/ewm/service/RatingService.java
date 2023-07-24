package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;

import java.util.LinkedList;

public interface RatingService {
    LinkedList<EventShortDto> getRating(boolean isRandom, boolean isAsc, String type, Integer from, Integer size);

    void setEventViewsCountAfterView(Long id);

    EventFullDto addVote(Long userId, boolean isPositive, Long eventId);

    EventFullDto deleteVote(Long userId, Long eventId);
}

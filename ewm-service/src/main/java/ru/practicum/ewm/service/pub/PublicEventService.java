package ru.practicum.ewm.service.pub;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid, String rangeStart,
                               String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                               Integer size, HttpServletRequest request);

    EventFullDto getById(Long id, HttpServletRequest request);
}

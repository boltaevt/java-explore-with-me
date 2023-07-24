package ru.practicum.ewm.service.admin;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.enums.State;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getAllByAdminRequest(List<Long> users, List<State> states, List<Long> categories,
                                            String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}

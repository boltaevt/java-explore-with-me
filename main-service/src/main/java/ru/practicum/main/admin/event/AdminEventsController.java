package ru.practicum.main.admin.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.service.EventsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.xml.bind.ValidationException;
import java.util.Collection;

@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {
    private final EventsService eventsService;

    @Autowired
    public AdminEventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public Collection<EventFullDto> getEvents(@RequestParam(required = false) Collection<Long> users,
                                              @RequestParam(required = false) Collection<EventState> states,
                                              @RequestParam(required = false) Collection<Long> categories,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) throws ValidationException {
        return eventsService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Validated @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventsService.updateAdminEvent(eventId, updateEventAdminRequest);
    }
}
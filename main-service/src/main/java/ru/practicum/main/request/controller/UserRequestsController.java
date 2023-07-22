package ru.practicum.main.request.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.UserRequestsService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/requests")
public class UserRequestsController {
    private final UserRequestsService userRequestsService;

    public UserRequestsController(UserRequestsService userRequestsService) {
        this.userRequestsService = userRequestsService;
    }

    @GetMapping
    public Collection<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        return userRequestsService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @RequestParam Long eventId) {
        return userRequestsService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return userRequestsService.cancelRequest(userId, requestId);
    }
}
package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.RatingService;
import ru.practicum.ewm.service.priv.PrivateEventService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final PrivateEventService service;
    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllByInitiatorId(@PathVariable Long userId,
                                                                   @RequestParam(defaultValue = "0") Integer from,
                                                                   @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(service.getAllByInitiatorId(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> save(@PathVariable Long userId,
                                             @RequestBody @Valid NewEventDto newEventDto) {
        return new ResponseEntity<>(service.save(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("{eventId}")
    public ResponseEntity<EventFullDto> getByIdAndInitiatorId(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return new ResponseEntity<>(service.getByIdAndInitiatorId(eventId, userId), HttpStatus.OK);
    }

    @PatchMapping("{eventId}")
    public ResponseEntity<EventFullDto> updateByInitiator(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return ResponseEntity.ok(service.updateByInitiator(eventId, userId, updateEventUserRequest));
    }

    @GetMapping("{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByEventIdAndInitiatorId(@PathVariable Long userId,
                                                                                            @PathVariable Long eventId) {
        return new ResponseEntity<>(service.getRequestsByEventIdAndInitiatorId(eventId, userId), HttpStatus.OK);
    }

    @PatchMapping("{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatusByInitiator(@PathVariable Long userId,
                                                                                         @PathVariable Long eventId,
                                                                                         @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return ResponseEntity.ok(service.updateRequestStatusByInitiator(eventId, userId, eventRequestStatusUpdateRequest));
    }

    @GetMapping("/rating")
    public ResponseEntity<List<EventShortDto>> getEventRating(@PathVariable Long userId,
                                                              @RequestParam(required = false, defaultValue = "false") boolean isRandom,
                                                              @RequestParam(required = false, defaultValue = "POSITIVE") String type,
                                                              @RequestParam(required = false, defaultValue = "false") boolean isAsc,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(ratingService.getRating(isRandom, isAsc, type, from, size), HttpStatus.OK);
    }

    @PostMapping("/vote/{eventId}")
    public ResponseEntity<EventFullDto> addVote(@PathVariable Long userId,
                           @RequestParam(required = false, defaultValue = "true") boolean isPositive,
                           @PathVariable("eventId") Long eventId) {
        return new ResponseEntity<>(ratingService.addVote(userId, isPositive, eventId), HttpStatus.CREATED);
    }

    @DeleteMapping("/vote/{eventId}")
    public ResponseEntity<EventFullDto> deleteVote(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        return new ResponseEntity<>(ratingService.deleteVote(userId, eventId), HttpStatus.OK);
    }
}

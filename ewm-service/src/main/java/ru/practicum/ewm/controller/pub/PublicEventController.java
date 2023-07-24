package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.RatingService;
import ru.practicum.ewm.service.pub.PublicEventService;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final PublicEventService service;
    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAll(@RequestParam(required = false) String text,
                                                      @RequestParam(required = false) List<Long> categories,
                                                      @RequestParam(required = false) Boolean paid,
                                                      @RequestParam(required = false) String rangeStart,
                                                      @RequestParam(required = false) String rangeEnd,
                                                      @RequestParam(defaultValue = "FALSE") Boolean onlyAvailable,
                                                      @RequestParam(required = false) String sort,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      HttpServletRequest request) {
        return ResponseEntity.ok(service.getAll(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getById(@PathVariable Long id,
                                                HttpServletRequest request) {
        return ResponseEntity.ok(service.getById(id, request));
    }

    @GetMapping("/rating")
    public ResponseEntity<LinkedList<EventShortDto>> getEventRating(@RequestParam(required = false, defaultValue = "false") boolean isRandom,
                                                                    @RequestParam(required = false, defaultValue = "POSITIVE") String type,
                                                                    @RequestParam(required = false, defaultValue = "false") boolean isAsc,
                                                                    @RequestParam(defaultValue = "0") Integer from,
                                                                    @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(ratingService.getRating(isRandom, isAsc, type, from, size), HttpStatus.OK);
    }
}

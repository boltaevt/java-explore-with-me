package ru.practicum.ewm.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.exception.DuplicateException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UnknownRatingTypeException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.Vote;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.repository.VoteRepository;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingServiceImpl implements RatingService {
    EventRepository eventRepository;
    VoteRepository voteRepository;
    UserRepository userRepository;

    @Transactional
    @Override
    public LinkedList<EventShortDto> getRating(boolean isRandom, boolean isAsc, String type, Integer from, Integer size) {
        LinkedList<EventShortDto> response = new LinkedList<>();
        if (isRandom) {
            Event randomEvent = eventRepository.findRandom();
            response.addFirst(EventMapper.toEventShortDto(randomEvent));
        }

        Set<EventShortDto> eventDtos;

        switch (type.toUpperCase()) {
            case "ALL":
                if (isAsc) {
                    eventDtos = voteRepository.findEventsByVoteCountAllAsc(PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                } else {
                    eventDtos = voteRepository.findEventsByVoteCountAllDesc(PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                }
                for (EventShortDto event : response) {
                    setEventViewsCountAfterView(event.getId());
                }
                return response;
            case "POSITIVE":
                if (isAsc) {
                    eventDtos = voteRepository.findEventsByVoteCountWithBooleanAsc(true, PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                } else {
                    eventDtos = voteRepository.findEventsByVoteCountWithBooleanDesc(true, PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                }
                for (EventShortDto event : response) {
                    setEventViewsCountAfterView(event.getId());
                }
                return response;
            case "NEGATIVE":
                if (isAsc) {
                    eventDtos = voteRepository.findEventsByVoteCountWithBooleanAsc(false, PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                } else {
                    eventDtos = voteRepository.findEventsByVoteCountWithBooleanDesc(false, PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                }
                for (EventShortDto event : response) {
                    setEventViewsCountAfterView(event.getId());
                }
                return response;
            case "VIEWCOUNT":
                if (isAsc) {
                    eventDtos = voteRepository.findEventsByViewsCountAsc(PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                } else {
                    eventDtos = voteRepository.findEventsByViewsCountDesc(PageRequest.of(from, size)).stream()
                            .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                    response.addAll(eventDtos);
                }
                for (EventShortDto event : response) {
                    setEventViewsCountAfterView(event.getId());
                }
            default:
                throw new UnknownRatingTypeException("Rating type " + type + " was not found.");
        }
    }

    @Override
    @Transactional
    public EventFullDto addVote(Long userId, boolean isPositive, Long eventId) {
        Event event = getEventByIdIdWithCheck(eventId);
        User user = getUserByIdWithCheck(userId);
        Vote voteCheck = voteRepository.findVoteByUserIdAndEventId(userId, eventId);
        if (voteCheck == null) {
            Vote vote = Vote.builder()
                    .user(user)
                    .event(event)
                    .isPositive(isPositive)
                    .build();

            voteRepository.save(vote);
        } else {
            throw new DuplicateException("Vote already added.");
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto deleteVote(Long userId, Long eventId) {
        Event event = getEventByIdIdWithCheck(eventId);
        getUserByIdWithCheck(userId);
        Vote vote = voteRepository.findVoteByUserIdAndEventId(userId, eventId);
        voteRepository.delete(vote);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public void setEventViewsCountAfterView(Long id) {
        Event event = getEventByIdIdWithCheck(id);
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
    }

    private Event getEventByIdIdWithCheck(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private User getUserByIdWithCheck(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
    }
}
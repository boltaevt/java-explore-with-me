package ru.practicum.main.event.mapper;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;

public class EventMapper {
    private EventMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Event toEvent(NewEventDto newEventDto, User user, Category category, Location location) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event,
                                              Integer views,
                                              Integer confirmedRequests,
                                              Integer ratingScore) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .ratingScore(ratingScore)
                .build();
    }

    public static EventShortDto toEventShortDto(Event event,
                                                CategoryDto categoryDto,
                                                UserShortDto userShortDto,
                                                Integer views,
                                                Integer confirmedRequests) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }
}
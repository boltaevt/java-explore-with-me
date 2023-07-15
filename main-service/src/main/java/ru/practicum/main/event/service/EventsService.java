package ru.practicum.main.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoriesRepository;
import ru.practicum.main.error.EditingErrorException;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.enums.AdminUpdateEventState;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.enums.EventUpdateRequestStatus;
import ru.practicum.main.event.enums.UserUpdateEventState;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.mapper.LocationMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.event.repository.LocationsRepository;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.mapper.RequestsMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.repository.RequestsRepository;
import ru.practicum.main.stat.StatService;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.event.EventRequestStatusUpdateRequest;
import ru.practicum.main.user.event.EventRequestStatusUpdateResult;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UsersRepository;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventsService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;
    private final LocationsRepository locationsRepository;
    private final RequestsRepository requestsRepository;
    private final StatService statService;

    public EventsService(EventsRepository eventsRepository,
                         UsersRepository usersRepository,
                         CategoriesRepository categoriesRepository,
                         LocationsRepository locationsRepository,
                         RequestsRepository requestsRepository,
                         StatService statService) {
        this.eventsRepository = eventsRepository;
        this.usersRepository = usersRepository;
        this.categoriesRepository = categoriesRepository;
        this.locationsRepository = locationsRepository;
        this.requestsRepository = requestsRepository;
        this.statService = statService;
    }

    public Collection<EventFullDto> getAdminEvents(Collection<Long> users,
                                                   Collection<EventState> states,
                                                   Collection<Long> categories,
                                                   String rangeStart,
                                                   String rangeEnd,
                                                   Integer from,
                                                   Integer size) throws ValidationException {
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, FORMATTER);
        checkDateValidity(start, end);

        Pageable pageable = PageRequest.of(from / size, size);

        Collection<Event> events = eventsRepository.getEvents(users, states, categories, start, end, pageable).toList();

        Map<Long, Integer> views = statService.getViews(events);
        Map<Long, Integer> confirmedRequests = statService.getConfirmedRequests(events);

        Collection<EventFullDto> eventFullDtos = new ArrayList<>();

        for (Event event : events) {
            int viewsCount = views.getOrDefault(event.getId(), 0);
            int confirmedRequestsCount = confirmedRequests.getOrDefault(event.getId(), 0);
            EventFullDto eventFullDto = EventMapper.toEventFullDto(
                    event,
                    viewsCount,
                    confirmedRequestsCount
            );

            if (eventFullDto != null) {
                eventFullDtos.add(eventFullDto);
            }
        }

        log.info("Запрошено {} событий", eventFullDtos.size());

        return eventFullDtos;
    }

    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = findEvent(eventId);

        LocalDateTime updatedEventDate = updateEventAdminRequest.getEventDate();
        if (updatedEventDate != null) {
            if (updatedEventDate.isBefore(LocalDateTime.now().plusHours(1)))
                throw new EditingErrorException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");

            event.setEventDate(updatedEventDate);
        }

        AdminUpdateEventState updatedAdminEventState = updateEventAdminRequest.getStateAction();
        if (updatedAdminEventState != null) {
            if (!event.getState().equals(EventState.PENDING) && updatedAdminEventState.equals(AdminUpdateEventState.PUBLISH_EVENT)) {
                throw new EditingErrorException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
            }

            if (event.getState().equals(EventState.PUBLISHED) && updatedAdminEventState.equals(AdminUpdateEventState.REJECT_EVENT)) {
                throw new EditingErrorException("Событие можно отклонить, только если оно еще не опубликовано.");
            }

            if (updatedAdminEventState.equals(AdminUpdateEventState.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }

            if (updatedAdminEventState.equals(AdminUpdateEventState.REJECT_EVENT))
                event.setState(EventState.CANCELED);
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(getCategory(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        LocationDto updatedLocationDto = updateEventAdminRequest.getLocation();
        if (updatedLocationDto != null) {
            Location updatedLocation = getLocationByLatAndLon(updatedLocationDto)
                    .orElse(addLocation(updatedLocationDto));
            event.setLocation(updatedLocation);
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        Map<Long, Integer> views = statService.getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = statService.getConfirmedRequests(List.of(event));
        event = eventsRepository.save(event);

        log.info("Обновлено событие {}", eventId);

        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public Collection<EventShortDto> getEvents(String text,
                                               Collection<Long> categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request) throws ValidationException {
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, FORMATTER);
        checkDateValidity(start, end);


        if (sort == null) sort = "id";
        if ("EVENT_DATE".equals(sort)) sort = "eventDate";

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(sort).descending());

        if (text != null) text = text.toLowerCase();
        Collection<Event> events = eventsRepository.getEvents(text, categories, paid, onlyAvailable, start, end, pageable).toList();

        statService.addHit(request);

        return getEventShortDtos(events);
    }

    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        Event event = findEvent(id);

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new EntityNotFoundException("Событие " + id + " не опубликовано.");
        }

        statService.addHit(request);
        Map<Long, Integer> views = statService.getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = statService.getConfirmedRequests(List.of(event));

        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public Collection<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        getUser(userId);

        Pageable pageable = PageRequest.of(from / size, size);

        Collection<Event> events = eventsRepository.findAllByInitiatorId(userId, pageable).toList();
        return getEventShortDtos(events);
    }

    public Event addUserEvent(Long userId, NewEventDto newEventDto) {
        User user = getUser(userId);
        Category category = getCategory(newEventDto.getCategory());
        Location location = addLocation(newEventDto.getLocation());

        Event event = eventsRepository.save(EventMapper.toEvent(newEventDto, user, category, location));
        log.info("Сохранено событие {}", event.getId());

        return event;
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));

        Map<Long, Integer> views = statService.getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = statService.getConfirmedRequests(List.of(event));

        log.info("Запрошено событие {}", eventId);

        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        getUser(userId);

        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EditingErrorException("Пользователь " + userId + " не является владельцем события " + eventId);
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EditingErrorException("Изменить можно только отмененные события или события в состоянии ожидания модерации.");
        }

        LocalDateTime updatedEventDate = updateEventUserRequest.getEventDate();
        if (updatedEventDate != null) {
            if (updatedEventDate.isBefore(LocalDateTime.now().plusHours(2)))
                throw new EditingErrorException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента.");

            event.setEventDate(updatedEventDate);
        }

        UserUpdateEventState updatedUserEventState = updateEventUserRequest.getStateAction();
        if (updatedUserEventState != null) {
            if (updatedUserEventState.equals(UserUpdateEventState.SEND_TO_REVIEW))
                event.setState(EventState.PENDING);
            if (updatedUserEventState.equals(UserUpdateEventState.CANCEL_REVIEW))
                event.setState(EventState.CANCELED);
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(getCategory(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        LocationDto updatedLocationDto = updateEventUserRequest.getLocation();
        if (updatedLocationDto != null) {
            Location updatedLocation = getLocationByLatAndLon(updatedLocationDto)
                    .orElse(addLocation(updatedLocationDto));
            event.setLocation(updatedLocation);
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        Map<Long, Integer> views = statService.getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = statService.getConfirmedRequests(List.of(event));
        event = eventsRepository.save(event);

        log.info("Обновлено событие {}", eventId);

        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public Collection<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        getUser(userId);
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EditingErrorException("Пользователь " + userId + " не является владельцем события " + eventId);
        }

        Collection<Request> requests = requestsRepository.findAllByEventId(eventId);

        log.info("Запрошено {} запросов", requests.size());

        return requests.stream()
                .map(RequestsMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateUserEventRequest(Long userId,
                                                                 Long eventId,
                                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        getUser(userId);
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EditingErrorException("Пользователь " + userId + " не является владельцем события " + eventId);
        }

        int confirmedRequests = statService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0) + 1;
        if (event.getParticipantLimit() != 0 && confirmedRequests > event.getParticipantLimit()) {
            throw new EditingErrorException("Превышено ограничение на количество участников в событии " + eventId);
        }

        Collection<Request> requests = requestsRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        for (Request request : requests) {
            if (!RequestStatus.PENDING.equals(request.getStatus())) {
                throw new EditingErrorException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }

            EventUpdateRequestStatus updateStatus = eventRequestStatusUpdateRequest.getStatus();
            if (EventUpdateRequestStatus.CONFIRMED.equals(updateStatus)) {
                request.setStatus(RequestStatus.CONFIRMED);
                requestsRepository.save(request);
                eventRequestStatusUpdateResult.getConfirmedRequests().add(RequestsMapper.toParticipationRequestDto(request));
            }

            if (EventUpdateRequestStatus.REJECTED.equals(updateStatus)) {
                if (RequestStatus.CONFIRMED.equals(request.getStatus())) {
                    throw new EditingErrorException("Нельзя отменить уже подтверждённый запрос.");
                }
                request.setStatus(RequestStatus.REJECTED);
                requestsRepository.save(request);
                eventRequestStatusUpdateResult.getRejectedRequests().add(RequestsMapper.toParticipationRequestDto(request));
            }
        }

        log.info("Обновлен запрос {}", eventId);

        return eventRequestStatusUpdateResult;
    }

    public Collection<EventShortDto> getEventShortDtos(Collection<Event> events) {
        Map<Long, Integer> views = statService.getViews(events);
        Map<Long, Integer> confirmedRequests = statService.getConfirmedRequests(events);

        Collection<EventShortDto> eventShortDtos = new ArrayList<>();

        for (Event event : events) {
            CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory());
            UserShortDto userShortDto = UserMapper.toUserShortDto(event.getInitiator());
            EventShortDto eventShortDto = EventMapper.toEventShortDto(
                    event,
                    categoryDto,
                    userShortDto,
                    views.get(event.getId()),
                    confirmedRequests.get(event.getId())
            );

            eventShortDtos.add(eventShortDto);
        }

        log.info("Запрошено {} событий", eventShortDtos.size());

        return eventShortDtos;
    }

    private void checkDateValidity(LocalDateTime start, LocalDateTime end) throws ValidationException {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }
    }

    private User getUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден."));
    }

    private Event findEvent(Long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));
    }

    private Category getCategory(Long catId) {
        return categoriesRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория " + catId + " не найдена"));
    }

    private Optional<Location> getLocationByLatAndLon(LocationDto locationDto) {
        return locationsRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
    }

    private Location addLocation(LocationDto locationDto) {
        return locationsRepository.save(LocationMapper.toLocation(locationDto));
    }
}
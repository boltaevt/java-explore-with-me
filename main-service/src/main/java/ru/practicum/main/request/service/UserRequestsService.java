package ru.practicum.main.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.error.EditingErrorException;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestsMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.repository.RequestsRepository;
import ru.practicum.main.stat.StatService;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UsersRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRequestsService {
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;

    private final StatService statService;

    public UserRequestsService(RequestsRepository requestsRepository,
                               UsersRepository usersRepository,
                               EventsRepository eventsRepository, StatService statService) {
        this.requestsRepository = requestsRepository;
        this.usersRepository = usersRepository;
        this.eventsRepository = eventsRepository;
        this.statService = statService;
    }

    public Collection<ParticipationRequestDto> getRequests(Long userId) {
        Collection<Request> requests = requestsRepository.findAllByRequesterId(userId);
        log.info("Запрошено {} запросов", requests.size());

        return requests.stream()
                .map(RequestsMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = checkUserExists(userId);
        Event event = checkEventExists(eventId);

        if (!requestsRepository.findAllByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new EditingErrorException("Запрос от пользователя " + userId + " на участие в событии " + eventId + " уже существует.");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new EditingErrorException("Инициатор события не может добавлять запрос на участие в нём.");
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new EditingErrorException("Событие " + eventId + " не опубликовано.");
        }

        RequestStatus status = RequestStatus.PENDING;
        if (event.getParticipantLimit().equals(0) || Boolean.TRUE.equals(!event.getRequestModeration())) {
            status = RequestStatus.CONFIRMED;
        }

        int confirmedRequests = statService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0) + 1;
        if (event.getParticipantLimit() != 0 && confirmedRequests > event.getParticipantLimit()) {
            throw new EditingErrorException("Превышено ограничение на количество участников в событии " + eventId);
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .status(status)
                .build();

        log.info("Создан запрос {}", request.getId());

        return RequestsMapper.toParticipationRequestDto(requestsRepository.save(request));
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExists(userId);

        Request request = requestsRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос " + requestId + " не найден."));

        if (RequestStatus.CONFIRMED.equals(request.getStatus())) {
            throw new EditingErrorException("Заявка " + requestId + " уже принята.");
        }

        request.setStatus(RequestStatus.CANCELED);

        log.info("Отменён запрос {}", request.getId());

        return RequestsMapper.toParticipationRequestDto(requestsRepository.save(request));
    }

    private User checkUserExists(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден."));
    }

    private Event checkEventExists(Long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));
    }
}
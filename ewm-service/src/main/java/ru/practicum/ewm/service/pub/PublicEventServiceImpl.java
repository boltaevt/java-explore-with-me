package ru.practicum.ewm.service.pub;

import client.HitClient;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.QEvent;
import ru.practicum.ewm.repository.EventRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.practicum.ewm.enums.State.PUBLISHED;
import static ru.practicum.ewm.mapper.DateTimeMapper.toLocalDateTime;
import static ru.practicum.ewm.mapper.EventMapper.toEventFullDto;
import static ru.practicum.ewm.mapper.EventMapper.toEventShortDtoList;
import static ru.practicum.ewm.mapper.HitMapper.toEndpointHit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicEventServiceImpl implements PublicEventService {
    EventRepository repository;
    HitClient hitClient;

    @Override
    public List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                      Integer size, HttpServletRequest request) {
        hitClient.save(toEndpointHit(request));

        Sort sortBy = Sort.unsorted();
        if (sort != null) {
            if (sort.toUpperCase().equals(EventSort.EVENT_DATE.toString())) {
                sortBy = Sort.by("eventDate");
            } else if (sort.toUpperCase().equals(EventSort.VIEWS.toString())) {
                sortBy = Sort.by("views");
            } else {
                throw new BadRequestException("Field: sort. Error: must be EVENT_DATE or VIEWS. Value: " + sort);
            }
        }

        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(event.state.eq(PUBLISHED));
        if (text != null && !text.isEmpty()) {
            conditions.add(event.annotation.toLowerCase().like('%' + text.toLowerCase() + '%')
                    .or(event.description.toLowerCase().like('%' + text.toLowerCase() + '%')));
        }
        if (categories != null && !categories.isEmpty()) {
            conditions.add(event.category.id.in(categories));
        }
        if (paid != null) {
            conditions.add(event.paid.eq(paid));
        }
        if (rangeStart != null && !rangeStart.isEmpty()) {
            conditions.add(event.eventDate.after(toLocalDateTime(rangeStart)));
        }
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            conditions.add(event.eventDate.before(toLocalDateTime(rangeEnd)));
        }
        if ((rangeStart == null || rangeStart.isEmpty()) && (rangeEnd == null || rangeEnd.isEmpty())) {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (onlyAvailable == Boolean.TRUE) {
            conditions.add(event.participantLimit.gt(event.confirmedRequests));
        }

        BooleanExpression expression = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        List<Event> events = StreamSupport.stream(repository.findAll(expression, sortBy).spliterator(), false)
                .skip(from).limit(size)
                .collect(Collectors.toList());

        return toEventShortDtoList(events);
    }

    @Transactional
    @Override
    public EventFullDto getById(Long id, HttpServletRequest request) {
        hitClient.save(toEndpointHit(request));

        Event event = getByIdWithCheck(id);

        if (!event.getState().equals(PUBLISHED)) {
            throw new ForbiddenException(String.format("Event with id=%d is not published", id));
        }

        event.setViews(event.getViews() + 1);
        repository.save(event);

        return toEventFullDto(event);
    }

    private Event getByIdWithCheck(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));
    }
}
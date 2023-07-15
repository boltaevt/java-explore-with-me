package ru.practicum.main.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.HitClient;
import ru.practicum.common_dto.ViewStatsDto;
import ru.practicum.main.error.StatGettingException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.request.model.RequestInfo;
import ru.practicum.main.request.repository.RequestsRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RequestsRepository requestsRepository;
    private final HitClient hitClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatService(RequestsRepository requestsRepository, HitClient hitClient) {
        this.requestsRepository = requestsRepository;
        this.hitClient = hitClient;
    }

    public Map<Long, Integer> getConfirmedRequests(Collection<Event> events) {
        Collection<RequestInfo> requestInfoCollection = requestsRepository.getConfirmedRequests(
                events.stream()
                        .map(Event::getId)
                        .collect(Collectors.toList())
        );

        Map<Long, Integer> confirmedRequests = new HashMap<>();

        for (RequestInfo requestInfo : requestInfoCollection) {
            confirmedRequests.put(requestInfo.getEventId(), requestInfo.getConfirmedRequestsCount().intValue());
        }

        log.info("Запрошено {} подтверждённых запросов", confirmedRequests.size());

        return confirmedRequests;
    }

    public Map<Long, Integer> getViews(Collection<Event> events) {
        Map<Long, Integer> views = new HashMap<>();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        Collection<String> uris = new ArrayList<>();

        for (Event event : events) {
            LocalDateTime publishedOn = event.getPublishedOn();

            if (publishedOn == null) continue;
            if (publishedOn.isBefore(start)) start = publishedOn;
            uris.add("/events/" + event.getId());
        }

        ResponseEntity<Object> response = hitClient.getViewStats(
                start.format(FORMATTER),
                end.format(FORMATTER),
                uris,
                true
        );

        try {
            Collection<ViewStatsDto> viewStatsDtos = List.of(objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));

            for (ViewStatsDto viewStatsDto : viewStatsDtos) {
                String[] uriElements = viewStatsDto.getUri().split("/");

                if (uriElements.length > 2) {
                    views.put(Long.parseLong(uriElements[2]), viewStatsDto.getHits());
                }
            }

            return views;
        } catch (JsonProcessingException exception) {
            throw new StatGettingException("Невозможно получить количество просмотров");
        }
    }

    public void addHit(HttpServletRequest request) {
        hitClient.addHit(request);
    }
}
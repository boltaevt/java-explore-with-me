package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common_dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Service
public class HitClient extends BaseClient {
    @Autowired
    public HitClient(@Value("${ewm-stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(String app, String uri, String ip) {
        EndpointHitDto endpointHiDto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        return post("/hit", endpointHiDto);
    }

    public ResponseEntity<Object> addHit(HttpServletRequest req) {
        return addHit(req.getContextPath(), req.getRequestURI(), req.getRemoteAddr());
    }

    public ResponseEntity<Object> getViewStats(String start, String end, Collection<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique
        );

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
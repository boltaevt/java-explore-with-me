package ru.practicum.service.hit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.common_dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.common_dto.ViewStatsDto(ap.name, eh.uri, count(eh.ip)) " +
            "from EndpointHit as eh " +
            "join App as ap on ap.id = eh.app.id " +
            "where eh.timestamp >= :start " +
            "and eh.timestamp <= :end " +
            "and (:uris is null or eh.uri in :uris) " +
            "group by ap.name, eh.uri " +
            "order by count(eh.ip) desc")
    Collection<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, Collection<String> uris);


    @Query("select new ru.practicum.common_dto.ViewStatsDto(ap.name, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit as eh " +
            "join App as ap on ap.id = eh.app.id " +
            "where eh.timestamp >= :start " +
            "and eh.timestamp <= :end " +
            "and ((coalesce(:uris, '') = '') or (eh.uri in :uris)) " +
            "group by ap.name, eh.uri " +
            "order by count(distinct eh.ip) desc")
    Collection<ViewStatsDto> getViewStatsUnique(LocalDateTime start, LocalDateTime end, Collection<String> uris);
}
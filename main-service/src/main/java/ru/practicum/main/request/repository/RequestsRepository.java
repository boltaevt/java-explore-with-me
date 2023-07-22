package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestInfo;

import java.util.Collection;

@Repository
public interface RequestsRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByRequesterId(Long requesterId);

    Collection<Request> findAllByRequesterIdAndEventId(Long requesterId, Long eventId);

    Collection<Request> findAllByEventId(Long eventId);

    Collection<Request> findAllByIdIn(Collection<Long> ids);

    @Query("select new ru.practicum.main.request.model.RequestInfo(req.event.id, count(req.id)) " +
            "from Request as req " +
            "where req.event.id in :eventIds " +
            "and req.status = 'CONFIRMED' " +
            "group by req.event.id")
    Collection<RequestInfo> getConfirmedRequests(Collection<Long> eventIds);
}
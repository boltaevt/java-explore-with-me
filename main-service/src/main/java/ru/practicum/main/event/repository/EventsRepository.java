package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Collection<Event> findAllByCategoryId(Long categoryId);

    @Query("select ev " +
            "from Event as ev " +
            "where (:users is null or ev.initiator.id in :users) " +
            "and (:states is null or ev.state in :states) " +
            "and (:categories is null or ev.category.id in :categories) " +
            "and (coalesce(:start, null) is null or ev.eventDate > :start) " +
            "and (coalesce(:end, null) is null or ev.eventDate < :end)")
    Page<Event> getEvents(Collection<Long> users,
                          Collection<EventState> states,
                          Collection<Long> categories,
                          LocalDateTime start,
                          LocalDateTime end,
                          Pageable pageable);

    @Query("select ev from " +
            "Event as ev " +
            "where (:text is null) or ((lower(ev.annotation) like %:text%) or (lower(ev.description) like %:text%)) " +
            "and (:categories is null or ev.category.id in :categories) " +
            "and (:paid is null or ev.paid = :paid) " +
            "and (ev.eventDate is null or ev.eventDate is not null) " +
            "and (false = :onlyAvailable or (true = :onlyAvailable and ev.participantLimit > 0)) " +
            "and (coalesce(:start, null) is null or ev.eventDate > :start) " +
            "and (coalesce(:end, null) is null or ev.eventDate < :end)")
    Page<Event> getEvents(String text,
                          Collection<Long> categories,
                          Boolean paid,
                          Boolean onlyAvailable,
                          LocalDateTime start,
                          LocalDateTime end,
                          Pageable pageable);
}
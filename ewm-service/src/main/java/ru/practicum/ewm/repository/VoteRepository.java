package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Vote;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select v from Vote v where v.user.id = ?1 and v.event.id = ?2")
    Vote findVoteByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT e, COUNT(v.event.id) AS vote_count " +
            "FROM Event e " +
            "LEFT JOIN Vote v ON e.id = v.event.id " +
            "WHERE v.isPositive = ?1 " +
            "GROUP BY e.id " +
            "ORDER BY vote_count DESC")
    List<Event> findEventsByVoteCountWithBooleanDesc(boolean isPositive, Pageable pageable);

    @Query("SELECT e, COUNT(v.event.id) AS vote_count " +
            "FROM Event e " +
            "LEFT JOIN Vote v ON e.id = v.event.id " +
            "WHERE v.isPositive = ?1 " +
            "GROUP BY e.id " +
            "ORDER BY vote_count ASC")
    List<Event> findEventsByVoteCountWithBooleanAsc(boolean isPositive, Pageable pageable);

    @Query("SELECT e, COUNT(v.event.id) AS vote_count " +
            "FROM Event e " +
            "LEFT JOIN Vote v ON e.id = v.event.id " +
            "GROUP BY e.id " +
            "ORDER BY vote_count ASC")
    List<Event> findEventsByVoteCountAllAsc(Pageable pageable);

    @Query("SELECT e, COUNT(v.event.id) AS vote_count " +
            "FROM Event e " +
            "LEFT JOIN Vote v ON e.id = v.event.id " +
            "GROUP BY e.id " +
            "ORDER BY vote_count DESC")
    List<Event> findEventsByVoteCountAllDesc(Pageable pageable);

    @Query("SELECT e FROM Event e ORDER BY e.views ASC")
    List<Event> findEventsByViewsCountAsc(Pageable pageable);

    @Query("SELECT e FROM Event e ORDER BY e.views DESC")
    List<Event> findEventsByViewsCountDesc(Pageable pageable);
}
package ru.practicum.main.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.event.dto.EventRatingDto;
import ru.practicum.main.rating.model.Rating;

import java.util.Collection;
import java.util.Optional;

public interface RatingsRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndEventId(Long userId, Long eventId);

@Query("select new ru.practicum.main.event.dto.EventRatingDto(ra.eventId, sum(ra.state)) " +
       "from Rating ra " +
       "where :eventIds is null or ra.eventId in :eventIds " +
       "group by ra.eventId")
    Collection<EventRatingDto> getAllEventsRating(Collection<Long> eventIds);
}

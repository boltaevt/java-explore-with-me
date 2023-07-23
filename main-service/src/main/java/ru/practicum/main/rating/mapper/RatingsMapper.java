package ru.practicum.main.rating.mapper;

import ru.practicum.main.rating.dto.RatingDto;
import ru.practicum.main.rating.enums.RatingState;
import ru.practicum.main.rating.model.Rating;

public class RatingsMapper {
    private RatingsMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static RatingDto toRatingDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .userId(rating.getUserId())
                .eventId(rating.getEventId())
                .state(rating.getState() == 1 ? RatingState.LIKE : RatingState.DISLIKE)
                .build();
    }
}
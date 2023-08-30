package ru.practicum.main.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.rating.enums.RatingState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private RatingState state;
}
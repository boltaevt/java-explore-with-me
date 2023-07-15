package ru.practicum.main.user.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {
    private Collection<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
    private Collection<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}
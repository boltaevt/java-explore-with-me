package ru.practicum.main.request.model;

import lombok.*;

@Getter
@Setter
public class RequestInfo {
    private Long eventId;
    private Long confirmedRequestsCount;

    public RequestInfo(Long eventId, Long confirmedRequestsCount) {
        this.eventId = eventId;
        this.confirmedRequestsCount = confirmedRequestsCount;
    }
}
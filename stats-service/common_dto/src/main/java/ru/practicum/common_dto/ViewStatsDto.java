package ru.practicum.common_dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private Integer hits;

    public ViewStatsDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits.intValue();
    }
}
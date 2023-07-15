package ru.practicum.main.event.mapper;

import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.model.Location;

public class LocationMapper {
    private LocationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
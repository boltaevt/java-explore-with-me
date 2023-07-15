package ru.practicum.main.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.mapper.LocationMapper;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.repository.LocationsRepository;

import java.util.Optional;

@Service
public class LocationsService {
    private final LocationsRepository locationsRepository;

    public LocationsService(LocationsRepository locationsRepository) {
        this.locationsRepository = locationsRepository;
    }

    public Location addLocation(LocationDto locationDto) {
        return locationsRepository.save(LocationMapper.toLocation(locationDto));
    }

    public Location getLocation(Long locationId) {
        return locationsRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Местоположение " + locationId + " не найдено."));
    }

    public Optional<Location> getLocationByLatAndLon(LocationDto locationDto) {
        return locationsRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
    }
}
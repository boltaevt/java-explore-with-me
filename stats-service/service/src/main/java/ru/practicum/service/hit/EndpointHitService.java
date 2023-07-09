package ru.practicum.service.hit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.common_dto.EndpointHitDto;

import java.util.Optional;

@Service
@Slf4j
public class EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;
    private final AppRepository appRepository;

    @Autowired
    public EndpointHitService(EndpointHitRepository endpointHitRepository, AppRepository appRepository) {
        this.endpointHitRepository = endpointHitRepository;
        this.appRepository = appRepository;
    }

    public void saveHit(EndpointHitDto endpointHitDto) {
        Optional<App> savedApp = appRepository.findByName(endpointHitDto.getApp());
        App app = savedApp.orElseGet(() -> appRepository.save(App.builder().name(endpointHitDto.getApp()).build()));
        EndpointHit endpointHit = endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto, app));
        log.info("Сохранена информация о запросе {}", endpointHit.getId());
    }
}
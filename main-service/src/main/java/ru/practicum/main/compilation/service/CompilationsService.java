package ru.practicum.main.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.mapper.CompilationsMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationsRepository;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.event.service.EventsService;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final EventsRepository eventsRepository;
    private final EventsService eventsService;

    public CompilationsService(CompilationsRepository compilationsRepository, EventsRepository eventsRepository, EventsService eventsService) {
        this.compilationsRepository = compilationsRepository;
        this.eventsRepository = eventsRepository;
        this.eventsService = eventsService;
    }

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Collection<Event> events = eventsRepository.findAllById(newCompilationDto.getEvents());
        Compilation compilation = compilationsRepository.save(CompilationsMapper.toCompilation(newCompilationDto, events));

        log.info("Создана подборка {}", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation, eventsService.getEventShortDtos(events));
    }

    public void deleteCompilation(Long compId) {
        if (compilationsRepository.findById(compId).isEmpty()) {
            throw new EntityNotFoundException("Подборка " + compId + "не найдена");
        }

        compilationsRepository.deleteById(compId);
        log.info("Удалена подборка {}", compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationsRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка " + compId + " не найдена."));

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventsRepository.findAllById(updateCompilationRequest.getEvents()));
        }

        Compilation updatedCompilation = compilationsRepository.save(compilation);

        log.info("Обновлена подборка {}", compId);

        return CompilationsMapper.toCompilationDto(updatedCompilation,
                eventsService.getEventShortDtos(updatedCompilation.getEvents()));
    }

    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Collection<Compilation> compilations = compilationsRepository.findAllByPinned(pinned, pageable).toList();
        Collection<CompilationDto> compilationDtos = new ArrayList<>();

        for (Compilation compilation : compilations) {
            CompilationDto compilationDto = CompilationsMapper.toCompilationDto(compilation, eventsService.getEventShortDtos(compilation.getEvents()));
            compilationDtos.add(compilationDto);
        }

        log.info("Запрошено {} подборок", compilationDtos.size());

        return compilationDtos;
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationsRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка " + compId + " не найдена."));
        log.info("Запрошена подборка {}", compId);

        return CompilationsMapper.toCompilationDto(compilation,
                eventsService.getEventShortDtos(compilation.getEvents()));
    }
}
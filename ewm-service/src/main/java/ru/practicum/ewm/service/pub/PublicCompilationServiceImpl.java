package ru.practicum.ewm.service.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.CompilationMapper.toCompilationDto;
import static ru.practicum.ewm.mapper.CompilationMapper.toCompilationDtoList;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository repository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = repository.findAllByPinned(pinned);
        } else {
            compilations = repository.findAll();
        }
        return toCompilationDtoList(compilations.stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Override
    public CompilationDto getById(Long compId) {
        return toCompilationDto(getByIdWithCheck(compId));
    }

    public Compilation getByIdWithCheck(Long compId) {
        return repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d was not found", compId)));
    }
}

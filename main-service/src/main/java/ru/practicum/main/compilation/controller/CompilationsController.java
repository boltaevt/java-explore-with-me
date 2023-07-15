package ru.practicum.main.compilation.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.service.CompilationsService;
import ru.practicum.main.compilation.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/compilations")
public class CompilationsController {
    private final CompilationsService compilationsService;

    public CompilationsController(CompilationsService compilationsService) {
        this.compilationsService = compilationsService;
    }

    @GetMapping
    public Collection<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        return compilationsService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return compilationsService.getCompilation(compId);
    }
}
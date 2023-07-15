package ru.practicum.main.admin.compilation;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.service.CompilationsService;

@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final CompilationsService compilationsService;

    public AdminCompilationsController(CompilationsService compilationsService) {
        this.compilationsService = compilationsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Validated @RequestBody NewCompilationDto newCompilationDto) {
        return compilationsService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationsService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Validated @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationsService.updateCompilation(compId, updateCompilationRequest);
    }
}
package ru.practicum.service.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common_dto.ViewStatsDto;

import javax.xml.bind.ValidationException;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/stats")
public class ViewStatsController {
    private final ViewStatsService viewStatsService;

    @Autowired
    public ViewStatsController(ViewStatsService viewStatsService) {
        this.viewStatsService = viewStatsService;
    }

    @GetMapping
    public Collection<ViewStatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) Collection<String> uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) throws ValidationException {
        return viewStatsService.getStats(start, end, uris, unique);
    }
}
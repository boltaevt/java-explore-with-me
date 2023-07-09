package ru.practicum.service.hit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common_dto.EndpointHitDto;

@Validated
@RestController
@RequestMapping("/hit")
public class EndpointHitController {
    private final EndpointHitService endpointHitService;

    @Autowired
    public EndpointHitController(EndpointHitService endpointHitService) {
        this.endpointHitService = endpointHitService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void saveEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        endpointHitService.saveHit(endpointHitDto);
    }
}
package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ParamHitDto;
import ru.practicum.ewm.ParamStatsDto;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid ParamHitDto paramHitDto) {
        statsService.hit(paramHitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> stats(@Valid ParamStatsDto paramStatsDto) {
        return statsService.stats(paramStatsDto);
    }
}

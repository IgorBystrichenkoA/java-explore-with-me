package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ParamHitDto;
import ru.practicum.ParamStatsDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public void hit(@RequestBody @Valid ParamHitDto paramHitDto) {
        statsService.hit(paramHitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> stats(@Valid ParamStatsDto paramStatsDto) {
        return statsService.stats(paramStatsDto);
    }
}

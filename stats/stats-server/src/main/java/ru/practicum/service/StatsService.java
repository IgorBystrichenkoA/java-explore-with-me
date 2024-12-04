package ru.practicum.service;

import jakarta.validation.Valid;
import ru.practicum.ParamHitDto;
import ru.practicum.ParamStatsDto;
import ru.practicum.StatsDto;

import java.util.List;

public interface StatsService {
    void hit(@Valid ParamHitDto endpointHit);

    List<StatsDto> stats(ParamStatsDto paramStatsDto);
}

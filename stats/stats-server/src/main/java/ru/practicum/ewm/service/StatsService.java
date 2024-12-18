package ru.practicum.ewm.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.ParamHitDto;
import ru.practicum.ewm.ParamStatsDto;
import ru.practicum.ewm.StatsDto;

import java.util.List;

public interface StatsService {
    void hit(@Valid ParamHitDto endpointHit);

    List<StatsDto> stats(ParamStatsDto paramStatsDto);
}

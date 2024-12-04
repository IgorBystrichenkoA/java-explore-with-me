package ru.practicum.dal;

import jakarta.validation.Valid;
import ru.practicum.ParamHitDto;
import ru.practicum.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    Long hit(@Valid ParamHitDto endpointHit);

    List<StatsDto> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

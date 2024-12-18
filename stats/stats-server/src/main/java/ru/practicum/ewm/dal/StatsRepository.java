package ru.practicum.ewm.dal;

import jakarta.validation.Valid;
import ru.practicum.ewm.ParamHitDto;
import ru.practicum.ewm.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    Long hit(@Valid ParamHitDto endpointHit);

    List<StatsDto> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

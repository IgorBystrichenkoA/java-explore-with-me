package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void hit(ParamHitDto paramHitDto);

    List<StatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}

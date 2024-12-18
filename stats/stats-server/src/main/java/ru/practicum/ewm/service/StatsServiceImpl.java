package ru.practicum.ewm.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ParamHitDto;
import ru.practicum.ewm.ParamStatsDto;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.dal.StatsRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void hit(@Valid ParamHitDto endpointHit) {
        statsRepository.hit(endpointHit);
        log.info("Hit: endpointHit = {}", endpointHit);
    }

    @Override
    public List<StatsDto> stats(ParamStatsDto paramStatsDto) {
        List<StatsDto> viewStats = statsRepository.stats(paramStatsDto.getStart(), paramStatsDto.getEnd(),
                paramStatsDto.getUris(), paramStatsDto.getUnique());
        log.info("Get stats: viewStats = {}", viewStats);
        return viewStats;
    }
}

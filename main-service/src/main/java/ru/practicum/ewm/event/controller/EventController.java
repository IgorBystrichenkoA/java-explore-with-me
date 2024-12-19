package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.event.PublicEventParam;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.EventViews;
import ru.practicum.ewm.event.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.ParamHitDto;
import ru.practicum.ewm.StatsClient;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final StatsClient statsClient;

    private void hit(HttpServletRequest request) {
        if (request == null) return;
        ParamHitDto endpointHit = new ParamHitDto();
        endpointHit.setApp("main-service");
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());
        statsClient.hit(endpointHit);
    }

    private void hitAndMerge(List<? extends EventViews> events, HttpServletRequest request) {
        if (request == null) return;

        String ip = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        ParamHitDto endpointHit = new ParamHitDto();
        endpointHit.setApp("main-service");
        endpointHit.setIp(ip);
        endpointHit.setTimestamp(now);

        Map<String, EventViews> eventMap = new HashMap<>();
        for (EventViews event : events) {
            String uri = "/events/" + event.getId();
            eventMap.put(uri, event);
            endpointHit.setUri(uri);
            statsClient.hit(endpointHit);
        }

        Set<String> uris = eventMap.keySet();
        List<StatsDto> stats = statsClient.stats(now.minusMonths(1), now.plusMinutes(1), uris.toArray(new String[0]), true);
        if (stats != null && !stats.isEmpty()) {
            for (StatsDto stat : stats) {
                EventViews event = eventMap.get(stat.getUri());
                if (event != null) {
                    event.setViews(stat.getHits());
                }
            }
        }
    }

    @GetMapping
    public List<EventShortDto> getAllEventsByParameters(@Valid PublicEventParam publicEventParam,
                                                        HttpServletRequest request) {
        List<EventShortDto> events = eventService.getAllByPublicParameters(publicEventParam);
        hit(request);
        hitAndMerge(events, request);
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        PublicEventParam publicEventParam = new PublicEventParam();
        publicEventParam.setEventId(id);
        EventFullDto event = eventService.getPublishedById(publicEventParam);
        hitAndMerge(List.of(event), request);
        return event;
    }
}

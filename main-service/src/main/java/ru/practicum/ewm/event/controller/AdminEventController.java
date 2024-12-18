package ru.practicum.ewm.event.controller;

import ru.practicum.ewm.user.dto.UpdateEventRequest;
import ru.practicum.ewm.event.AdminEventParam;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByParameters(@Valid AdminEventParam adminEventParam) {
        return eventService.getAllByParameters(adminEventParam);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        AdminEventParam adminEventParam = new AdminEventParam();
        adminEventParam.setEventId(eventId);
        updateEventRequest.setEventId(eventId);
        adminEventParam.setUpdateEventRequest(updateEventRequest);
        return EventMapper.toEventFullDto(eventService.update(adminEventParam));
    }
}

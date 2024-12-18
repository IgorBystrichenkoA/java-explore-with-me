package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.ParticipationRequestDto;
import ru.practicum.ewm.event.PrivateUserEventParam;
import ru.practicum.ewm.user.dto.UpdateEventRequest;
import ru.practicum.ewm.user.dto.UserEventsParam;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createUserEvent(@PathVariable Long userId,
                                        @RequestBody @Valid NewEventDto newEventDto) {
        newEventDto.setUserId(userId);
        return EventMapper.toEventFullDto(userService.create(newEventDto));
    }

    @GetMapping
    public List<EventShortDto> getEventsCreatedByUser(@PathVariable Long userId,
                                                      @Valid UserEventsParam userEventsParam) {
        userEventsParam.setUserId(userId);
        return EventMapper.toListEventShortDto(userService.getByInitiator(userEventsParam));
    }


    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEventId(eventId);
        return EventMapper.toEventFullDto(userService.getUserEvent(privateUserEventParam));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        updateEventRequest.setUserId(userId);
        updateEventRequest.setEventId(eventId);
        return EventMapper.toEventFullDto(userService.updateUserEvent(updateEventRequest));
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEventId(eventId);
        return RequestMapper.toListParticipationRequestDto(userService.getRequestsOfUserEvent(privateUserEventParam));
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                                      @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEventId(eventId);
        privateUserEventParam.setEventRequestStatusUpdateRequest(eventRequestStatusUpdateRequest);
        return userService.updateParticipationRequests(privateUserEventParam);
    }
}

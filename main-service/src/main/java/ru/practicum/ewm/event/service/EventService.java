package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.AdminEventParam;
import ru.practicum.ewm.event.PublicEventParam;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.event.PrivateUserEventParam;
import ru.practicum.ewm.user.dto.UpdateEventRequest;
import ru.practicum.ewm.user.dto.UserEventsParam;

import java.util.List;

public interface EventService {

    List<EventFullDto> getAllByParameters(AdminEventParam adminEventParam);

    Event update(AdminEventParam adminEventParam);

    List<EventShortDto> getAllByPublicParameters(PublicEventParam publicEventParam);

    EventFullDto getPublishedById(PublicEventParam publicEventParam);

    List<Event> getByInitiator(UserEventsParam userEventsParam);

    Event create(NewEventDto newEventDto);

    Event getUserEvent(PrivateUserEventParam privateUserEventParam);

    Event updateUserEvent(UpdateEventRequest request);

    List<Request> getRequestsOfUserEvent(PrivateUserEventParam privateUserEventParam);

    EventRequestStatusUpdateResult updateParticipationRequests(PrivateUserEventParam privateUserEventParam);
}

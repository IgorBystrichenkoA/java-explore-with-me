package ru.practicum.ewm.event;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        if (!event.getConfirmedRequests().isEmpty()) {
            eventFullDto.setConfirmedRequests((long) event.getConfirmedRequests().size());
        } else {
            eventFullDto.setConfirmedRequests(0L);
        }
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(LocationMapper.toLocationDtoFromLocation(event.getLocation()));
        if (event.getPaid() != null) {
            eventFullDto.setPaid(event.getPaid());
        }
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        if (event.getRequestModeration() != null) {
            eventFullDto.setRequestModeration(event.getRequestModeration());
        }
        eventFullDto.setState(EventState.to(event.getState()));
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }

    public List<EventFullDto> toListEventFullDto(List<Event> eventList) {
        return eventList.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests((long) event.getConfirmedRequests().size());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());

        return eventShortDto;
    }

    public List<EventShortDto> toListEventShortDto(List<Event> eventList) {
        return eventList.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public Event fromNewEventDto(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid() != null && newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit() == null ? 0L : newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(initiator)
                .category(category)
                .confirmedRequests(new ArrayList<>())
                .createdOn(LocalDateTime.now())
                .publishedOn(LocalDateTime.now())
                .views(0L)
                .state(EventState.PENDING)
                .build();
    }

}

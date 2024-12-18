package ru.practicum.ewm.event.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.data.OffsetBasedPageRequest;
import ru.practicum.ewm.event.AdminEventParam;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.PublicEventParam;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.ConditionsNotRespected;
import ru.practicum.ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.event.PrivateUserEventParam;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.UpdateEventRequest;
import ru.practicum.ewm.user.dto.UserEventsParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final StatsClient statsClient;

    private static final String NOT_EXISTING_EVENT = "Событие не найдено или недоступно";

    private static final String NOT_EXISTING_USER = "Пользователь не найден";

    private static final String NOT_EXISTING_CATEGORY = "Категория по id %s не существует";

    private static final String NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT = "Событие не удовлетворяет правилам редактирования";

    private static final String NOT_RESPECTED_CONDITIONS_TO_MODIFY_USER_EVENT = "Изменить можно только события в состоянии ожидания модерации";

    private static final String NOT_RESPECTED_TIME_RULES_TO_MODIFY_USER_EVENT = "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента";

    private static final String REACHED_LIMIT_OF_REQUESTS = "Достигнут лимит по заявкам";


    @Override
    public List<EventFullDto> getAllByParameters(AdminEventParam adminEventParam) {
        log.info("ADMIN: Запрос на получение всех событий по параметрам");

        Predicate predicate = adminEventParam.toPredicate();
        Pageable pageable = new OffsetBasedPageRequest(adminEventParam.getFrom(), adminEventParam.getSize());
        Page<Event> eventList = predicate != null ?
                eventRepository.findAll(predicate, pageable) :
                eventRepository.findAll(pageable);

        List<EventFullDto> result = EventMapper.toListEventFullDto(eventList.getContent());
        mergeViews(result);
        mergeConfirmedRequests(result);
        log.info("ADMIN: Получен список событий размером {}", eventList.getSize());
        return result;
    }

    private void updateEventFields(Event oldEvent, UpdateEventRequest newEvent) {
        if (newEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null) {
            Category category = categoryRepository.findById(newEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id = " + newEvent.getCategory() + " не найдена"));
            oldEvent.setCategory(category);
        }
        if (newEvent.getDescription() != null) {
            oldEvent.setDescription(newEvent.getDescription());
        }
        if (newEvent.getEventDate() != null) {
            oldEvent.setEventDate(newEvent.getEventDate());
        }
        if (newEvent.getLocation() != null) {
            Optional<Location> location = locationRepository
                    .findByLatAndLon(newEvent.getLocation().getLat(), newEvent.getLocation().getLon());
            if (location.isPresent()) {
                newEvent.setLocation(location.get());
            } else {
                newEvent.setLocation(locationRepository.save(newEvent.getLocation()));
            }
        }
        if (newEvent.getPaid() != null) {
            oldEvent.setPaid(newEvent.getPaid());
        }
        if (newEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(newEvent.getRequestModeration());
        }
        if (newEvent.getTitle() != null) {
            oldEvent.setTitle(newEvent.getTitle());
        }
    }

    @Override
    @Transactional
    public Event update(AdminEventParam adminEventParam) {
        log.info("ADMIN: Получен запрос на изменение события и его статуса по id {}", adminEventParam.getEventId());

        Event eventFromDb = eventRepository.findById(adminEventParam.getEventId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_EVENT));
        UpdateEventRequest newEvent = adminEventParam.getUpdateEventRequest();

        switch (eventFromDb.getState()) {
            case EventState.CANCELED: case EventState.PENDING:
                break;
            default:
                throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
        }

        updateEventFields(eventFromDb, newEvent);

        if (newEvent.getStateAction() != null) {
            EventState state = EventState.valueOf(newEvent.getStateAction());
            switch (state) {
                case PUBLISH_EVENT: {
                    if (eventFromDb.getState() != EventState.PENDING) {
                        throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
                    }
                    LocalDateTime publishedDate = LocalDateTime.now();
                    if (isEarlierThan(eventFromDb.getEventDate(), publishedDate.plusHours(1))) {
                        throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
                    }
                    eventFromDb.setPublishedOn(publishedDate);
                    eventFromDb.setState(EventState.PUBLISHED);
                }
                break;
                case REJECT_EVENT: {
                    if (eventFromDb.getState() == EventState.PUBLISHED) {
                        throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
                    }
                    eventFromDb.setPublishedOn(null);
                    eventFromDb.setState(EventState.CANCELED);
                }
                break;
            }
        }
        Event eventSaved = eventRepository.save(eventFromDb);

        log.info("ADMIN: Событие {} изменено", eventSaved);
        return eventSaved;
    }

    private void mergeViews(List<? extends EventViews> events) {
        LocalDateTime now = LocalDateTime.now();

        Map<String, EventViews> eventMap = new HashMap<>();
        for (EventViews event : events) {
            String uri = "/events/" + event.getId();
            eventMap.put(uri, event);
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

    private void mergeConfirmedRequests(List<? extends EventConfirmedRequests> events) {
        List<Long> eventIds = events.stream().map(EventConfirmedRequests::getId).toList();
        List<CountByEvent> counts = requestRepository.countByStatusRequests(eventIds, RequestStatus.CONFIRMED);
        Map<Long, Long> countByEvents = counts.stream()
                .collect(Collectors.toMap(CountByEvent::getEventId, CountByEvent::getCount));
        for (EventConfirmedRequests event : events) {
            event.setConfirmedRequests(countByEvents.getOrDefault(event.getId(), 0L));
        }
    }

    @Override
    public List<EventShortDto> getAllByPublicParameters(PublicEventParam publicEventParam) {
        int from = publicEventParam.getFrom();
        int size = publicEventParam.getSize();
        log.info("PUBLIC: Получен запрос на получение события");

        Predicate predicate = publicEventParam.toPredicate();
        Pageable pageable = new OffsetBasedPageRequest(from, size);

        if (publicEventParam.getSort() == EventSort.VIEWS) {
            List<Event> eventsFromDb = StreamSupport
                    .stream(eventRepository.findAll(predicate).spliterator(), false)
                    .toList();
            List<EventShortDto> result = EventMapper.toListEventShortDto(eventsFromDb);
            mergeViews(result);
            mergeConfirmedRequests(result);
            return result.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .skip(from)
                    .limit(size)
                    .toList();
        }
        if (publicEventParam.getSort() == EventSort.EVENT_DATE) {
            pageable = new OffsetBasedPageRequest(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        }
        List<Event> eventsFromDb = eventRepository.findAll(predicate, pageable).getContent();
        List<EventShortDto> result = EventMapper.toListEventShortDto(eventsFromDb);
        mergeViews(result);
        mergeConfirmedRequests(result);

        log.info("PUBLIC: Получен список всех событий размером {}", result.size());
        return result;
    }

    @Override
    public EventFullDto getPublishedById(PublicEventParam publicEventParam) {
        log.info("PUBLIC: Получен запрос на получения события по id {}", publicEventParam.getEventId());

        Event eventFromDb = eventRepository.getEventById(publicEventParam.getEventId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_EVENT));
        if (eventFromDb.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format(NOT_EXISTING_EVENT));
        }
        EventFullDto result = EventMapper.toEventFullDto(eventFromDb);
        mergeViews(List.of(result));
        mergeConfirmedRequests(List.of(result));

        log.info("PUBLIC: Получено событие {} по id {}", eventFromDb, eventFromDb.getId());
        return result;
    }

    @Override
    public List<Event> getByInitiator(UserEventsParam param) {
        Long userId = param.getUserId();
        log.info("PRIVATE: Запрос получения событий пользователя с id {}", userId);

        Pageable pageable = new OffsetBasedPageRequest(param.getFrom(), param.getSize());
        getUserById(userId);
        List<Event> eventList = eventRepository.getAllByInitiatorId(userId, pageable).getContent();
        log.info("PRIVATE: Получен список событий размером {}, созданных пользователем с id {}", eventList.size(), userId);

        return eventList;
    }

    @Override
    @Transactional
    public Event create(NewEventDto newEventDto) {
        log.info("PRIVATE: Запрос на создание события {} от пользователя с id {}", newEventDto, newEventDto.getUserId());

        User user = getUserById(newEventDto.getUserId());
        Category category = getCategoryById(newEventDto.getCategory());
        Event event = EventMapper.fromNewEventDto(newEventDto, user, category);
        locationRepository.save(event.getLocation());
        Event eventFromDb = eventRepository.save(event);

        log.info("PRIVATE: Создано событие {}, пользователем с id {}", eventFromDb, user.getId());
        return eventFromDb;
    }

    @Override
    public Event getUserEvent(PrivateUserEventParam privateUserEventParam) {
        Long userId = privateUserEventParam.getUserId();
        Long eventId = privateUserEventParam.getEventId();
        log.info("PRIVATE: Получен запрос на получение события с id {} от пользователя с id {}", userId, eventId);

        getUserById(userId);
        Event event = getEventById(eventId);

        log.info("PRIVATE: Получено событие {} от пользователя с id {}", userId, eventId);
        return event;
    }

    private static boolean isEarlierThan(LocalDateTime dateTime, LocalDateTime other) {
        if (dateTime == null || other == null) {
            return false;
        }
        return dateTime.isBefore(other);
    }

    @Override
    @Transactional
    public Event updateUserEvent(UpdateEventRequest request) {
        log.info("PRIVATE: Запрос на изменение события с id {} от пользователя с id {}",
                request.getEventId(), request.getUserId());

        Event eventFromDb = getEventById(request.getEventId());
        switch (eventFromDb.getState()) {
            case PENDING: case CANCELED:
                break;
            default:
                throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_MODIFY_USER_EVENT);
        }
        LocalDateTime twoHoursLater = LocalDateTime.now().plusHours(2);
        if (isEarlierThan(eventFromDb.getEventDate(), twoHoursLater) ||
                isEarlierThan(request.getEventDate(), twoHoursLater)) {
            throw new ConditionsNotRespected(NOT_RESPECTED_TIME_RULES_TO_MODIFY_USER_EVENT);
        }
        updateEventFields(eventFromDb, request);

        if (request.getStateAction() != null) {
            EventState state = EventState.valueOf(request.getStateAction());
            switch (state) {
                case PUBLISH_EVENT: {
                    if (eventFromDb.getState() != EventState.PENDING) {
                        throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
                    }
                    LocalDateTime publishedDate = LocalDateTime.now();
                    if (isEarlierThan(eventFromDb.getEventDate(), publishedDate.plusHours(1))) {
                        throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
                    }
                    eventFromDb.setPublishedOn(publishedDate);
                    eventFromDb.setState(EventState.PUBLISHED);
                }
                break;
                case REJECT_EVENT: case CANCEL_REVIEW: {
                    if (eventFromDb.getState() == EventState.PUBLISHED) {
                        throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
                    }
                    eventFromDb.setPublishedOn(null);
                    eventFromDb.setState(EventState.CANCELED);
                }
                break;
                case SEND_TO_REVIEW: {
                    eventFromDb.setState(EventState.PENDING);
                }
                break;
            }
        }

        Event updatedEvent = eventRepository.save(eventFromDb);

        log.info("PRIVATE: Обновлено событие по id {} от пользователя с id {}",
                request.getEventId(), request.getUserId());
        return updatedEvent;
    }

    @Override
    public List<Request> getRequestsOfUserEvent(PrivateUserEventParam privateUserEventParam) {
        Long userId = privateUserEventParam.getUserId();
        Long eventId = privateUserEventParam.getEventId();
        log.info("PRIVATE: Запрос получение всех запросов с id {} от пользователя с id {}", eventId, userId);

        getUserById(userId);
        getEventById(eventId);
        List<Request> requestList = requestRepository.getRequestsByEventId(eventId);

        log.info("PRIVATE: Получен список запросов на участие пользователя с id {} в событии с id {}", userId, eventId);
        return requestList;
    }

    private void checkPending(List<Request> requestsFromDb) {
        if (requestsFromDb.stream().anyMatch(request -> request.getStatus() != RequestStatus.PENDING)) {
            throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_MODIFY_USER_EVENT);
        }
    }

    private void checkLimit(Event event) {
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == event.getConfirmedRequests().size()) {
            throw new ConditionsNotRespected(REACHED_LIMIT_OF_REQUESTS);
        }
    }

    private static int getConfirmCount(List<Request> requestsFromDb, Event eventFromDb) {
        int confirmCount = requestsFromDb.size();
        if (eventFromDb.getRequestModeration() && eventFromDb.getParticipantLimit() > 0) {
            confirmCount = (int) (eventFromDb.getParticipantLimit() - eventFromDb.getConfirmedRequests().size());
            if (confirmCount <= 0) {
                throw new ConditionsNotRespected(REACHED_LIMIT_OF_REQUESTS);
            }
            if (confirmCount > requestsFromDb.size()) {
                confirmCount = requestsFromDb.size();
            }
        }
        return confirmCount;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequests(PrivateUserEventParam param) {
        log.info("PRIVATE: Запрос на обновление статуса запросов события с id {}", param.getEventId());

        Event eventFromDb = getEventById(param.getEventId());
        List<Request> requestsFromDb = requestRepository
                .findByIdIn(param.getEventRequestStatusUpdateRequest().getRequestIds());

        checkLimit(eventFromDb);
        checkPending(requestsFromDb);

        EventRequestStatusUpdateRequest updateRequest = param.getEventRequestStatusUpdateRequest();
        RequestStatus newStatus = updateRequest.getStatus();

        if (newStatus == RequestStatus.REJECTED) {
            requestsFromDb.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        }

        if (newStatus == RequestStatus.CONFIRMED) {
            int confirmCount = getConfirmCount(requestsFromDb, eventFromDb);
            for (int i = 0; i < confirmCount; ++i) {
                requestsFromDb.get(i).setStatus(RequestStatus.CONFIRMED);
                eventFromDb.getConfirmedRequests().add(requestsFromDb.get(i).getId());
            }
            for (int i = confirmCount; i < requestsFromDb.size(); ++i) {
                requestsFromDb.get(i).setStatus(RequestStatus.REJECTED);
            }
        }

        List<Request> requestsSaved = requestRepository.saveAll(requestsFromDb);
        eventRepository.save(eventFromDb);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        for (Request request : requestsSaved) {
            switch (request.getStatus()) {
                case CONFIRMED:
                    result.addConfirmedRequest(RequestMapper.toParticipationRequestDto(request));
                    break;
                case REJECTED:
                    result.addRejectedRequest(RequestMapper.toParticipationRequestDto(request));
                    break;
            }
        }

        log.info("PRIVATE: Запрос на обновление статуса запросов события с id {} успешен",param.getEventId());
        return result;
    }

    private User getUserById(Long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_USER));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.getEventById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_EVENT));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_EXISTING_CATEGORY, categoryId)));
    }

}

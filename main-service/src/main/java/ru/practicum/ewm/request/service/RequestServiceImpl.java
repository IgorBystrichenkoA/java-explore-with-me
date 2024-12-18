package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.CountByEvent;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConditionsNotRespected;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.PrivateRequestParam;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private static final String NOT_EXISTING_USER = "Пользователь c id %s не найден";

    private static final String NOT_EXISTING_EVENT = "Событие с id %s не найдено или недоступно";

    private static final String NOT_EXISTING_REQUEST = "Запрос с id %s не найден или не существует";

    private static final String ALREADY_EXISTING_REQUEST_FOR_EVENT = "Заявка на участие в мероприятие по id %s уже существует !";

    private static final String NOT_PERMITTED_TO_APPLY_TO_PARTICIPATE_AT_OWN_EVENT = "Инициатор с id %s события с id %s не может добавить запрос на участие в своём событии";

    private static final String NOT_PERMITTED_TO_APPLY_TO_NOT_PUBLISHED_EVENT = "Нельзя участвовать в неопубликованном событии с id %s";

    private static final String EVENT_REACHED_MAX_PARTICIPANTS = "У события с id %s достигнут лимит запросов на участие";

    @Override
    @Transactional
    public Request createUserRequest(PrivateRequestParam privateRequestParam) {
        log.info("PRIVATE: Запрос на участие пользователя по id {} в событие по id {}",
                privateRequestParam.getUserId(), privateRequestParam.getRequestId());

        User userFromDb = userRepository.getUserById(privateRequestParam.getUserId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_USER));
        Event eventFromDb = eventRepository.getEventById(privateRequestParam.getEventId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_EVENT));

        if (eventFromDb.getInitiator().getId().equals(userFromDb.getId())) {
            throw new ConditionsNotRespected(String.format(NOT_PERMITTED_TO_APPLY_TO_PARTICIPATE_AT_OWN_EVENT,
                    userFromDb.getId(), eventFromDb.getId()));
        }
        if (!eventFromDb.getState().equals(EventState.PUBLISHED)
                && !eventFromDb.getState().equals(EventState.PUBLISH_EVENT)) {
            throw new ConditionsNotRespected(String.format(NOT_PERMITTED_TO_APPLY_TO_NOT_PUBLISHED_EVENT,
                    eventFromDb.getId()));
        }
        if (eventFromDb.getParticipantLimit() != 0
                && eventFromDb.getParticipantLimit().equals((long) eventFromDb.getConfirmedRequests().size())) {
            throw new ConditionsNotRespected(String.format(EVENT_REACHED_MAX_PARTICIPANTS, eventFromDb.getId()));
        }
        Optional<Request> requestAlreadyExist =
                requestRepository.findRequestByRequesterIdAndEventId(userFromDb.getId(), eventFromDb.getId());
        if (requestAlreadyExist.isPresent()) {
            throw new ConditionsNotRespected(String.format(ALREADY_EXISTING_REQUEST_FOR_EVENT, eventFromDb.getId()));
        }
        if (eventFromDb.getParticipantLimit() > 0) {
            List<CountByEvent> confirmed = requestRepository
                    .countByStatusRequests(List.of(eventFromDb.getId()), RequestStatus.CONFIRMED);
            if (!confirmed.isEmpty()) {
                if (confirmed.getFirst().getCount() >= eventFromDb.getParticipantLimit()) {
                    throw new ConditionsNotRespected("Достигнут лимит запросов на участие в событии");
                }
            }
        }
        Request requestToSave = new Request();
        requestToSave.setCreated(LocalDateTime.now());
        requestToSave.setEvent(eventFromDb);
        requestToSave.setRequester(userFromDb);
        requestToSave.setStatus(eventFromDb.getRequestModeration() && eventFromDb.getParticipantLimit() > 0
                ? RequestStatus.PENDING : RequestStatus.CONFIRMED);
        Request requestSaved = requestRepository.save(requestToSave);

        log.info("PRIVATE: Создан запрос на участие пользователя с id {} в событии с id {}",
                userFromDb.getId(), eventFromDb.getId());
        return requestSaved;
    }

    @Override
    public List<Request> getUserRequests(Long userId) {
        log.info("PRIVATE: Запрос на получение информации о запросе на участие пользователя по id {}", userId);

        checkUserById(userId);
        List<Request> requestList = requestRepository.getRequestsByRequesterId(userId);

        log.info("PRIVATE: Получен список запросов на участие пользователя по id {}", userId);
        return requestList;

    }

    @Override
    @Transactional
    public Request cancelUserRequest(PrivateRequestParam param) {
        log.info("PRIVATE: Получен запрос на отмену запроса на участие в событие");

        checkUserById(param.getUserId());
        Long requestId = param.getRequestId();
        Request requestToModify = requestRepository.getRequestById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_EXISTING_REQUEST, requestId)));
        requestToModify.setStatus(RequestStatus.CANCELED);
        Request requestSaved = requestRepository.save(requestToModify);

        log.info("PRIVATE: Получен отмененный запрос на участие пользователя с id {}", param.getUserId());
        return requestSaved;
    }

    private void checkUserById(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_EXISTING_USER, userId)));
    }
}

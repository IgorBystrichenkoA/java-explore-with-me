package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.PrivateRequestParam;

import java.util.List;

public interface RequestService {
    List<Request> getUserRequests(Long userId);

    Request createUserRequest(PrivateRequestParam privateUserParam);

    Request cancelUserRequest(PrivateRequestParam privateUserParam);

}

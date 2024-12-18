package ru.practicum.ewm.request;

import ru.practicum.ewm.exception.ConditionsNotRespected;

public enum RequestStatus {

    PENDING,

    REJECTED,

    CONFIRMED,

    CANCELED;

    public static RequestStatus from(String state) {
        for (RequestStatus status : RequestStatus.values()) {
            if (status.name().equals(state)) {
                return status;
            }
        }
        throw new ConditionsNotRespected(String.format("Ошибка состояние REQUEST: Статус запроса на участие %s не существует !", state));
    }



}

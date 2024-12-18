package ru.practicum.ewm.event;

import lombok.Data;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.model.Event;

@Data
public class PrivateUserEventParam {

    private Long userId;

    private Long eventId;

    private int from;

    private int size;

    private Event event;

    private Long category;

    private EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest;

}

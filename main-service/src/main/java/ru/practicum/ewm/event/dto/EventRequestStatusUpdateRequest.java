package ru.practicum.ewm.event.dto;

import lombok.Getter;
import ru.practicum.ewm.request.RequestStatus;
import java.util.List;

@Getter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}

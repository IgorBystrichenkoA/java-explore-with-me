package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.request.Request;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateShortResult {
    private List<Request> confirmedRequests;
    private List<Request> rejectedRequests;
}

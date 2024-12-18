package ru.practicum.ewm.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivateRequestParam {
    private Long userId;
    private Long eventId;
    private Long requestId;
}

package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CountByEvent {
    private Long eventId;
    private Long count;
}

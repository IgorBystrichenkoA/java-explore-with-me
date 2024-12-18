package ru.practicum.ewm.user.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEventsParam {
    Long userId;

    @JsonSetter(nulls = Nulls.SKIP)
    Integer from = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    Integer size = 10;
}
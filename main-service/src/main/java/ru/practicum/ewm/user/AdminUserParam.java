package ru.practicum.ewm.user;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.util.List;

@Data
public class AdminUserParam {
    private List<Long> ids;
    @JsonSetter(nulls = Nulls.SKIP)
    private int from = 0;
    @JsonSetter(nulls = Nulls.SKIP)
    private int size = 10;

    private Long userId;
}

package ru.practicum.ewm.comment;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicCommentParam {
    private Long eventId;
    @JsonSetter(nulls = Nulls.SKIP)
    private int from = 0;
    @JsonSetter(nulls = Nulls.SKIP)
    private int size = 10;
}

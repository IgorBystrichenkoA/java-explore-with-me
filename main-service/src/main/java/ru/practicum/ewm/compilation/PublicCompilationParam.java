package ru.practicum.ewm.compilation;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicCompilationParam {
    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean pinned = false;
    @JsonSetter(nulls = Nulls.SKIP)
    private int from = 0;
    @JsonSetter(nulls = Nulls.SKIP)
    private int size = 10;
}

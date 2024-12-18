package ru.practicum.ewm.category;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

@Data
public class PageCategoryParam {
    @JsonSetter(nulls = Nulls.SKIP)
    private int from = 0;
    @JsonSetter(nulls = Nulls.SKIP)
    private int size = 10;
}

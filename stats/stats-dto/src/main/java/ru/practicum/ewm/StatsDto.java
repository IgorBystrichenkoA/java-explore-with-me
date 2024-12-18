package ru.practicum.ewm;

import lombok.Data;

@Data
public class StatsDto {
    private String app;
    private String uri;
    private long hits;
}

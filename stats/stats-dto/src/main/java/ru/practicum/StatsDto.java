package ru.practicum;

import lombok.Data;

@Data
public class StatsDto {
    private String app;
    private String uri;
    private long hits;
}

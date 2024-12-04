package ru.practicum;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ParamStatsDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime start;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime end;
    List<String> uris;
    @JsonSetter(nulls = Nulls.SKIP)
    Boolean unique = false;

    @AssertFalse(message = "Incorrect period")
    public boolean isValidReleaseDate() {
        return end.isBefore(start);
    }
}

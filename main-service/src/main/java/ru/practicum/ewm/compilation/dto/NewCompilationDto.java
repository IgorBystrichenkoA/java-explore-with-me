package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewCompilationDto {
    private List<Long> events;
    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

}

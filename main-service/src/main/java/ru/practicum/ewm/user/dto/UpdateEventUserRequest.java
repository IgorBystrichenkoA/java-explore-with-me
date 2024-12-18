package ru.practicum.ewm.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.model.Location;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventUserRequest {
    private Long userId;

    private Long eventId;

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @Positive
    private Long participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;

    @AssertTrue
    private boolean isValidDate() {
        if (eventDate == null) {
            return true;
        }
        return !eventDate.isBefore(LocalDateTime.now());
    }

}

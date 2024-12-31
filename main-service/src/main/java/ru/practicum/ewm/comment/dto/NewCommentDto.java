package ru.practicum.ewm.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCommentDto {
    private Long eventId;
    private Long authorId;
    @NotBlank
    private String text;
}

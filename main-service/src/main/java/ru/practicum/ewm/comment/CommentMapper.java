package ru.practicum.ewm.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.comment.dto.CommentDto;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .build();
    }

}

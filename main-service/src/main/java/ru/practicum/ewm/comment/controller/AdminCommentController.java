package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        commentDto.setId(commentId);
        return CommentMapper.toCommentDto(commentService.updateFromAdmin(commentDto));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        commentService.deleteFromAdmin(commentId);
    }
}

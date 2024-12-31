package ru.practicum.ewm.comment.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.PublicCommentParam;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/events/{eventId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> findByEventId(@PathVariable Long eventId, PublicCommentParam publicCommentParam) {
        publicCommentParam.setEventId(eventId);
        return commentService.findByEventId(publicCommentParam).stream().map(CommentMapper::toCommentDto).toList();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto create(@PathVariable Long eventId, @RequestParam Long userId,
                             @RequestBody @Valid NewCommentDto newComment) {
        newComment.setEventId(eventId);
        newComment.setAuthorId(userId);
        return CommentMapper.toCommentDto(commentService.add(newComment));
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable Long commentId, @RequestParam Long userId,
                             @RequestBody CommentDto commentDto) {
        commentDto.setId(commentId);
        commentDto.setAuthorId(userId);
        return CommentMapper.toCommentDto(commentService.update(commentDto));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long eventId, @PathVariable Long commentId, @RequestParam Long userId) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setEventId(eventId);
        commentDto.setAuthorId(userId);
        commentService.delete(commentDto);
    }
}
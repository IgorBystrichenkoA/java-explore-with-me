package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.PublicCommentParam;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    List<Comment> findByEventId(PublicCommentParam publicCommentParam);

    Comment add(NewCommentDto newComment);

    Comment update(CommentDto commentDto);

    void delete(CommentDto commentDto);

    Comment updateFromAdmin(CommentDto commentDto);

    void deleteFromAdmin(Long commentId);
}

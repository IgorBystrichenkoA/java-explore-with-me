package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.comment.PublicCommentParam;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.data.OffsetBasedPageRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConditionsNotRespected;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private static final String NOT_EXISTING_COMMENT = "Комментарий не найден";

    private static final String NOT_EXISTING_USER = "Пользователь не найден";

    private static final String NOT_EXISTING_EVENT = "Событие не найдено";

    private static final String UPDATE_NOT_ALLOWED = "Недостаточно прав для редактирования комментария";

    private static final String DELETE_NOT_ALLOWED = "Недостаточно прав для удаления комментария";

    @Override
    public List<Comment> findByEventId(PublicCommentParam publicCommentParam) {
        log.info("PUBLIC: Запрос на получения комментариев события по id: {}", publicCommentParam.getEventId());

        Pageable pageRequest = new OffsetBasedPageRequest(publicCommentParam.getFrom(), publicCommentParam.getSize());
        List<Comment> comments = commentRepository.getCommentsByEventId(publicCommentParam.getEventId(), pageRequest);

        log.info("PUBLIC: Получен список комментариев события по id: {} получено: {}",
                publicCommentParam.getEventId(), comments.size());
        return List.of();
    }

    @Override
    public Comment add(NewCommentDto newComment) {
        log.info("PUBLIC: Запрос на добавление комментария");

        User author = userRepository.findById(newComment.getAuthorId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_USER));
        Event event = eventRepository.findById(newComment.getEventId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_EVENT));
        Comment comment = new Comment(author, newComment.getText(), event);
        Comment commentSaved = commentRepository.save(comment);

        log.info("PUBLIC: Запрос на добавление комментария успешно выполнен");
        return commentSaved;
    }

    @Override
    public Comment update(CommentDto commentDto) {
        log.info("PUBLIC: Запрос на обновление комментария");

        Comment commentFromDb = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_COMMENT));
        Long commentAuthorId = commentFromDb.getAuthor().getId();
        Long userId = commentDto.getAuthorId();
        if (!Objects.equals(commentAuthorId, userId)) {
            throw new ConditionsNotRespected(UPDATE_NOT_ALLOWED);
        }
        if (commentDto.getText() != null) {
            commentFromDb.setText(commentDto.getText());
        }
        Comment commentSaved = commentRepository.save(commentFromDb);

        log.info("PUBLIC: Запрос на обновление комментария успешно выполнен");
        return commentSaved;
    }

    @Override
    public void delete(CommentDto commentDto) {
        log.info("PUBLIC: Запрос на удаление комментария");

        Comment commentFromDb = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_COMMENT));
        Event eventFromDb = eventRepository.getEventById(commentDto.getEventId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_EVENT));

        Long commentAuthorId = commentFromDb.getAuthor().getId();
        Long eventInitiatorId = eventFromDb.getInitiator().getId();
        Long userId = commentDto.getAuthorId();
        if (!Objects.equals(userId, commentAuthorId) && !Objects.equals(userId, eventInitiatorId)) {
            throw new ConditionsNotRespected(DELETE_NOT_ALLOWED);
        }
        commentRepository.delete(commentFromDb);

        log.info("PUBLIC: Запрос на удаление комментария успешно выполнен");
    }

    @Override
    public Comment updateFromAdmin(CommentDto commentDto) {
        log.info("ADMIN: Запрос на обновление комментария");

        Comment commentFromDb = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_COMMENT));
        if (commentDto.getText() != null) {
            commentFromDb.setText(commentDto.getText());
        }
        Comment commentSaved = commentRepository.save(commentFromDb);

        log.info("ADMIN: Запрос на обновление комментария успешно выполнен");
        return commentSaved;
    }

    @Override
    public void deleteFromAdmin(Long commentId) {
        log.info("ADMIN: Запрос на удаление комментария");

        Comment commentFromDb = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_COMMENT));
        commentRepository.delete(commentFromDb);

        log.info("ADMIN: Запрос на удаление комментария успешно выполнен");
    }
}

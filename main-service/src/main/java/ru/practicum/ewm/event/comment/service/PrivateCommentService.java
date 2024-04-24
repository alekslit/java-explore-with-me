package ru.practicum.ewm.event.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.comment.Comment;
import ru.practicum.ewm.event.comment.CommentDto;
import ru.practicum.ewm.event.comment.CommentMapper;
import ru.practicum.ewm.event.comment.CommentRepository;
import ru.practicum.ewm.event.comment.complaint.*;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.conflict.AlreadyExistException;
import ru.practicum.ewm.exception.conflict.ConflictOperationException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.ewm.exception.NotFoundException.*;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_COMPLAINT_ADVICE;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_COMPLAINT_MESSAGE;
import static ru.practicum.ewm.exception.conflict.ConflictOperationException.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentService {
    private static final long HOURS_FOR_UPDATE_COMMENT = 24;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;

    /*--------------------Основные методы--------------------*/
    public Comment saveComment(CommentDto commentDto, Long userId, Long eventId) {
        log.debug("Попытка сохранить новый объект Comment.");
        // Проверим что существует пользователь:
        User user = getUserById(userId);
        // Проверим событие:
        Event event = getEventById(eventId);
        // Проверим что событие опубликовано:
        checkEventStatus(event);
        // Сохраняем комментарий:
        Comment comment = CommentMapper.mapToComment(commentDto, user, event);
        comment = commentRepository.save(comment);
        // Обновим информацию о количестве уникальных комментариев у события:
        addCommentsCountToEvent(event);
        eventRepository.save(event);

        return comment;
    }

    public Comment updateComment(CommentDto commentDto, Long userId, Long eventId, Long commentId) {
        log.debug("Попытка изменить информацию об объекте Comment.");
        // Проверим что существует пользователь:
        getUserById(userId);
        // Проверим событие:
        getEventById(eventId);
        // Проверим комментарий:
        Comment comment = getCommentById(commentId);
        // Обновляем объект:
        updateCommentObject(comment, commentDto, userId);

        return commentRepository.save(comment);
    }

    public String deleteComment(Long userId, Long eventId, Long commentId) {
        log.debug("Попытка удалить объект Comment по его commentId.");
        // Проверим что существует пользователь:
        getUserById(userId);
        // Проверим событие:
        getEventById(eventId);
        // Проверим комментарий:
        Comment comment = getCommentById(commentId);
        // Проверим, что комментарий удаляет владелец:
        checkForUserIsAuthorComment(comment, userId);
        // удаляем комментарий:
        commentRepository.deleteById(commentId);

        return String.format("Комментарий с commentId = %d, удалён.", commentId);
    }

    public Complaint saveComplaint(ComplaintDto complaintDto, Long userId, Long commentId) {
        log.debug("Попытка сохранить новый объект Complaint.");
        // Проверим что существует пользователь:
        User user = getUserById(userId);
        // Проверим комментарий:
        Comment comment = getCommentById(commentId);
        // Проверим причину жалобы:
        checkReasonComplaint(complaintDto);
        // Автор не может жаловаться на свой комментарий:
        checkForUserIsNotAuthorComment(comment, userId);
        Complaint complaint = ComplaintMapper.mapToComplaint(complaintDto, user, comment);
        // Сохраняем жалобу:
        try {
            return complaintRepository.save(complaint);
        } catch (DataIntegrityViolationException e) {
            log.debug("{}: {} userId = {}, commentId = {}.", AlreadyExistException.class.getSimpleName(),
                    DUPLICATE_COMPLAINT_MESSAGE, userId, commentId);
            throw new AlreadyExistException(DUPLICATE_COMPLAINT_MESSAGE +
                    String.format(" userId = %d, eventId = %d.", userId, commentId),
                    DUPLICATE_COMPLAINT_ADVICE);
        }
    }

    /*---------------Вспомогательные методы---------------*/
    private void updateCommentObject(Comment comment, CommentDto commentDto, Long userId) {
        // Проверим что ещё не прошло 24 часа:
        checkCommentCreationDate(comment);
        // Проверим, что комментарий меняет владелец:
        checkForUserIsAuthorComment(comment, userId);
        // Обновляем комментарий:
        comment.setText(commentDto.getText());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), USER_NOT_FOUND_MESSAGE, userId);
            return new NotFoundException(USER_NOT_FOUND_MESSAGE + userId, USER_NOT_FOUND_ADVICE);
        });
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, eventId);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId, EVENT_NOT_FOUND_ADVICE);
        });
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), COMMENT_NOT_FOUND_MESSAGE, commentId);
            return new NotFoundException(COMMENT_NOT_FOUND_MESSAGE + commentId, COMMENT_NOT_FOUND_ADVICE);
        });
    }

    private void checkCommentCreationDate(Comment comment) {
        if (LocalDateTime.now().isAfter(comment.getCreationDate().plusHours(HOURS_FOR_UPDATE_COMMENT))) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    UPDATE_COMMENT_CONFLICT_MESSAGE, comment.getId());
            throw new ConflictOperationException(UPDATE_COMMENT_CONFLICT_MESSAGE + comment.getId(),
                    COMMENT_DATE_TIME_CONFLICT_ADVICE);
        }
    }

    private void checkForUserIsAuthorComment(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    UPDATE_COMMENT_CONFLICT_MESSAGE, comment.getId());
            throw new ConflictOperationException(UPDATE_COMMENT_CONFLICT_MESSAGE + comment.getId(),
                    COMMENT_OWNER_CONFLICT_ADVICE);
        }
    }

    private void checkReasonComplaint(ComplaintDto complaintDto) {
        List<String> reasonsList = Stream.of(ComplaintReason.values())
                .map(ComplaintReason::name)
                .collect(Collectors.toList());
        if (!reasonsList.contains(complaintDto.getReason())) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    COMPLAINT_REASON_CONFLICT_MESSAGE, complaintDto.getReason());
            throw new ConflictOperationException(COMPLAINT_REASON_CONFLICT_MESSAGE + complaintDto.getReason(),
                    COMPLAINT_REASON_CONFLICT_ADVICE);
        }
    }

    private void checkForUserIsNotAuthorComment(Comment comment, Long userId) {
        if (comment.getUser().getId().equals(userId)) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    COMPLAINT_AUTHOR_CONFLICT_MESSAGE, userId);
            throw new ConflictOperationException(COMPLAINT_AUTHOR_CONFLICT_MESSAGE + userId,
                    COMPLAINT_AUTHOR_CONFLICT_ADVICE);
        }
    }

    private void checkEventStatus(Event event) {
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    COMMENT_EVENT_STATUS_CONFLICT_MESSAGE, event.getId());
            throw new ConflictOperationException(COMMENT_EVENT_STATUS_CONFLICT_MESSAGE + event.getId(),
                    COMMENT_EVENT_STATUS_CONFLICT_ADVICE);
        }
    }

    private void addCommentsCountToEvent(Event event) {
        // Получаем количество комментариев, если не нашли, значит комментариев 0:
        Long commentsCount = commentRepository.getUniqueCommentsCountByEventId(event.getId()).orElse(0L);
        // добавляем просмотры к событию:
        event.setCommentsCount(commentsCount);
    }
}
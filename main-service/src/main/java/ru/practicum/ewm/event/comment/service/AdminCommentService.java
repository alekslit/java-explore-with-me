package ru.practicum.ewm.event.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.comment.Comment;
import ru.practicum.ewm.event.comment.CommentRepository;
import ru.practicum.ewm.event.comment.complaint.Complaint;
import ru.practicum.ewm.event.comment.complaint.ComplaintReason;
import ru.practicum.ewm.event.comment.complaint.ComplaintRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.NotFoundException.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCommentService {
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;

    /*--------------------Основные методы--------------------*/
    public String deleteComment(Long eventId, Long commentId) {
        log.debug("Попытка удалить объект Comment по его commentId.");
        // Проверим событие:
        getEventById(eventId);
        // Проверим комментарий:
        getCommentById(commentId);
        // удаляем комментарий:
        commentRepository.deleteById(commentId);

        return String.format("Комментарий с commentId = %d, удалён.", commentId);
    }

    public List<Comment> getAllComments(List<Long> users,
                                        List<Long> events,
                                        String rangeStart,
                                        String rangeEnd,
                                        Integer from,
                                        Integer size) {
        log.debug("Попытка получить список объектов Comment по фильтрам.");
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return commentRepository.getAllComments(users, events, parseToLocalDateTime(rangeStart),
                parseToLocalDateTime(rangeEnd), pageRequest).getContent();
    }

    public List<Complaint> getAllComplaints(List<Long> users,
                                            List<Long> comments,
                                            List<String> reasons,
                                            String rangeStart,
                                            String rangeEnd,
                                            Integer from,
                                            Integer size) {
        log.debug("Попытка получить список объектов Complaint по фильтрам.");
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return complaintRepository.getAllComplaints(users, comments, mapToComplaintReason(reasons),
                parseToLocalDateTime(rangeStart), parseToLocalDateTime(rangeEnd), pageRequest).getContent();
    }

    /*---------------Вспомогательные методы---------------*/
    private void getEventById(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, eventId);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId, EVENT_NOT_FOUND_ADVICE);
        });
    }

    private void getCommentById(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), COMMENT_NOT_FOUND_MESSAGE, commentId);
            return new NotFoundException(COMMENT_NOT_FOUND_MESSAGE + commentId, COMMENT_NOT_FOUND_ADVICE);
        });
    }

    private LocalDateTime parseToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (time != null) {
            return LocalDateTime.parse(time, formatter);
        } else {
            return null;
        }
    }

    private List<ComplaintReason> mapToComplaintReason(List<String> reasonList) {
        if (reasonList == null) {
            return null;
        }
        return reasonList.stream()
                .map(ComplaintReason::valueOf)
                .collect(Collectors.toList());
    }
}
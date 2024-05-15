package ru.practicum.ewm.event.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.comment.complaint.ComplaintDto;
import ru.practicum.ewm.event.comment.complaint.ComplaintMapper;
import ru.practicum.ewm.event.comment.service.AdminCommentService;
import ru.practicum.ewm.event.comment.service.PrivateCommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final PrivateCommentService privateService;
    private final AdminCommentService adminService;

    /*--------------------Основные User (Private) методы--------------------*/
    // сохранение комментария к событию:
    @PostMapping("users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto,
                                  @PathVariable Long userId,
                                  @PathVariable Long eventId) {
        return CommentMapper.mapToCommentDto(privateService.saveComment(commentDto, userId, eventId));
    }

    // изменение комментария автором:
    @PatchMapping("users/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@Valid @RequestBody CommentDto commentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commentId) {
        return CommentMapper.mapToCommentDto(privateService.updateComment(commentDto, userId, eventId, commentId));
    }

    // удаление комментария его автором:
    @DeleteMapping("users/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteComment(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @PathVariable Long commentId) {
        return privateService.deleteComment(userId, eventId, commentId);
    }

    // жалоба на комментарий пользователя:
    @PostMapping("users/{userId}/events/comments/{commentId}/complaint")
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintDto saveComplaint(@Valid @RequestBody ComplaintDto complaintDto,
                                      @PathVariable Long userId,
                                      @PathVariable Long commentId) {
        return ComplaintMapper.mapToComplaintDto(privateService.saveComplaint(complaintDto, userId, commentId));
    }

    /*--------------------Основные Admin методы--------------------*/
    // удаление комментария администратором (из-за жалобы):
    @DeleteMapping("admin/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteComment(@PathVariable Long eventId,
                                @PathVariable Long commentId) {
        return adminService.deleteComment(eventId, commentId);
    }

    // поиск комментариев по фильтрам:
    @GetMapping("admin/events/comments")
    public List<CommentDto> getAllComments(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size) {

        return CommentMapper.mapToCommentDto(adminService
                .getAllComments(users, events, rangeStart, rangeEnd, from, size));
    }

    // поиск жалоб по фильтрам:
    @GetMapping("admin/events/comments/complaints")
    public List<ComplaintDto> getAllComplaints(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> comments,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size) {

        return ComplaintMapper.mapToComplaintDto(adminService
                .getAllComplaints(users, comments, rangeStart, rangeEnd, from, size));
    }
}
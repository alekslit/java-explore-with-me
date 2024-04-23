package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.service.AdminEventService;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.event.service.PublicEventService;
import ru.practicum.ewm.exception.NotAvailableException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.exception.NotAvailableException.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AdminEventService adminService;
    private final PrivateEventService privateService;
    private final PublicEventService publicService;

    /*--------------------Основные User (Private) методы--------------------*/
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@Valid @RequestBody NewEventDto eventDto,
                                  @PathVariable Long userId) {
        checkEventDateTime(eventDto.getEventDate());
        return EventMapper.mapToEventFullDto(privateService.saveEvent(eventDto, userId));
    }

    @GetMapping("/users/{userId}/events")
    public List<EventFullDto> getAllUserEvent(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size) {
        return EventMapper.mapToEventFullDto(privateService.getAllUserEvent(userId, from, size));
    }

    // По этому эндпоинту получаем событие с комментариями:
    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        return privateService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventRequest eventRequest,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        checkEventDateTime(eventRequest.getEventDate());
        return EventMapper.mapToEventFullDto(privateService.updateEvent(eventRequest, userId, eventId));
    }

    /*--------------------Основные Admin методы--------------------*/
    @GetMapping("/admin/events")
    public List<EventFullDto> getAllEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size) {

        return EventMapper.mapToEventFullDto(adminService
                .getAllEvents(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventRequest eventRequest,
                                    @PathVariable Long eventId) {
        checkEventDateTime(eventRequest.getEventDate());
        return EventMapper.mapToEventFullDto(adminService.updateEvent(eventRequest, eventId));
    }

    /*--------------------Основные Public методы--------------------*/
    @GetMapping("/events")
    public List<EventShortDto> getAllPublishedEvent(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size,
            HttpServletRequest request) {
        checkRequestDateTime(rangeStart, rangeEnd);

        return EventMapper.mapToEventShortDto(publicService
                .getAllPublishedEvent(text, categories, paid, rangeStart, rangeEnd,
                        onlyAvailable, sort, from, size, request));
    }

    // По этому эндпоинту получаем событие с комментариями:
    @GetMapping("/events/{id}")
    public EventFullDto getPublishedEventById(@PathVariable Long id,
                                              HttpServletRequest request) {
        return publicService.getPublishedEventById(id, request);
    }

    /*--------------------Вспомогательные методы (валидация запроса)--------------------*/
    private void checkEventDateTime(String dateTime) {
        if (dateTime == null) {
            return;
        }
        LocalDateTime eventDateTime = LocalDateTime.parse(dateTime, formatter);
        if (eventDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            log.debug("{}: {}{}.", NotAvailableException.class.getSimpleName(),
                    NOT_AVAILABLE_EVENT_DATE_TIME_MESSAGE, eventDateTime);
            throw new NotAvailableException(NOT_AVAILABLE_EVENT_DATE_TIME_MESSAGE + eventDateTime,
                    NOT_AVAILABLE_EVENT_DATE_TIME_ADVICE);
        }
    }

    private void checkRequestDateTime(String start, String end) {
        if (start == null || end == null) {
            return;
        }
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        if (startTime.isAfter(endTime)) {
            log.debug("{}: {}", NotAvailableException.class.getSimpleName(), NOT_AVAILABLE_DATE_TIME_MESSAGE);
            throw new NotAvailableException(NOT_AVAILABLE_DATE_TIME_MESSAGE, NOT_AVAILABLE_DATE_TIME_ADVICE);
        }
    }
}
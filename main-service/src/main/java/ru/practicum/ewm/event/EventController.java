package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewOrUpdateEventDto;
import ru.practicum.ewm.event.service.AdminEventService;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.event.service.PublicEventService;
import ru.practicum.ewm.exception.ForbiddenDateTimeException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.exception.ForbiddenDateTimeException.FORBIDDEN_EVENT_DATE_TIME_ADVICE;
import static ru.practicum.ewm.exception.ForbiddenDateTimeException.FORBIDDEN_EVENT_DATE_TIME_MESSAGE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final AdminEventService adminService;
    private final PrivateEventService privateService;
    private final PublicEventService publicService;

    /*--------------------Основные User (Private) методы--------------------*/
    @PostMapping("/users/{userId}/events")
    public EventFullDto saveEvent(@Valid @RequestBody NewOrUpdateEventDto eventDto,
                                  @PathVariable Long userId) {
        checkDateTime(eventDto.getEventDate());
        return EventMapper.mapToEventFullDto(privateService.saveEvent(eventDto, userId));
    }

    @GetMapping("/users/{userId}/events")
    public List<EventFullDto> getAllUserEvent(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return privateService.getAllUserEvent(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        return privateService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody NewOrUpdateEventDto eventDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        checkDateTime(eventDto.getEventDate());
        return privateService.updateEvent(eventDto, userId, eventId);
    }

    /*--------------------Основные Admin методы--------------------*/
    @GetMapping("/admin/events")
    public List<EventFullDto> getAllEvents(@RequestParam List<Long> users,
                                           @RequestParam List<String> states,
                                           @RequestParam List<Long> categories,
                                           @RequestParam String rangeStart,
                                           @RequestParam String rangeEnd,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {

        return adminService.getAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody NewOrUpdateEventDto eventDto,
                                    @PathVariable Long eventId) {
        return adminService.updateEvent(eventDto, eventId);
    }

    /*--------------------Основные Public методы--------------------*/
    @GetMapping("/events")
    public List<EventShortDto> getAllPublishedEvent(@RequestParam(required = false) String text,
                                                    @RequestParam(required = false) List<String> categories,
                                                    @RequestParam(required = false) Boolean paid,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                    @RequestParam(required = false) String sort,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    HttpServletRequest request) {
        return publicService.getAllPublishedEvent(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getPublishedEventById(@PathVariable Long id,
                                              HttpServletRequest request) {
        return publicService.getPublishedEventById(id, request);
    }

    /*--------------------Вспомогательные методы--------------------*/
    private void checkDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        if (localDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            log.debug("{}: {}{}.", ForbiddenDateTimeException.class.getSimpleName(),
                    FORBIDDEN_EVENT_DATE_TIME_MESSAGE, localDateTime);
            throw new ForbiddenDateTimeException(FORBIDDEN_EVENT_DATE_TIME_MESSAGE + localDateTime,
                    FORBIDDEN_EVENT_DATE_TIME_ADVICE);
        }
    }
}
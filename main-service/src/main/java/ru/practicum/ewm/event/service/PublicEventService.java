package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.StatDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.comment.CommentMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.exception.NotAvailableException;
import ru.practicum.ewm.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_EVENT_SORTED_ADVICE;
import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_EVENT_SORTED_MESSAGE;
import static ru.practicum.ewm.exception.NotFoundException.EVENT_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.EVENT_NOT_FOUND_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatClient client;

    private final EventRepository eventRepository;

    /*--------------------Основные методы--------------------*/
    public List<Event> getAllPublishedEvent(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            String rangeStart,
                                            String rangeEnd,
                                            Boolean onlyAvailable,
                                            String sort,
                                            Integer from,
                                            Integer size,
                                            HttpServletRequest request) {
        log.debug("Попытка получить список объектов Event для публичного просмотра.");
        // если в запросе не указан диапазон дат [rangeStart-rangeEnd],
        // то нужно выгружать события, которые произойдут позже текущей даты и времени:
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = formatter.format(LocalDateTime.now());
        }
        // получаем события:
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> eventList = eventRepository.getAllPublishedEvent(text, categories, paid,
                parseToLocalDateTime(rangeStart), parseToLocalDateTime(rangeEnd),
                onlyAvailable ? onlyAvailable : null, pageRequest).getContent();
        // сортировка:
        eventList = sortedEventList(eventList, sort);
        // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
        // нужно сохранить в сервисе статистики:
        saveStats(request);

        return eventList;
    }

    public EventFullDto getPublishedEventById(Long id, HttpServletRequest request) {
        log.debug("Попытка получить опубликованный объект Event по его id.");
        // получаем объект вместе с комментариями:
        Event event = getEventWithCommentsByIdAndState(id, EventStatus.PUBLISHED);
        // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
        // нужно сохранить в сервисе статистики:
        saveStats(request);
        // обновим информацию о просмотрах по этому событию в БД:
        addViewsToEvent(event);
        eventRepository.save(event);
        // Готовим dto объект:
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
        eventFullDto.setComments(CommentMapper.mapToCommentDto(event.getComments()));

        return eventFullDto;
    }

    /*--------------------Вспомогательные методы--------------------*/
    private LocalDateTime parseToLocalDateTime(String time) {
        if (time != null) {
            return LocalDateTime.parse(time, formatter);
        } else {
            return null;
        }
    }

    private void addViewsToEvent(Event event) {
        // собираем uri события для запроса количества просмотров:
        String eventUri = "/events/" + event.getId();
        // получаем количество просмотров:
        Long viewsCount = client.getUniqueViewsByUri(eventUri);
        // добавляем просмотры к событию:
        event.setViews(viewsCount);
    }

    private List<Event> sortedEventList(List<Event> eventList, String sort) {
        if (sort == null) {
            return eventList;
        }
        switch (sort) {
            case "EVENT_DATE":
                return eventList.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            case "VIEWS":
                return eventList.stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .collect(Collectors.toList());
            case "COMMENTS":
                // добавим возможность сортировки по количеству уникальных комментариев:
                return eventList.stream()
                        .sorted(Comparator.comparing(Event::getCommentsCount).reversed())
                        .collect(Collectors.toList());
            default:
                log.debug("{}: {}{}.", NotAvailableException.class.getSimpleName(),
                        NOT_AVAILABLE_EVENT_SORTED_MESSAGE, sort);
                throw new NotAvailableException(NOT_AVAILABLE_EVENT_SORTED_MESSAGE + sort,
                        NOT_AVAILABLE_EVENT_SORTED_ADVICE);
        }
    }

    private void saveStats(HttpServletRequest request) {
        log.debug("client ip: {}, endpoint path: {}", request.getRemoteAddr(), request.getRequestURI());
        client.saveStat(StatDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build());
    }

    private Event getEventWithCommentsByIdAndState(Long eventId, EventStatus state) {
        return eventRepository.getEventWithCommentsByIdAndStateIsOptional(eventId, state)
                .orElseThrow(() -> {
                    log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, eventId);
                    return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId, EVENT_NOT_FOUND_ADVICE);
                });
    }
}
package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.StatDto;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.NotFoundException.EVENT_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.EVENT_NOT_FOUND_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventService implements EventService {
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

    public Event getPublishedEventById(Long id, HttpServletRequest request) {
        log.debug("Попытка получить опубликованный объект Event по его id.");
        // получаем объект:
        Event event = eventRepository.findByIdAndState(id, EventStatus.PUBLISHED).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, id);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + id, EVENT_NOT_FOUND_ADVICE);
        });
        // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
        // нужно сохранить в сервисе статистики:
        saveStats(request);
        // обновим информацию о просмотрах по этому событию в БД:
        event = addViewsToEvent(event);
        event = eventRepository.save(event);

        return event;
    }

    /*--------------------Вспомогательные методы--------------------*/
    private LocalDateTime parseToLocalDateTime(String time) {
        if (time != null) {
            return LocalDateTime.parse(time, formatter);
        } else {
            return null;
        }
    }

    private Event addViewsToEvent(Event event) {
        String statsStart = formatter.format(LocalDateTime.now().minusYears(50));
        String statsEnd = formatter.format(LocalDateTime.now().plusHours(1));
        // собираем uri события для запроса количества просмотров:
        String eventUri = "/events/" + event.getId();
        // получаем статистику просмотров:
        List<ViewStats> statsList = client
                .getStats(statsStart, statsEnd, new ArrayList<>(List.of(eventUri)), true);
        // добавляем просмотры к событию:
        event.setViews(!statsList.isEmpty() ? statsList.get(0).getHits() : event.getViews());

        return event;
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
        }

        return eventList;
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
}
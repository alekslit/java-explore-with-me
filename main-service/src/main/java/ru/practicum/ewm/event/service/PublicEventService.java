package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.StatDto;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventParticipationCount;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.ParticipationRequestRepository;

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
    private static final String STATS_START = formatter.format(LocalDateTime.now().minusYears(50));
    private static final String START_END = formatter.format(LocalDateTime.now());

    private final StatClient client;

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    /*--------------------Основные методы--------------------*/
    public List<EventShortDto> getAllPublishedEvent(String text,
                                                    List<String> categories,
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
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "1.");
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = formatter.format(LocalDateTime.now());
        }
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "2.");
        // получаем события:
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "3.");
        List<Event> eventList = eventRepository.getAllPublishedEvent(text, categories, paid,
                parseToLocalDateTime(rangeStart), parseToLocalDateTime(rangeEnd),
                onlyAvailable ? onlyAvailable : null, pageRequest).getContent();
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "4.");
        // подготовим dto для ответа:
        List<EventShortDto> eventDtoList = EventMapper.mapToEventShortDto(eventList);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "5.");
        eventDtoList = addViewsAndParticipantCountToEvent(eventDtoList);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "6.");
        eventDtoList = sortedEventList(eventDtoList, sort);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "7.");
        // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
        // нужно сохранить в сервисе статистики:
        saveStats(request);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "8.");

        return eventDtoList;
    }

    public EventFullDto getPublishedEventById(Long id, HttpServletRequest request) {
        log.debug("Попытка получить опубликованный объект Event по его id.");
        // получаем объект:
        Event event = eventRepository.findByIdAndState(id, "PUBLISHED").orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, id);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + id, EVENT_NOT_FOUND_ADVICE);
        });
        // готовим dto для ответа:
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
        eventFullDto = addViewsAndParticipantCountToEvent(eventFullDto);
        // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
        // нужно сохранить в сервисе статистики:
        saveStats(request);

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

    private List<EventShortDto> addViewsAndParticipantCountToEvent(List<EventShortDto> eventList) {
        eventList = addViewsToEvent(addParticipantCountToEvent(eventList));

        return eventList;
    }

    private EventFullDto addViewsAndParticipantCountToEvent(EventFullDto eventDto) {
        eventDto = addViewsToEvent(addParticipantCountToEvent(eventDto));

        return eventDto;
    }

    private List<EventShortDto> addViewsToEvent(List<EventShortDto> eventList) {
        // собираем uris событий для запроса количества просмотров:
        List<String> eventUris = eventList.stream()
                .map(event -> "events/" + event.getId())
                .collect(Collectors.toList());
        // получаем статистику просмотров:
        List<ViewStats> statsList = client.getStats(STATS_START, START_END, eventUris, false)/*.getBody()*/;
        // добавляем просмотры к событиям:
        eventList = eventList.stream()
                .map(event -> {
                    String eventId = event.getId().toString();
                    for (ViewStats stats : statsList) {
                        String uriId = stats.getUri().substring(stats.getUri().length() - 1);
                        if (eventId.equals(uriId)) {
                            event.setViews(stats.getHits());
                            return event;
                        }
                    }
                    return event;
                })
                .collect(Collectors.toList());

        return eventList;
    }

    private EventFullDto addViewsToEvent(EventFullDto eventFullDto) {
        // собираем uri события для запроса количества просмотров:
        String eventUri = "events/" + eventFullDto.getId();
        // получаем статистику просмотров:
        List<ViewStats> statsList = client
                .getStats(null, null, new ArrayList<>(List.of(eventUri)), false)/*.getBody()*/;
        // добавляем просмотры к событию:
        if (!statsList.isEmpty()) {
            eventFullDto.setViews(statsList.get(0).getHits());
        }

        return eventFullDto;
    }

    private List<EventShortDto> addParticipantCountToEvent(List<EventShortDto> eventList) {
        // собираем id-шники событий для запроса количества участников:
        List<Long> eventUris = eventList.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());
        // получаем количество участников:
        List<EventParticipationCount> participationCounts = participationRequestRepository
                .getEventsParticipationCount(eventUris);
        // добавляем количество участников событию:
        eventList = eventList.stream()
                .map(event -> {
                    for (EventParticipationCount count : participationCounts) {
                        if (event.getId().equals(count.getEventId())) {
                            event.setConfirmedRequests(count.getParticipationCount());
                            return event;
                        }
                    }
                    return event;
                })
                .collect(Collectors.toList());

        return eventList;
    }

    private EventFullDto addParticipantCountToEvent(EventFullDto eventDto) {
        // получаем количество участников:
        Integer participantCount = participationRequestRepository.getCountConfirmedRequests(eventDto.getId());
        // добавляем количество участников событию:
        eventDto.setConfirmedRequests(participantCount);

        return eventDto;
    }

    private List<EventShortDto> sortedEventList(List<EventShortDto> eventList, String sort) {
        switch (sort) {
            case "EVENT_DATE":
                return eventList.stream()
                        .sorted(Comparator.comparing(EventShortDto::getEventDate))
                        .collect(Collectors.toList());
            case "VIEWS":
                return eventList.stream()
                        .sorted(Comparator.comparing(EventShortDto::getViews))
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
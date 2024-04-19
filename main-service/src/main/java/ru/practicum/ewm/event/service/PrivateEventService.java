package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventParticipationCount;
import ru.practicum.ewm.event.dto.NewOrUpdateEventDto;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.event.status.EventStatusForUser;
import ru.practicum.ewm.exception.ConflictOperationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.ParticipationRequestRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.ConflictOperationException.*;
import static ru.practicum.ewm.exception.NotFoundException.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrivateEventService implements EventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String STATS_START = formatter.format(LocalDateTime.now().minusYears(50));
    private static final String STATS_END = formatter.format(LocalDateTime.now());

    private final StatClient client;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    /*--------------------Основные методы--------------------*/
    public Event saveEvent(NewOrUpdateEventDto eventDto, Long userId) {
        log.debug("Попытка сохранить новый объект Event.");
        // проверяем существует ли пользователь:
        User user = getUserById(userId);
        // проверяем существует ли категория события:
        Category category = getCategoryById(eventDto.getCategory());
        // сохраняем событие:
        Event event = eventRepository.save(EventMapper.mapToEvent(eventDto, user, category));

        return event;
    }

    public List<EventFullDto> getAllUserEvent(Long userId, Integer from, Integer size) {
        log.debug("Попытка получить список объектов Event пользователя.");
        // проверяем существует ли пользователь:
        getUserById(userId);
        // запрашиваем события:
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> eventList = eventRepository.findAllByInitiatorId(userId, pageRequest).getContent();
        // готовим dto для ответа:
        List<EventFullDto> eventDtoList = EventMapper.mapToEventFullDto(eventList);
        if (eventDtoList.isEmpty()) {
            return eventDtoList;
        }

        return addViewsAndParticipantCountToEvent(eventDtoList);
    }

    public EventFullDto getUserEventById(Long userId, Long eventId) {
        log.debug("Попытка получить Event пользователя по его eventId.");
        // проверяем существует ли пользователь:
        getUserById(userId);
        // проверяем существует ли событие:
        Event event = getEventById(eventId);
        // готовим dto для ответа:
        EventFullDto eventDto = EventMapper.mapToEventFullDto(event);
        eventDto = addViewsAndParticipantCountToEvent(eventDto);

        return eventDto;
    }

    public EventFullDto updateEvent(NewOrUpdateEventDto eventDto, Long userId, Long eventId) {
        log.debug("Попытка изменить информацию об объекте Event пользователем.");
        // проверяем существует ли пользователь:
        getUserById(userId);
        // проверим, существует или событие с таким id:
        Event event = getEventById(eventId);
        // проверяем существует ли категория события:
        Category category;
        if (eventDto.getCategory() != null) {
            category = getCategoryById(eventDto.getCategory());
        } else {
            category = null;
        }
        // получаем количество участников в событии:
        Integer confirmedRequests = participationRequestRepository.getCountConfirmedRequests(eventId);
        // обновляем объект:
        event = updateEventObject(event, eventDto, category, confirmedRequests);
        // сохраняем событие:
        event = eventRepository.save(event);
        // собираем dto объект для ответа (+ просмотры и участники):
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
        eventFullDto.setConfirmedRequests(confirmedRequests);
        eventFullDto = addViewsToEvent(eventFullDto);

        return eventFullDto;
    }

    /*--------------------Вспомогательные методы--------------------*/
    private User getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), USER_NOT_FOUND_MESSAGE, userId);
            return new NotFoundException(USER_NOT_FOUND_MESSAGE + userId, USER_NOT_FOUND_ADVICE);
        });

        return user;
    }

    private Category getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), CATEGORY_NOT_FOUND_MESSAGE, catId);
            return new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE + catId, CATEGORY_NOT_FOUND_ADVICE);
        });

        return category;
    }

    private Event getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, eventId);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId, EVENT_NOT_FOUND_ADVICE);
        });

        return event;
    }

    private List<EventFullDto> addViewsAndParticipantCountToEvent(List<EventFullDto> eventList) {
        eventList = addViewsToEvent(addParticipantCountToEvent(eventList));

        return eventList;
    }

    private EventFullDto addViewsAndParticipantCountToEvent(EventFullDto eventDto) {
        eventDto = addViewsToEvent(addParticipantCountToEvent(eventDto));

        return eventDto;
    }

    private List<EventFullDto> addViewsToEvent(List<EventFullDto> eventList) {
        // собираем uris событий для запроса количества просмотров:
        List<String> eventUris = eventList.stream()
                .map(event -> "events/" + event.getId())
                .collect(Collectors.toList());
        // получаем статистику просмотров:
        List<ViewStats> statsList = client.getStats(STATS_START, STATS_END, eventUris, false)/*.getBody()*/;
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

    private List<EventFullDto> addParticipantCountToEvent(List<EventFullDto> eventList) {
        // собираем id-шники событий для запроса количества участников:
        List<Long> eventUris = eventList.stream()
                .map(EventFullDto::getId)
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

    private Event updateEventObject(Event event,
                                    NewOrUpdateEventDto eventDto,
                                    Category category,
                                    Integer participantCount) {
        // изменить можно только отмененные события или события в состоянии ожидания модерации:
        if (event.getState().equals(EventStatus.PUBLISHED)) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_UPDATE_EVENT_MESSAGE, event.getId());
            throw new ConflictOperationException(CONFLICT_UPDATE_EVENT_MESSAGE + event.getId(),
                    EVENT_STATUS_CONFLICT_ADVICE);
        }
        // annotation:
        event.setAnnotation(eventDto.getAnnotation() != null ? eventDto.getAnnotation() : event.getAnnotation());
        // category:
        event.setCategory(category != null ? category : event.getCategory());
        // description:
        event.setDescription(eventDto.getDescription() != null ? eventDto.getDescription() : event.getDescription());
        // eventDate:
        event.setEventDate(eventDto.getEventDate() != null ?
                LocalDateTime.parse(eventDto.getEventDate(), formatter) : event.getEventDate());
        // location:
        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }
        // paid:
        event.setPaid(eventDto.getPaid() != null ? eventDto.getPaid() : event.getPaid());
        // participantLimit:
        event = updateEventParticipantLimit(event, eventDto, participantCount);
        // requestModeration:
        event.setRequestModeration(eventDto.getRequestModeration() != null ?
                eventDto.getRequestModeration() : event.getRequestModeration());
        // title:
        event.setTitle(eventDto.getTitle() != null ? eventDto.getTitle() : event.getTitle());
        // state:
        event = updateEventState(event, eventDto);

        return event;
    }

    private Event updateEventParticipantLimit(Event event, NewOrUpdateEventDto eventDto, Integer participantCount) {
        if (eventDto.getParticipantLimit() != null &&
                eventDto.getParticipantLimit() != 0 &&
                eventDto.getParticipantLimit() < participantCount) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_UPDATE_EVENT_MESSAGE, event.getId());
            throw new ConflictOperationException(CONFLICT_UPDATE_EVENT_MESSAGE + event.getId(),
                    LOW_PARTICIPANT_LIMIT_ADVICE + String.format("participantLimit = %d < participantCount = %d",
                            eventDto.getParticipantLimit(), participantCount));
        } else if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        return event;
    }

    private Event updateEventState(Event event, NewOrUpdateEventDto eventDto) {
        switch (EventStatusForUser.valueOf(eventDto.getStateAction())) {
            case SEND_TO_REVIEW:
                    event.setState(EventStatus.PENDING);
                    return event;
            case CANCEL_REVIEW:
                    event.setState(EventStatus.CANCELED);
                    return event;

        }

        return event;
    }
}
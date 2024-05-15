package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.event.status.EventStatusForAdmin;
import ru.practicum.ewm.exception.NotAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.conflict.ConflictOperationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_EVENT_UPDATE_STATUS_ADVICE_TO_ADMIN;
import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_EVENT_UPDATE_STATUS_MESSAGE;
import static ru.practicum.ewm.exception.NotFoundException.*;
import static ru.practicum.ewm.exception.conflict.ConflictOperationException.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminEventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    /*--------------------Основные методы--------------------*/
    public List<Event> getAllEvents(List<Long> users,
                                    List<String> states,
                                    List<Long> categories,
                                    String rangeStart,
                                    String rangeEnd,
                                    Integer from,
                                    Integer size) {
        log.debug("Попытка получить список объектов Event по фильтрам.");
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return eventRepository.getAllEvents(users, mapToEventStatus(states), categories,
                parseToLocalDateTime(rangeStart), parseToLocalDateTime(rangeEnd), pageRequest).getContent();
    }

    public Event updateEvent(UpdateEventRequest eventRequest, Long eventId) {
        log.debug("Попытка изменить информацию об объекте Event.");
        // проверим, существует или событие с таким id:
        Event event = getEventById(eventId);
        // проверяем существует ли категория события:
        Category category;
        if (eventRequest.getCategory() != null) {
            category = getCategoryById(eventRequest.getCategory());
        } else {
            category = null;
        }
        // обновляем объект:
        updateEventObject(event, eventRequest, category);

        // сохраняем событие:
        return eventRepository.save(event);
    }

    /*--------------------Вспомогательные методы--------------------*/
    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, eventId);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId, EVENT_NOT_FOUND_ADVICE);
        });
    }

    private Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), CATEGORY_NOT_FOUND_MESSAGE, catId);
            return new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE + catId, CATEGORY_NOT_FOUND_ADVICE);
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

    private void updateEventObject(Event event,
                                   UpdateEventRequest eventRequest,
                                   Category category) {
        // annotation:
        event.setAnnotation(eventRequest.getAnnotation() != null ?
                eventRequest.getAnnotation() : event.getAnnotation());
        // category:
        event.setCategory(category != null ? category : event.getCategory());
        // description:
        event.setDescription(eventRequest.getDescription() != null ?
                eventRequest.getDescription() : event.getDescription());
        // eventDate:
        event.setEventDate(eventRequest.getEventDate() != null ?
                LocalDateTime.parse(eventRequest.getEventDate(), formatter) : event.getEventDate());
        // location:
        if (eventRequest.getLocation() != null) {
            event.setLat(eventRequest.getLocation().getLat());
            event.setLon(eventRequest.getLocation().getLon());
        }
        // paid:
        event.setPaid(eventRequest.getPaid() != null ? eventRequest.getPaid() : event.getPaid());
        // participantLimit:
        updateEventParticipantLimit(event, eventRequest);
        // requestModeration:
        event.setRequestModeration(eventRequest.getRequestModeration() != null ?
                eventRequest.getRequestModeration() : event.getRequestModeration());
        // title:
        event.setTitle(eventRequest.getTitle() != null ? eventRequest.getTitle() : event.getTitle());
        // state:
        updateEventState(event, eventRequest);
    }

    private void updateEventParticipantLimit(Event event, UpdateEventRequest eventRequest) {
        if (eventRequest.getParticipantLimit() != null &&
                eventRequest.getParticipantLimit() != 0 &&
                eventRequest.getParticipantLimit() < event.getConfirmedRequests()) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_UPDATE_EVENT_MESSAGE, event.getId());
            throw new ConflictOperationException(CONFLICT_UPDATE_EVENT_MESSAGE + event.getId(),
                    LOW_PARTICIPANT_LIMIT_ADVICE + String.format("participantLimit = %d < participantLimit = %d",
                            eventRequest.getParticipantLimit(), event.getParticipantLimit()));
        } else if (eventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequest.getParticipantLimit());
        }

        // available (возможность для аренды):
        updateEventAvailable(event);

    }

    private void updateEventState(Event event, UpdateEventRequest eventRequest) {
        // если обновляем данные события, без изменения его статуса:
        if (eventRequest.getStateAction() == null) {
            return;
        }
        switch (EventStatusForAdmin.valueOf(eventRequest.getStateAction())) {
            // если пытаемся опубликовать:
            case PUBLISH_EVENT:
                // дата начала изменяемого события должна быть не ранее чем за час от даты публикации:
                // событие можно публиковать, только если оно в состоянии ожидания публикации:
                if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(1)) &&
                        event.getState().equals(EventStatus.PENDING)) {
                    event.setState(EventStatus.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    return;
                } else {
                    log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                            CONFLICT_UPDATE_EVENT_MESSAGE, event.getId());
                    throw new ConflictOperationException(CONFLICT_UPDATE_EVENT_MESSAGE + event.getId(),
                            PUBLISHED_CONFLICT_ADVICE);
                }

                // если отклоняем:
            case REJECT_EVENT:
                // событие можно отклонить, только если оно еще не опубликовано:
                if (!event.getState().equals(EventStatus.PUBLISHED)) {
                    event.setState(EventStatus.CANCELED);
                    return;
                } else {
                    log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                            CONFLICT_UPDATE_EVENT_MESSAGE, event.getId());
                    throw new ConflictOperationException(CONFLICT_UPDATE_EVENT_MESSAGE + event.getId(),
                            CANCELED_CONFLICT_ADVICE);
                }
            default:
                // некорректное значение:
                log.debug("{}: {}{}.", NotAvailableException.class.getSimpleName(),
                        NOT_AVAILABLE_EVENT_UPDATE_STATUS_MESSAGE, eventRequest.getStateAction());
                throw new NotAvailableException(NOT_AVAILABLE_EVENT_UPDATE_STATUS_MESSAGE +
                        eventRequest.getStateAction(), NOT_AVAILABLE_EVENT_UPDATE_STATUS_ADVICE_TO_ADMIN);
        }
    }

    private List<EventStatus> mapToEventStatus(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        return stringList.stream()
                .map(EventStatus::valueOf)
                .collect(Collectors.toList());
    }

    private void updateEventAvailable(Event event) {
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            event.setAvailable(false);
        }
    }
}
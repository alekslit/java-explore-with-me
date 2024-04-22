package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.event.status.EventStatusForUser;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.conflict.ConflictOperationException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.exception.NotFoundException.*;
import static ru.practicum.ewm.exception.conflict.ConflictOperationException.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrivateEventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    /*--------------------Основные методы--------------------*/
    public Event saveEvent(NewEventDto eventDto, Long userId) {
        log.debug("Попытка сохранить новый объект Event.");
        // проверяем существует ли пользователь:
        User user = getUserById(userId);
        // проверяем существует ли категория события:
        Category category = getCategoryById(eventDto.getCategory());

        // сохраняем событие:
        return eventRepository.save(EventMapper.mapToEvent(eventDto, user, category));
    }

    public List<Event> getAllUserEvent(Long userId, Integer from, Integer size) {
        log.debug("Попытка получить список объектов Event пользователя.");
        // проверяем существует ли пользователь:
        getUserById(userId);
        // запрашиваем события:
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return eventRepository.findAllByInitiatorId(userId, pageRequest).getContent();
    }

    public Event getUserEventById(Long userId, Long eventId) {
        log.debug("Попытка получить Event пользователя по его eventId.");
        // проверяем существует ли пользователь:
        getUserById(userId);

        // проверяем существует ли событие:
        return getEventById(eventId);
    }

    public Event updateEvent(UpdateEventRequest eventRequest, Long userId, Long eventId) {
        log.debug("Попытка изменить информацию об объекте Event пользователем.");
        // проверяем существует ли пользователь:
        getUserById(userId);
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
    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), USER_NOT_FOUND_MESSAGE, userId);
            return new NotFoundException(USER_NOT_FOUND_MESSAGE + userId, USER_NOT_FOUND_ADVICE);
        });
    }

    private Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), CATEGORY_NOT_FOUND_MESSAGE, catId);
            return new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE + catId, CATEGORY_NOT_FOUND_ADVICE);
        });
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, eventId);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId, EVENT_NOT_FOUND_ADVICE);
        });
    }

    private void updateEventObject(Event event,
                                   UpdateEventRequest eventRequest,
                                   Category category) {
        // изменить можно только отмененные события или события в состоянии ожидания модерации:
        if (event.getState().equals(EventStatus.PUBLISHED)) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_UPDATE_EVENT_MESSAGE, event.getId());
            throw new ConflictOperationException(CONFLICT_UPDATE_EVENT_MESSAGE + event.getId(),
                    EVENT_STATUS_CONFLICT_ADVICE);
        }
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
                            eventRequest.getParticipantLimit(), event.getConfirmedRequests()));
        } else if (eventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequest.getParticipantLimit());
        }

        // available (возможность для аренды):
        updateEventAvailable(event);
    }

    private void updateEventState(Event event, UpdateEventRequest eventRequest) {
        if (eventRequest.getStateAction() == null) {
            return;
        }
        switch (EventStatusForUser.valueOf(eventRequest.getStateAction())) {
            case SEND_TO_REVIEW:
                event.setState(EventStatus.PENDING);
                return;
            case CANCEL_REVIEW:
                event.setState(EventStatus.CANCELED);
                return;
            default:
        }
    }

    private void updateEventAvailable(Event event) {
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            event.setAvailable(false);
        }
    }
}
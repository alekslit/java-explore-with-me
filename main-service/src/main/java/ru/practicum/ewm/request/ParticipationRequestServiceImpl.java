package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.exception.NotAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.conflict.AlreadyExistException;
import ru.practicum.ewm.exception.conflict.ConflictOperationException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_REQUEST_STATUS_ADVICE;
import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_REQUEST_STATUS_MESSAGE;
import static ru.practicum.ewm.exception.NotFoundException.*;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_PARTICIPATION_REQUEST_ADVICE;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_PARTICIPATION_REQUEST_MESSAGE;
import static ru.practicum.ewm.exception.conflict.ConflictOperationException.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /*--------------------Основные User (Private) методы--------------------*/
    @Override
    public ParticipationRequest saveParticipationRequest(Long userId, Long eventId) {
        log.debug("Попытка сохранить новый объект ParticipationRequest.");
        // проверим, существует ли пользователь:
        User requester = getUserById(userId);
        // проверим есть ли событие с таким id:
        Event event = getEventById(eventId);
        // проверим запрос на конфликты с событием:
        checkEventOnConflict(event, userId, eventId);
        // сохраняем объект:
        ParticipationRequest participationRequest = ParticipationRequestMapper
                .mapToParticipationRequest(requester, event);
        try {
            participationRequest = participationRequestRepository.save(participationRequest);
        } catch (DataIntegrityViolationException e) {
            log.debug("{}: {} userId = {}, eventId = {}.", AlreadyExistException.class.getSimpleName(),
                    DUPLICATE_PARTICIPATION_REQUEST_MESSAGE, userId, eventId);
            throw new AlreadyExistException(DUPLICATE_PARTICIPATION_REQUEST_MESSAGE +
                    String.format(" userId = %d, eventId = %d.", userId, eventId),
                    DUPLICATE_PARTICIPATION_REQUEST_ADVICE);
        }
        // проверим нужно ли обновить количество участников в событии и доступность для участия:
        checkEventConfirmedRequestAndAvailable(participationRequest);

        return participationRequest;
    }

    @Override
    public ParticipationRequest cancelParticipationRequest(Long userId, Long requestId) {
        log.debug("Попытка отменить запрос на участие в событии (ParticipationRequest).");
        // проверим, существует ли пользователь:
        getUserById(userId);
        // проверим, существует ли запрос на участие:
        ParticipationRequest participationRequestDb = getParticipationRequestById(requestId);
        // меняем статус и обновляем данные:
        ParticipationRequest resultParticipationRequest = participationRequestDb;
        resultParticipationRequest.setStatus(ParticipationRequestStatus.REJECTED);
        resultParticipationRequest = participationRequestRepository.save(participationRequestDb);
        resultParticipationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        // проверим нужно ли обновить количество участников в событии и доступность для участия:
        checkEventConfirmedRequestAndAvailable(participationRequestDb);

        return resultParticipationRequest;
    }

    @Override
    public List<ParticipationRequest> getUserParticipationRequests(Long userId) {
        log.debug("Попытка получить список объектов ParticipationRequest по id пользователя.");
        // проверим, существует ли пользователь:
        getUserById(userId);

        return participationRequestRepository.findAllByRequesterId(userId);
    }

    @Override
    public List<ParticipationRequest> getEventOwnerParticipationRequests(Long userId, Long eventId) {
        log.debug("Попытка получить список объектов ParticipationRequest автором события.");
        return participationRequestRepository.findAllByEventId(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateParticipationRequestsStatus(EventRequestStatusUpdateRequest request,
                                                                            Long userId,
                                                                            Long eventId) {
        log.debug("Попытка обновить статусы объектов ParticipationRequest события пользователя.");
        // проверим, существует ли пользователь:
        getUserById(userId);
        // проверим есть ли событие с таким id:
        Event event = getEventById(eventId);
        // получим заявки, которые нужно изменить:
        List<ParticipationRequest> participationRequestList = participationRequestRepository
                .findAllByEventIdAndIdIn(eventId, request.getRequestIds());
        // статус можно изменить только у заявок, находящихся в состоянии ожидания:
        checkRequestStatusOnConflict(participationRequestList, request.getRequestIds());
        // обновляем заявки:
        List<ParticipationRequest> updateRequests = updateParticipationRequestObject(request,
                event, participationRequestList);
        participationRequestRepository.saveAll(updateRequests);

        return ParticipationRequestMapper.mapToStatusUpdateResult(updateRequests);
    }

    /*--------------------Вспомогательные методы--------------------*/
    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), USER_NOT_FOUND_MESSAGE, userId);
            return new NotFoundException(USER_NOT_FOUND_MESSAGE + userId, USER_NOT_FOUND_ADVICE);
        });
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), EVENT_NOT_FOUND_MESSAGE, eventId);
            return new NotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId, EVENT_NOT_FOUND_ADVICE);
        });
    }

    private ParticipationRequest getParticipationRequestById(Long requestId) {
        return participationRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(),
                            PARTICIPATION_REQUEST_NOT_FOUND_MESSAGE, requestId);
                    return new NotFoundException(PARTICIPATION_REQUEST_NOT_FOUND_MESSAGE + requestId,
                            PARTICIPATION_REQUEST_NOT_FOUND_ADVICE);
                });
    }

    private void checkEventOnConflict(Event event, Long userId, Long eventId) {
        // инициатор события не может добавить запрос на участие в своём событии:
        if (event.getInitiator().getId().equals(userId)) {
            log.debug("{}: {} userId = {}, eventId = {}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_SAVE_PARTICIPATION_REQUEST_MESSAGE, userId, eventId);
            throw new ConflictOperationException(CONFLICT_SAVE_PARTICIPATION_REQUEST_MESSAGE +
                    String.format(" userId = %d, eventId = %d.", userId, eventId), USER_IS_OWNER_ADVICE);
        }
        // нельзя участвовать в неопубликованном событии:
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            log.debug("{}: {} userId = {}, eventId = {}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_SAVE_PARTICIPATION_REQUEST_MESSAGE, userId, eventId);
            throw new ConflictOperationException(CONFLICT_SAVE_PARTICIPATION_REQUEST_MESSAGE +
                    String.format(" userId = %d, eventId = %d.", userId, eventId), EVENT_IS_NOT_PUBLISHED_ADVICE);
        }
        // проверим лимит запросов на участие:
        Long confirmedRequests = participationRequestRepository.getCountConfirmedRequests(eventId);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit().equals(confirmedRequests)) {
            log.debug("{}: {} userId = {}, eventId = {}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_SAVE_PARTICIPATION_REQUEST_MESSAGE, userId, eventId);
            throw new ConflictOperationException(CONFLICT_SAVE_PARTICIPATION_REQUEST_MESSAGE +
                    String.format(" userId = %d, eventId = %d.", userId, eventId), MAX_EVENT_PARTICIPANT_COUNT_ADVICE);
        }
    }

    private void checkRequestStatusOnConflict(List<ParticipationRequest> requestList, List<Long> requestIds) {
        List<ParticipationRequestStatus> statusList = requestList.stream()
                .map(ParticipationRequest::getStatus)
                .collect(Collectors.toList());
        if (statusList.contains(ParticipationRequestStatus.CONFIRMED) ||
                statusList.contains(ParticipationRequestStatus.REJECTED)) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_UPDATE_PARTICIPATION_REQUEST_MESSAGE, requestIds);
            throw new ConflictOperationException(CONFLICT_UPDATE_PARTICIPATION_REQUEST_MESSAGE +
                    requestIds, REQUEST_STATUS_CONFLICT_ADVICE);
        }
    }

    private List<ParticipationRequest> updateParticipationRequestObject(EventRequestStatusUpdateRequest request,
                                                                        Event event,
                                                                        List<ParticipationRequest> requestList) {
        // собираем список заявок для обновления:
        List<ParticipationRequest> updateRequests = new ArrayList<>();
        switch (ParticipationRequestStatus.valueOf(request.getStatus())) {
            case CONFIRMED:
                // нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие:
                if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                    log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                            CONFLICT_UPDATE_PARTICIPATION_REQUEST_MESSAGE, request.getRequestIds());
                    throw new ConflictOperationException(CONFLICT_UPDATE_PARTICIPATION_REQUEST_MESSAGE +
                            request.getRequestIds(), PARTICIPANT_COUNT_IS_MAX_ADVICE);
                } else {
                    Long countParticipants = event.getConfirmedRequests();
                    for (ParticipationRequest req : requestList) {
                        if (countParticipants < event.getParticipantLimit()) {
                            req.setStatus(ParticipationRequestStatus.CONFIRMED);
                            updateRequests.add(req);
                            ++countParticipants;
                        } else {
                            // если при подтверждении данной заявки, лимит заявок для события исчерпан,
                            // то все неподтверждённые заявки необходимо отклонить:
                            req.setStatus(ParticipationRequestStatus.REJECTED);
                            updateRequests.add(req);
                        }
                        // обновим информацию о количестве участников и доступности события:
                        updateEventParticipantsAndAvailable(event, countParticipants);
                    }

                    return updateRequests;
                }

            case REJECTED:
                updateRequests = requestList.stream()
                        .peek(req -> req.setStatus(ParticipationRequestStatus.REJECTED))
                        .collect(Collectors.toList());
                return updateRequests;
            default:
                log.debug("{}: {}{}.", NotAvailableException.class.getSimpleName(),
                        NOT_AVAILABLE_REQUEST_STATUS_MESSAGE, request.getStatus());
                throw new NotAvailableException(NOT_AVAILABLE_REQUEST_STATUS_MESSAGE + request.getStatus(),
                        NOT_AVAILABLE_REQUEST_STATUS_ADVICE);
        }
    }

    private void updateEventParticipantsAndAvailable(Event event, Long countParticipants) {
        event.setConfirmedRequests(countParticipants);
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            event.setAvailable(false);
        } else {
            event.setAvailable(true);
        }

        eventRepository.save(event);
    }

    private void checkEventConfirmedRequestAndAvailable(ParticipationRequest participationRequest) {
        if (participationRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            Long confirmedRequest = participationRequestRepository
                    .getCountConfirmedRequests(participationRequest.getEvent().getId());
            // обновим количество участников в событии:
            Event event = participationRequest.getEvent();
            event.setConfirmedRequests(confirmedRequest);
            // проверим доступность аренды (available):
            if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                event.setAvailable(false);
            } else {
                event.setAvailable(true);
            }

            eventRepository.save(event);
        }
    }
}
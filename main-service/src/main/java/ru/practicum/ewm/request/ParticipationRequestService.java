package ru.practicum.ewm.request;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequest saveParticipationRequest(Long userId, Long eventId);

    ParticipationRequest cancelParticipationRequest(Long userId, Long requestId);

    List<ParticipationRequest> getUserParticipationRequests(Long userId);

    List<ParticipationRequest> getEventOwnerParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateParticipationRequestsStatus(EventRequestStatusUpdateRequest request,
                                                                     Long userId,
                                                                     Long eventId);
}
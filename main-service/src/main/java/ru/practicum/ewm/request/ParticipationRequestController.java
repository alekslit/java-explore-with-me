package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
@Validated
public class ParticipationRequestController {
    private final ParticipationRequestService service;

    /*--------------------Основные User (Private) методы--------------------*/
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveParticipationRequest(@PathVariable Long userId,
                                                            @RequestParam Long eventId) {
        return ParticipationRequestMapper
                .mapToParticipationRequestDto(service.saveParticipationRequest(userId, eventId));
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        return ParticipationRequestMapper
                .mapToParticipationRequestDto(service.cancelParticipationRequest(userId, requestId));
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserParticipationRequests(@PathVariable Long userId) {
        return ParticipationRequestMapper
                .mapToParticipationRequestDto(service.getUserParticipationRequests(userId));
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventOwnerParticipationRequests(@PathVariable Long userId,
                                                                            @PathVariable Long eventId) {
        return ParticipationRequestMapper
                .mapToParticipationRequestDto(service.getEventOwnerParticipationRequests(userId, eventId));
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequestsStatus(
            @RequestBody EventRequestStatusUpdateRequest request,
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return service.updateParticipationRequestsStatus(request, userId, eventId);
    }
}
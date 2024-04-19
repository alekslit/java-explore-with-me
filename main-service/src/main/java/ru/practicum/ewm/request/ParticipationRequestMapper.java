package ru.practicum.ewm.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipationRequestMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequest mapToParticipationRequest(User requester, Event event) {
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .status(event.getRequestModeration().equals(false) || event.getParticipantLimit() == 0 ?
                        ParticipationRequestStatus.CONFIRMED : ParticipationRequestStatus.PENDING)
                .created(LocalDateTime.now())
                .build();

        return participationRequest;
    }

    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest participationRequest) {
        ParticipationRequestDto participationRequestDto = ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus().toString())
                .created(formatter.format(participationRequest.getCreated()))
                .build();

        return participationRequestDto;
    }

    public static List<ParticipationRequestDto> mapToParticipationRequestDto(List<ParticipationRequest> requestList) {
        List<ParticipationRequestDto> requestDtoList = requestList.stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());

        return requestDtoList;
    }

    public static EventRequestStatusUpdateResult mapToStatusUpdateResult(List<ParticipationRequest> requestList) {
        List<ParticipationRequest> confirmedRequests = requestList.stream()
                .filter(req -> req.getStatus().equals(ParticipationRequestStatus.CONFIRMED))
                .collect(Collectors.toList());
        List<ParticipationRequest> rejectedRequests = requestList.stream()
                .filter(req -> req.getStatus().equals(ParticipationRequestStatus.REJECTED))
                .collect(Collectors.toList());
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(ParticipationRequestMapper.mapToParticipationRequestDto(confirmedRequests))
                .rejectedRequests(ParticipationRequestMapper.mapToParticipationRequestDto(rejectedRequests))
                .build();

        return result;
    }
}
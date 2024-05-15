package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/*----------Объект для ответа на запрос изменения статуса запроса на участие----------*/
@Getter
@ToString
@Builder(toBuilder = true)
public final class EventRequestStatusUpdateResult {
    // подтверждённые запросы:
    private final List<ParticipationRequestDto> confirmedRequests;
    // отклонённые запросы:
    private final List<ParticipationRequestDto> rejectedRequests;
}
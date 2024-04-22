package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/*----------Объект для ответа на запрос изменения статуса запроса на участие----------*/
@Data
@Builder(toBuilder = true)
public class EventRequestStatusUpdateResult {
    // подтверждённые запросы:
    private final List<ParticipationRequestDto> confirmedRequests;
    // отклонённые запросы:
    private final List<ParticipationRequestDto> rejectedRequests;
}
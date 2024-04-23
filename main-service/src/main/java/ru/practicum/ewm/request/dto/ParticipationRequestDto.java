package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
public final class ParticipationRequestDto {
    // идентификатор заявки на участие в событии:
    private final Long id;
    // идентификатор пользователя, который отправил заявку:
    private final Long requester;
    // идентификатор события, на участие в котором составлена заявка:
    private final Long event;
    // статус заявки:
    private final String status;
    // дата и время создания заявки:
    private final String created;
}
package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ParticipationRequestDto {
    // идентификатор заявки на участие в событии:
    private Long id;
    // идентификатор пользователя, который отправил заявку:
    private Long requester;
    // идентификатор события, на участие в котором составлена заявка:
    private Long event;
    // статус заявки:
    private String status;
    // дата и время создания заявки:
    private String created;
}
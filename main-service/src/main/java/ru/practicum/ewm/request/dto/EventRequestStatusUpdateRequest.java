package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/*----------Объект для принятия запроса на изменение статуса запроса на участие----------*/
@Getter
@ToString
@AllArgsConstructor
public final class EventRequestStatusUpdateRequest {
    // идентификаторы запросов на участие в событии текущего пользователя:
    private final List<Long> requestIds;

    // новый статус запроса на участие в событии текущего пользователя:
    private final String status;
}
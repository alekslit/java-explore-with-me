package ru.practicum.ewm.request.dto;

import lombok.Data;

import java.util.List;

/*----------Объект для принятия запроса на изменение статуса запроса на участие----------*/
@Data
public class EventRequestStatusUpdateRequest {
    // идентификаторы запросов на участие в событии текущего пользователя:
    private List<Long> requestIds;

    // новый статус запроса на участие в событии текущего пользователя:
    private String status;
}
package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.category.CategoryDto;

@Getter
@ToString
@Builder(toBuilder = true)
public final class EventShortDto {
    // идентификатор события:
    private final Long id;
    // краткое описание события:
    private final String annotation;
    // категория события:
    private final CategoryDto category;
    // количество одобренных заявок на участие в данном событии:
    private final Long confirmedRequests;
    // дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"):
    private final String eventDate;
    // пользователь, создатель события:
    private final UserShortDto initiator;
    // нужно ли оплачивать участие в событии:
    private final Boolean paid;
    // заголовок события:
    private final String title;
    // количество просмотрев события:
    private final Long views;
}
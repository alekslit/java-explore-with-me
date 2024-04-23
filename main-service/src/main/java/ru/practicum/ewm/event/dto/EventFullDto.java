package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.category.CategoryDto;

@Getter
@ToString
@Builder(toBuilder = true)
public final class EventFullDto {
    // идентификатор события:
    private final Long id;
    // краткое описание события:
    private final String annotation;
    // категория события:
    private final CategoryDto category;
    // количество одобренных заявок на участие в данном событии:
    private final Long confirmedRequests;
    // дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss"):
    private final String createdOn;
    // полное описание события:
    private final String description;
    // дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"):
    private final String eventDate;
    // пользователь, создатель события:
    private final UserShortDto initiator;
    // широта и долгота места проведения события:
    private final Location location;
    // нужно ли оплачивать участие в событии:
    private final Boolean paid;
    // ограничение на количество участников, значение 0 - означает отсутствие ограничения:
    private final Long participantLimit;
    // дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss"):
    private final String publishedOn;
    // нужна ли пре-модерация заявок на участие:
    private final Boolean requestModeration;
    // состояние события:
    private final String state;
    // заголовок события:
    private final String title;
    // количество просмотров события:
    private final Long views;
}
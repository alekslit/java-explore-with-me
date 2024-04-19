package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.category.CategoryDto;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class EventFullDto {
    // идентификатор события:
    private Long id;
    // краткое описание события:
    private String annotation;
    // категория события:
    private CategoryDto category;
    // количество одобренных заявок на участие в данном событии:
    private Integer confirmedRequests;
    // дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss"):
    private String createdOn;
    // полное описание события:
    private String description;
    // дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"):
    private String eventDate;
    // пользователь, создатель события:
    private UserShortDto initiator;
    // широта и долгота места проведения события:
    private Location location;
    // нужно ли оплачивать участие в событии:
    private Boolean paid;
    // ограничение на количество участников, значение 0 - означает отсутствие ограничения:
    private Integer participantLimit;
    // дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss"):
    private String publishedOn;
    // нужна ли пре-модерация заявок на участие:
    private Boolean requestModeration;
    // состояние события:
    private String state;
    // заголовок события:
    private String title;
    // количество просмотрев события:
    private Long views;
}
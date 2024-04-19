package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.category.CategoryDto;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class EventShortDto {
    // идентификатор события:
    private Long id;
    // краткое описание события:
    private String annotation;
    // категория события:
    private CategoryDto category;
    // количество одобренных заявок на участие в данном событии:
    private Integer confirmedRequests;
    // дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"):
    private String eventDate;
    // пользователь, создатель события:
    private UserShortDto initiator;
    // нужно ли оплачивать участие в событии:
    private Boolean paid;
    // заголовок события:
    private String title;
    // количество просмотрев события:
    private Long views;
}
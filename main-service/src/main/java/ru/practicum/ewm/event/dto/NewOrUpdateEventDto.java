package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class NewOrUpdateEventDto {
    // краткое описание события:
    @Size(min = 20, max = 2000, message = "Слишком длинное или слишком короткое краткое описание события " +
            "(annotation). Длинна краткого описания события должна быть от {min} до {max} символов.")
    private String annotation;

    // id категории события:
    private Long category;

    // полное описание события:
    @Size(min = 20, max = 7000, message = "Слишком длинное или слишком короткое полное описание события " +
            "(description). Длинна полного описания события должна быть от {min} до {max} символов.")
    private String description;

    // дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"):
    private String eventDate;

    // широта и долгота места проведения события:
    private Location location;

    // нужно ли оплачивать участие в событии:
    private Boolean paid;

    // ограничение на количество участников, значение 0 - означает отсутствие ограничения:
    private Integer participantLimit;

    // Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события.
    // Если false - то будут подтверждаться автоматически.:
    private Boolean requestModeration;

    // заголовок события:
    @Size(min = 3, max = 120, message = "Слишком длинный или слишком короткий заголовок события " +
            "(title). Длинна заголовка события должна быть от {min} до {max} символов.")
    private String title;

    // вспомогательный статус события (Admin/User):
    private String stateAction;
}
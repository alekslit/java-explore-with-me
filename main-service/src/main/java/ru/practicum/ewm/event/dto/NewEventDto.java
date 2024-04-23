package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class NewEventDto {
    // краткое описание события:
    @NotBlank(message = "Краткое описание события (annotation) не может быть пустым.")
    @Size(min = 20, max = 2000, message = "Слишком длинное или слишком короткое краткое описание события " +
            "(annotation). Длинна краткого описания события должна быть от {min} до {max} символов.")
    private final String annotation;

    // id категории события:
    private final Long category;

    // полное описание события:
    @NotBlank(message = "Полное описание события (description) не может быть пустым.")
    @Size(min = 20, max = 7000, message = "Слишком длинное или слишком короткое полное описание события " +
            "(description). Длинна полного описания события должна быть от {min} до {max} символов.")
    private final String description;

    // дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"):
    private final String eventDate;

    // широта и долгота места проведения события:
    private final Location location;

    // нужно ли оплачивать участие в событии:
    private final Boolean paid;

    // ограничение на количество участников, значение 0 - означает отсутствие ограничения:
    @PositiveOrZero(message = "Ограничение на количество участников (participantLimit) " +
            "может быть положительным числом или нулём.")
    private final Long participantLimit;

    // Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события.
    // Если false - то будут подтверждаться автоматически.:
    private final Boolean requestModeration;

    // заголовок события:
    @Size(min = 3, max = 120, message = "Слишком длинный или слишком короткий заголовок события " +
            "(title). Длинна заголовка события должна быть от {min} до {max} символов.")
    private final String title;
}
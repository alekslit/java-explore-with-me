package ru.practicum.ewm.event.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
@Builder
public final class CommentDto {
    // идентификатор:
    private final Long id;

    @NotBlank(message = "Текст комментария не может быть пустым.")
    @Size(min = 2, max = 1000, message = "Слишком длинный или слишком короткий текст комментария. " +
            "Длинна комментария должна быть от {min} до {max} символов.")
    // текст комментария:
    private final String text;

    // имя пользователя, который оставил комментарий:
    private final String authorName;

    // id события к которому оставлен комментарий:
    private final Long eventId;

    // дата создания комментария (в формате "yyyy-MM-dd HH:mm:ss"):
    private final String creationDate;
}